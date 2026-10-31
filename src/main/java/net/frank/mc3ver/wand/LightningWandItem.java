package net.frank.mc3ver.wand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightningWandItem extends Item {

    public LightningWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!LightningWandLogic.canUse(player.getCooldowns().isOnCooldown(stack), player.isSpectator())) {
            return InteractionResult.FAIL;
        }

        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level instanceof ServerLevel serverLevel) {
            int chargeTicks = getUseDuration(stack, livingEntity) - remainingUseDuration;
            float ratio = LightningWandLogic.calculateChargeRatio(chargeTicks, LightningWandLogic.MAX_CHARGE_TICKS);

            // Knisternde Funken beim Aufladen erzeugen (Frequenz steigt mit der Ladung)
            int interval = Math.max(2, 8 - (int) (ratio * 5));
            if (chargeTicks % interval == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        livingEntity.getX() + (serverLevel.getRandom().nextDouble() - 0.5) * 0.6,
                        livingEntity.getEyeY() - 0.1 + (serverLevel.getRandom().nextDouble() - 0.5) * 0.4,
                        livingEntity.getZ() + (serverLevel.getRandom().nextDouble() - 0.5) * 0.6,
                        (int) (1 + ratio * 3),
                        0.05,
                        0.05,
                        0.05,
                        0.02);

                float pitch = 1.4f + ratio * 0.6f;
                serverLevel.playSound(
                        null,
                        livingEntity.getX(),
                        livingEntity.getY(),
                        livingEntity.getZ(),
                        SoundEvents.FURNACE_FIRE_CRACKLE,
                        SoundSource.PLAYERS,
                        0.4f + ratio * 0.4f,
                        pitch);
            }
        }
    }

    // Werte eines Schusses, alle abhängig davon, wie lange aufgeladen wurde
    private record Charge(float ratio, float baseDamage, int durabilityCost) {
        static Charge of(int chargeTicks) {
            return new Charge(
                    LightningWandLogic.calculateChargeRatio(chargeTicks, LightningWandLogic.MAX_CHARGE_TICKS),
                    LightningWandLogic.calculateChargedDamage(
                            LightningWandLogic.BASE_DAMAGE,
                            LightningWandLogic.MAX_DAMAGE_MULTIPLIER,
                            chargeTicks,
                            LightningWandLogic.MAX_CHARGE_TICKS),
                    LightningWandLogic.calculateDurabilityCost(
                            LightningWandLogic.MIN_DURABILITY_COST,
                            LightningWandLogic.MAX_DURABILITY_COST,
                            chargeTicks,
                            LightningWandLogic.MAX_CHARGE_TICKS));
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }

        Charge charge = Charge.of(getUseDuration(stack, livingEntity) - timeLeft);

        // Sound beim Auslösen (wuchtiger bei höherer Aufladung)
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER,
                SoundSource.PLAYERS,
                0.5f + charge.ratio() * 0.5f,
                1.8f - charge.ratio() * 0.4f);

        player.getCooldowns().addCooldown(stack, LightningWandLogic.COOLDOWN_TICKS);

        if (level instanceof ServerLevel serverLevel) {
            fire(stack, serverLevel, player, charge);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    // Erst ein anvisiertes Lebewesen (Kettenblitz), sonst ein Block (anzünden), sonst ein Strahl in die Luft
    private static void fire(ItemStack stack, ServerLevel level, Player player, Charge charge) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookAngle.scale(LightningWandLogic.RANGE));

        LivingEntity primaryTarget = findAimedTarget(level, player, eyePos, lookAngle);
        if (primaryTarget != null) {
            strikeChain(level, player, primaryTarget, charge);
            damageWand(stack, level, player, charge.durabilityCost());
            return;
        }

        BlockHitResult blockHit =
                level.clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            strikeBlock(level, eyePos, blockHit, charge.ratio());
            damageWand(stack, level, player, charge.durabilityCost());
            return;
        }

        // Weder Mob noch Block getroffen (in den Himmel geschossen)
        spawnLightningArc(level, eyePos, endPos, charge.ratio());
    }

    private static LivingEntity findAimedTarget(ServerLevel level, Player player, Vec3 eyePos, Vec3 lookAngle) {
        AABB searchBox = player.getBoundingBox().inflate(LightningWandLogic.RANGE);
        List<LivingEntity> potentialTargets = level.getEntitiesOfClass(
                LivingEntity.class, searchBox, entity -> entity.isAlive() && !entity.isSpectator() && entity != player);
        return LightningWandLogic.pickAimedTarget(potentialTargets, candidate -> {
            Vec3 targetCenter = center(candidate);
            double tolerance = Math.max(1.0, candidate.getBbWidth() * 0.8 + 0.5);
            return LightningWandLogic.calculateAimDistance(
                    eyePos.x,
                    eyePos.y,
                    eyePos.z,
                    lookAngle.x,
                    lookAngle.y,
                    lookAngle.z,
                    targetCenter.x,
                    targetCenter.y,
                    targetCenter.z,
                    LightningWandLogic.RANGE,
                    tolerance);
        });
    }

    private static void strikeChain(ServerLevel level, Player player, LivingEntity primaryTarget, Charge charge) {
        LivingEntity currentSource = player;
        LivingEntity currentTarget = primaryTarget;
        Set<UUID> hitEntities = new HashSet<>();
        hitEntities.add(player.getUUID());

        for (int chainIndex = 0; chainIndex < LightningWandLogic.MAX_CHAINS && currentTarget != null; chainIndex++) {
            hitEntities.add(currentTarget.getUUID());
            Vec3 startVec = currentSource == player ? player.getEyePosition() : center(currentSource);
            strikeEntity(level, player, startVec, currentTarget, chainIndex, charge);

            currentSource = currentTarget;
            currentTarget = findNextChainTarget(level, currentSource, hitEntities);
        }
    }

    private static void strikeEntity(
            ServerLevel level, Player player, Vec3 startVec, LivingEntity target, int chainIndex, Charge charge) {
        Vec3 targetVec = center(target);

        // Blitz-Partikel-Bogen zwischen Quelle und Ziel
        spawnLightningArc(level, startVec, targetVec, charge.ratio());

        // Schaden berechnen und austeilen (skaliert mit Aufladung)
        float damage =
                LightningWandLogic.calculateDamage(charge.baseDamage(), LightningWandLogic.DAMAGE_DECAY, chainIndex);

        // Echter Blitz-Treffer (lädt Creeper auf, konvertiert Schweine/Villager etc.)
        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.setPos(target.position());
            bolt.setVisualOnly(true);
            if (player instanceof ServerPlayer sp) {
                bolt.setCause(sp);
            }
        }
        target.thunderHit(level, bolt);

        // Zusätzlichen Magieschaden austeilen, falls der Stab-Schaden höher als der Grundblitz (5.0) ist
        float extraDamage = LightningWandLogic.calculateExtraDamage(damage, LightningWandLogic.THUNDER_HIT_DAMAGE);
        if (extraDamage > 0 && target.isAlive()) {
            target.hurtServer(level, level.damageSources().indirectMagic(player, player), extraDamage);
        }

        // Sound und Funken am getroffenen Ziel
        level.playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS,
                0.4f,
                1.5f + (chainIndex * 0.1f));
        int sparkCount = (int) (12 + charge.ratio() * 18);
        level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK, targetVec.x, targetVec.y, targetVec.z, sparkCount, 0.3, 0.3, 0.3, 0.05);
    }

    private static LivingEntity findNextChainTarget(ServerLevel level, LivingEntity source, Set<UUID> hitEntities) {
        AABB chainBox = source.getBoundingBox().inflate(LightningWandLogic.CHAIN_RADIUS);
        List<LivingEntity> chainCandidates = level.getEntitiesOfClass(
                LivingEntity.class,
                chainBox,
                entity -> entity.isAlive() && !entity.isSpectator() && !hitEntities.contains(entity.getUUID()));
        return LightningWandLogic.pickNearestInRange(
                chainCandidates, source::distanceToSqr, LightningWandLogic.CHAIN_RADIUS);
    }

    // Blitzstrahl direkt auf den Block (kein Kettensprung), danach anzünden, was brennt
    private static void strikeBlock(ServerLevel level, Vec3 eyePos, BlockHitResult blockHit, float chargeRatio) {
        Vec3 hitVec = blockHit.getLocation();
        BlockPos hitPos = blockHit.getBlockPos();

        spawnLightningArc(level, eyePos, hitVec, chargeRatio);
        int blockSparks = (int) (15 + chargeRatio * 20);
        level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK, hitVec.x, hitVec.y, hitVec.z, blockSparks, 0.2, 0.2, 0.2, 0.05);
        level.playSound(
                null,
                hitPos.getX(),
                hitPos.getY(),
                hitPos.getZ(),
                SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.BLOCKS,
                0.5f + chargeRatio * 0.3f,
                1.6f);

        igniteBlock(level, hitPos, blockHit.getDirection(), level.getBlockState(hitPos));
    }

    private static void igniteBlock(ServerLevel level, BlockPos hitPos, Direction face, BlockState targetState) {
        if (targetState.is(Blocks.TNT)) {
            TntBlock.prime(level, hitPos);
            level.removeBlock(hitPos, false);
        } else if (targetState.hasProperty(BlockStateProperties.LIT)
                && !targetState.getValue(BlockStateProperties.LIT)) {
            level.setBlock(hitPos, targetState.setValue(BlockStateProperties.LIT, true), 11);
            level.playSound(null, hitPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
        } else if (isFlammableBlock(targetState)) {
            BlockPos firePos = hitPos.relative(face);
            if (BaseFireBlock.canBePlacedAt(level, firePos, face)) {
                level.setBlock(firePos, BaseFireBlock.getState(level, firePos), 11);
                level.playSound(null, firePos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
            } else {
                BlockPos topPos = hitPos.above();
                if (level.getBlockState(topPos).isAir() && BaseFireBlock.canBePlacedAt(level, topPos, Direction.UP)) {
                    level.setBlock(topPos, BaseFireBlock.getState(level, topPos), 11);
                    level.playSound(null, topPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
                }
            }
        }
    }

    // Haltbarkeit verringern (skaliert mit Aufladung)
    private static void damageWand(ItemStack stack, ServerLevel level, Player player, int durabilityCost) {
        if (player instanceof ServerPlayer serverPlayer) {
            stack.hurtAndBreak(
                    durabilityCost,
                    level,
                    serverPlayer,
                    item -> player.onEquippedItemBroken(
                            item,
                            player.getUsedItemHand() == InteractionHand.MAIN_HAND
                                    ? EquipmentSlot.MAINHAND
                                    : EquipmentSlot.OFFHAND));
        }
    }

    private static Vec3 center(LivingEntity entity) {
        return entity.position().add(0, entity.getBbHeight() * 0.5, 0);
    }

    private static void spawnLightningArc(ServerLevel level, Vec3 from, Vec3 to, float chargeRatio) {
        Vec3 diff = to.subtract(from);
        double distance = diff.length();
        int particleCount = Math.max(5, (int) (distance * (3 + chargeRatio * 2)));

        for (int i = 0; i <= particleCount; i++) {
            double progress = (double) i / particleCount;
            double x = from.x + diff.x * progress;
            double y = from.y + diff.y * progress;
            double z = from.z + diff.z * progress;

            // Kleine zufällige Zickzack-Abweichung für realistischen Blitzeffekt
            if (i > 0 && i < particleCount) {
                x += (level.getRandom().nextDouble() - 0.5) * 0.25;
                y += (level.getRandom().nextDouble() - 0.5) * 0.25;
                z += (level.getRandom().nextDouble() - 0.5) * 0.25;
            }

            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private static boolean isFlammableBlock(BlockState state) {
        if (state.ignitedByLava()) {
            return true;
        }
        return state.is(BlockTags.LOGS)
                || state.is(BlockTags.PLANKS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(BlockTags.WOODEN_SLABS)
                || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_FENCES)
                || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS)
                || state.is(BlockTags.WOODEN_PRESSURE_PLATES)
                || state.is(BlockTags.WOODEN_BUTTONS)
                || state.is(BlockTags.FLOWERS)
                || state.is(Blocks.HAY_BLOCK)
                || state.is(Blocks.BOOKSHELF)
                || state.is(Blocks.CHISELED_BOOKSHELF)
                || state.is(Blocks.BAMBOO)
                || state.is(Blocks.SCAFFOLDING);
    }
}
