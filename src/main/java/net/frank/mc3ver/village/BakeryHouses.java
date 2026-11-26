package net.frank.mc3ver.village;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.frank.mc3ver.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/** Hängt die Bäckerei in die Häuser-Pools der fünf Vanilla-Dörfer, bevor die Welten laden. */
public final class BakeryHouses {

    private BakeryHouses() {}

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> addTo(server.registryAccess()));
    }

    static void addTo(RegistryAccess registries) {
        var pools = registries.lookupOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureProcessorList> mossy =
                registries.lookupOrThrow(Registries.PROCESSOR_LIST).getOrThrow(ProcessorLists.MOSSIFY_10_PERCENT);
        for (String style : BakeryLayoutLogic.STYLES) {
            StructureTemplatePool pool = pools.getValue(Identifier.parse(BakeryLayoutLogic.poolId(style)));
            if (pool == null) continue;
            String template = BakeryLayoutLogic.templateId(style);
            StructurePoolElement bakery = (BakeryLayoutLogic.mossy(style)
                            ? StructurePoolElement.legacy(template, mossy)
                            : StructurePoolElement.legacy(template))
                    .apply(StructureTemplatePool.Projection.RIGID);
            var templates = ((StructureTemplatePoolAccessor) pool).mc3ver$templates();
            // Zweiter Start desselben Servers bekommt dieselben Pools: nicht doppelt eintragen
            if (templates.stream().anyMatch(element -> element.toString().contains(template))) continue;
            for (int i = 0; i < BakeryLayoutLogic.POOL_WEIGHT; i++) templates.add(bakery);
        }
    }
}
