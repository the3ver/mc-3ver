package net.frank.mc3ver.tree;

import net.frank.mc3ver.Mc3verMod;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.level.block.DispenserBlock;

public class PearTreeEntities {

    public static final ResourceKey<EntityType<?>> PEAR_BOAT_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE,
        Mc3verMod.id("pear_boat")
    );

    public static final ResourceKey<EntityType<?>> PEAR_CHEST_BOAT_KEY = ResourceKey.create(
        Registries.ENTITY_TYPE,
        Mc3verMod.id("pear_chest_boat")
    );

    public static final EntityType<Boat> PEAR_BOAT_ENTITY_TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        PEAR_BOAT_KEY,
        EntityType.Builder.<Boat>of((type, level) -> new Boat(type, level, () -> PearTreeItems.PEAR_BOAT), MobCategory.MISC)
            .noLootTable()
            .sized(1.375f, 0.5625f)
            .eyeHeight(0.5625f)
            .clientTrackingRange(10)
            .build(PEAR_BOAT_KEY)
    );

    public static final EntityType<ChestBoat> PEAR_CHEST_BOAT_ENTITY_TYPE = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        PEAR_CHEST_BOAT_KEY,
        EntityType.Builder.<ChestBoat>of((type, level) -> new ChestBoat(type, level, () -> PearTreeItems.PEAR_CHEST_BOAT), MobCategory.MISC)
            .noLootTable()
            .sized(1.375f, 0.5625f)
            .eyeHeight(0.5625f)
            .clientTrackingRange(10)
            .build(PEAR_CHEST_BOAT_KEY)
    );

    public static void register() {
        DispenserBlock.registerBehavior(PearTreeItems.PEAR_BOAT, new BoatDispenseItemBehavior(PEAR_BOAT_ENTITY_TYPE));
        DispenserBlock.registerBehavior(PearTreeItems.PEAR_CHEST_BOAT, new BoatDispenseItemBehavior(PEAR_CHEST_BOAT_ENTITY_TYPE));
    }
}
