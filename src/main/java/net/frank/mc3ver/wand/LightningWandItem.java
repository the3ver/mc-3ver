package net.frank.mc3ver.wand;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
                    0.05, 0.05, 0.05, 0.02
                );

                float pitch = 1.4f + ratio * 0.6f;
                serverLevel.playSound(
                    null,
                    livingEntity.getX(),
                    livingEntity.getY(),
                    livingEntity.getZ(),
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.PLAYERS,
                    0.4f + ratio * 0.4f,
                    pitch
                );
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof Player player)) {
            return false;
        }

        int chargeTicks = getUseDuration(stack, livingEntity) - timeLeft;
        float chargeRatio = LightningWandLogic.calculateChargeRatio(chargeTicks, LightningWandLogic.MAX_CHARGE_TICKS);
        float chargedBaseDamage = LightningWandLogic.calculateChargedDamage(
            LightningWandLogic.BASE_DAMAGE,
            LightningWandLogic.MAX_DAMAGE_MULTIPLIER,
            chargeTicks,
            LightningWandLogic.MAX_CHARGE_TICKS
        );
        int durabilityCost = LightningWandLogic.calculateDurabilityCost(
            LightningWandLogic.MIN_DURABILITY_COST,
            LightningWandLogic.MAX_DURABILITY_COST,
            chargeTicks,
            LightningWandLogic.MAX_CHARGE_TICKS
        );

        // Sound beim Auslösen (wuchtiger bei höherer Aufladung)
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.LIGHTNING_BOLT_THUNDER,
            SoundSource.PLAYERS,
            0.5f + chargeRatio * 0.5f,
            1.8f - chargeRatio * 0.4f
        );

        player.getCooldowns().addCooldown(stack, LightningWandLogic.COOLDOWN_TICKS);

        if (level instanceof ServerLevel serverLevel) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookAngle = player.getLookAngle();
            Vec3 endPos = eyePos.add(lookAngle.scale(LightningWandLogic.RANGE));

            // Lebende Ziele im Umkreis suchen
            AABB searchBox = player.getBoundingBox().inflate(LightningWandLogic.RANGE);
            List<LivingEntity> potentialTargets = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                searchBox,
                entity -> entity.isAlive() && !entity.isSpectator() && entity != player
            );

            LivingEntity primaryTarget = null;
            double closestAimDistance = Double.MAX_VALUE;

            for (LivingEntity candidate : potentialTargets) {
                Vec3 targetCenter = candidate.position().add(0, candidate.getBbHeight() * 0.5, 0);
                double tolerance = Math.max(1.0, candidate.getBbWidth() * 0.8 + 0.5);

                double aimDist = LightningWandLogic.calculateAimDistance(
                    eyePos.x, eyePos.y, eyePos.z,
                    lookAngle.x, lookAngle.y, lookAngle.z,
                    targetCenter.x, targetCenter.y, targetCenter.z,
                    LightningWandLogic.RANGE,
                    tolerance
                );

                if (aimDist > 0 && aimDist < closestAimDistance) {
                    closestAimDistance = aimDist;
                    primaryTarget = candidate;
                }
            }

            if (primaryTarget != null) {
                LivingEntity currentSource = player;
                LivingEntity currentTarget = primaryTarget;
                Set<UUID> hitEntities = new HashSet<>();
                hitEntities.add(player.getUUID());

                for (int chainIndex = 0; chainIndex < LightningWandLogic.MAX_CHAINS; chainIndex++) {
                    if (currentTarget == null) {
                        break;
                    }

                    hitEntities.add(currentTarget.getUUID());

                    Vec3 startVec = currentSource == player ? player.getEyePosition() : currentSource.position().add(0, currentSource.getBbHeight() * 0.5, 0);
                    Vec3 targetVec = currentTarget.position().add(0, currentTarget.getBbHeight() * 0.5, 0);

                    // Blitz-Partikel-Bogen zwischen Quelle und Ziel
                    spawnLightningArc(serverLevel, startVec, targetVec, chargeRatio);

                    // Schaden berechnen und austeilen (skaliert mit Aufladung)
                    float damage = LightningWandLogic.calculateDamage(
                        chargedBaseDamage,
                        LightningWandLogic.DAMAGE_DECAY,
                        chainIndex
                    );

                    // Echter Blitz-Treffer (lädt Creeper auf, konvertiert Schweine/Villager etc.)
                    net.minecraft.world.entity.LightningBolt bolt = net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT.create(serverLevel, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
                    if (bolt != null) {
                        bolt.setPos(currentTarget.position());
                        bolt.setVisualOnly(true);
                        if (player instanceof ServerPlayer sp) {
                            bolt.setCause(sp);
                        }
                    }
                    currentTarget.thunderHit(serverLevel, bolt);

                    // Zusätzlichen Magieschaden austeilen, falls der Stab-Schaden höher als der Grundblitz (5.0) ist
                    float extraDamage = LightningWandLogic.calculateExtraDamage(damage, LightningWandLogic.THUNDER_HIT_DAMAGE);
                    if (extraDamage > 0 && currentTarget.isAlive()) {
                        currentTarget.hurtServer(serverLevel, serverLevel.damageSources().indirectMagic(player, player), extraDamage);
                    }

                    // Sound und Funken am getroffenen Ziel
                    serverLevel.playSound(
                        null,
                        currentTarget.getX(),
                        currentTarget.getY(),
                        currentTarget.getZ(),
                        SoundEvents.LIGHTNING_BOLT_IMPACT,
                        SoundSource.PLAYERS,
                        0.4f,
                        1.5f + (chainIndex * 0.1f)
                    );
                    int sparkCount = (int) (12 + chargeRatio * 18);
                    serverLevel.sendParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        targetVec.x,
                        targetVec.y,
                        targetVec.z,
                        sparkCount,
                        0.3,
                        0.3,
                        0.3,
                        0.05
                    );

                    // Nächstes Ziel in der Kette finden
                    currentSource = currentTarget;
                    LivingEntity nextTarget = null;
                    double closestDistanceSq = Double.MAX_VALUE;

                    AABB chainBox = currentSource.getBoundingBox().inflate(LightningWandLogic.CHAIN_RADIUS);
                    List<LivingEntity> chainCandidates = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        chainBox,
                        entity -> entity.isAlive() && !entity.isSpectator() && !hitEntities.contains(entity.getUUID())
                    );

                    for (LivingEntity candidate : chainCandidates) {
                        double distSq = currentSource.distanceToSqr(candidate);
                        if (LightningWandLogic.isWithinRange(
                            currentSource.getX(), currentSource.getY(), currentSource.getZ(),
                            candidate.getX(), candidate.getY(), candidate.getZ(),
                            LightningWandLogic.CHAIN_RADIUS
                        ) && distSq < closestDistanceSq) {
                            closestDistanceSq = distSq;
                            nextTarget = candidate;
                        }
                    }

                    currentTarget = nextTarget;
                }

                // Haltbarkeit verringern (skaliert mit Aufladung)
                if (player instanceof ServerPlayer serverPlayer) {
                    stack.hurtAndBreak(
                        durabilityCost,
                        serverLevel,
                        serverPlayer,
                        item -> player.onEquippedItemBroken(item, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
                    );
                }
            } else {
                // Kein Mob anvisiert: Block-Raycast prüfen
                BlockHitResult blockHit = serverLevel.clip(new net.minecraft.world.level.ClipContext(
                    eyePos,
                    endPos,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    player
                ));

                if (blockHit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                    Vec3 hitVec = blockHit.getLocation();
                    net.minecraft.core.BlockPos hitPos = blockHit.getBlockPos();
                    net.minecraft.core.Direction face = blockHit.getDirection();
                    net.minecraft.world.level.block.state.BlockState targetState = serverLevel.getBlockState(hitPos);

                    // Blitzstrahl direkt auf den Block feuern (kein Kettensprung)
                    spawnLightningArc(serverLevel, eyePos, hitVec, chargeRatio);
                    int blockSparks = (int) (15 + chargeRatio * 20);
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, hitVec.x, hitVec.y, hitVec.z, blockSparks, 0.2, 0.2, 0.2, 0.05);
                    serverLevel.playSound(null, hitPos.getX(), hitPos.getY(), hitPos.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 0.5f + chargeRatio * 0.3f, 1.6f);

                    // Brennbarkeit prüfen und ggf. in Brand setzen
                    if (targetState.is(net.minecraft.world.level.block.Blocks.TNT)) {
                        net.minecraft.world.level.block.TntBlock.prime(serverLevel, hitPos);
                        serverLevel.removeBlock(hitPos, false);
                    } else if (targetState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)
                        && !targetState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT)) {
                        serverLevel.setBlock(hitPos, targetState.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, true), 11);
                        serverLevel.playSound(null, hitPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
                    } else if (isFlammableBlock(targetState)) {
                        net.minecraft.core.BlockPos firePos = hitPos.relative(face);
                        if (net.minecraft.world.level.block.BaseFireBlock.canBePlacedAt(serverLevel, firePos, face)) {
                            serverLevel.setBlock(firePos, net.minecraft.world.level.block.BaseFireBlock.getState(serverLevel, firePos), 11);
                            serverLevel.playSound(null, firePos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
                        } else {
                            net.minecraft.core.BlockPos topPos = hitPos.above();
                            if (serverLevel.getBlockState(topPos).isAir()
                                && net.minecraft.world.level.block.BaseFireBlock.canBePlacedAt(serverLevel, topPos, net.minecraft.core.Direction.UP)) {
                                serverLevel.setBlock(topPos, net.minecraft.world.level.block.BaseFireBlock.getState(serverLevel, topPos), 11);
                                serverLevel.playSound(null, topPos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 0.8f, 1.0f);
                            }
                        }
                    }

                    // Haltbarkeit verringern (skaliert mit Aufladung)
                    if (player instanceof ServerPlayer serverPlayer) {
                        stack.hurtAndBreak(
                            durabilityCost,
                            serverLevel,
                            serverPlayer,
                            item -> player.onEquippedItemBroken(item, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND)
                        );
                    }
                } else {
                    // Weder Mob noch Block getroffen (in den Himmel geschossen)
                    spawnLightningArc(serverLevel, eyePos, endPos, chargeRatio);
                }
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    private void spawnLightningArc(ServerLevel level, Vec3 from, Vec3 to, float chargeRatio) {
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

    private boolean isFlammableBlock(net.minecraft.world.level.block.state.BlockState state) {
        if (state.ignitedByLava()) {
            return true;
        }
        return state.is(net.minecraft.tags.BlockTags.LOGS)
            || state.is(net.minecraft.tags.BlockTags.PLANKS)
            || state.is(net.minecraft.tags.BlockTags.LEAVES)
            || state.is(net.minecraft.tags.BlockTags.WOOL)
            || state.is(net.minecraft.tags.BlockTags.WOOL_CARPETS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_SLABS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_STAIRS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_FENCES)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_DOORS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_TRAPDOORS)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_PRESSURE_PLATES)
            || state.is(net.minecraft.tags.BlockTags.WOODEN_BUTTONS)
            || state.is(net.minecraft.tags.BlockTags.FLOWERS)
            || state.is(net.minecraft.world.level.block.Blocks.HAY_BLOCK)
            || state.is(net.minecraft.world.level.block.Blocks.BOOKSHELF)
            || state.is(net.minecraft.world.level.block.Blocks.CHISELED_BOOKSHELF)
            || state.is(net.minecraft.world.level.block.Blocks.BAMBOO)
            || state.is(net.minecraft.world.level.block.Blocks.SCAFFOLDING);
    }
}
