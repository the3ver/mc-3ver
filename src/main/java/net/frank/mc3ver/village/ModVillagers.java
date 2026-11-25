package net.frank.mc3ver.village;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Set;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModVillagers {

    // Gespeichert, an alle Clients synchronisiert (Skin) und bei Zombifizierung und Heilung mitgenommen
    public static final AttachmentType<Boolean> FEMALE = AttachmentRegistry.create(
            Mc3verMod.id("female"),
            builder -> builder.persistent(Codec.BOOL)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                    .copyOnDeath());

    public static final ResourceKey<Block> BAKING_OVEN_KEY =
            ResourceKey.create(Registries.BLOCK, Mc3verMod.id("baking_oven"));

    public static final Block BAKING_OVEN = Registry.register(
            BuiltInRegistries.BLOCK,
            BAKING_OVEN_KEY,
            new BakingOvenBlock(BlockBehaviour.Properties.of()
                    .setId(BAKING_OVEN_KEY)
                    .mapColor(MapColor.COLOR_RED)
                    .strength(2.0f, 6.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)));

    public static final ResourceKey<Item> BAKING_OVEN_ITEM_KEY =
            ResourceKey.create(Registries.ITEM, Mc3verMod.id("baking_oven"));

    public static final Item BAKING_OVEN_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            BAKING_OVEN_ITEM_KEY,
            new BlockItem(
                    BAKING_OVEN,
                    new Item.Properties().setId(BAKING_OVEN_ITEM_KEY).useBlockDescriptionPrefix()));

    public static final ResourceKey<PoiType> BAKER_POI_KEY =
            ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Mc3verMod.id("baker"));

    // Ein Ticket, Reichweite 1 wie die Vanilla-Arbeitsblöcke; PoiTypes.register macht Fabric zugänglich
    public static final PoiType BAKER_POI = PoiTypes.register(
            BuiltInRegistries.POINT_OF_INTEREST_TYPE,
            BAKER_POI_KEY,
            Set.copyOf(BAKING_OVEN.getStateDefinition().getPossibleStates()),
            1,
            1);

    public static final ResourceKey<VillagerProfession> BAKER_KEY =
            ResourceKey.create(Registries.VILLAGER_PROFESSION, Mc3verMod.id("baker"));

    public static final VillagerProfession BAKER = Registry.register(
            BuiltInRegistries.VILLAGER_PROFESSION,
            BAKER_KEY,
            new VillagerProfession(
                    Component.translatable("entity.mc3ver.villager.baker"),
                    ModVillagers::isBakerJobSite,
                    ModVillagers::isBakerJobSite,
                    ImmutableSet.of(Items.WHEAT),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_BUTCHER,
                    bakerTradeSets()));

    private ModVillagers() {}

    private static Int2ObjectMap<ResourceKey<TradeSet>> bakerTradeSets() {
        Int2ObjectMap<ResourceKey<TradeSet>> sets = new Int2ObjectOpenHashMap<>();
        for (int level = 1; level <= 5; level++) {
            sets.put(level, ResourceKey.create(Registries.TRADE_SET, Mc3verMod.id("baker/level_" + level)));
        }
        return sets;
    }

    private static boolean isBakerJobSite(Holder<PoiType> poi) {
        return poi.is(BAKER_POI_KEY);
    }

    // Für die Jobsuche (AcquirePoi, YieldJobSite): den Backofen bekommen nur Bewohnerinnen
    public static boolean mayWorkAt(Entity worker, Holder<PoiType> poi) {
        boolean female = worker instanceof Villager villager && isFemale(villager);
        return BakerJobLogic.canTakeJob(female, isBakerJobSite(poi));
    }

    public static boolean isFemale(Villager villager) {
        return villager.getAttachedOrElse(FEMALE, false);
    }

    public static void register() {
        BakeryHouses.register();
        // Neue, geborene und schon gespeicherte Bewohner ohne Flag würfeln einmal
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Villager villager && !villager.hasAttached(FEMALE)) {
                villager.setAttached(FEMALE, VillagerGenderLogic.isFemale(villager.getUUID()));
            }
        });
    }
}
