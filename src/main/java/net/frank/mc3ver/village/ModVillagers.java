package net.frank.mc3ver.village;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.frank.mc3ver.Mc3verMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.npc.villager.Villager;

public final class ModVillagers {

    // Gespeichert, an alle Clients synchronisiert (Skin) und bei Zombifizierung und Heilung mitgenommen
    public static final AttachmentType<Boolean> FEMALE = AttachmentRegistry.create(
            Mc3verMod.id("female"),
            builder -> builder.persistent(Codec.BOOL)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
                    .copyOnDeath());

    private ModVillagers() {}

    public static boolean isFemale(Villager villager) {
        return villager.getAttachedOrElse(FEMALE, false);
    }

    public static void register() {
        // Neue, geborene und schon gespeicherte Bewohner ohne Flag würfeln einmal
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Villager villager && !villager.hasAttached(FEMALE)) {
                villager.setAttached(FEMALE, VillagerGenderLogic.isFemale(villager.getUUID()));
            }
        });
    }
}
