package net.frank.mc3ver.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

// Findet die Transportkarten eines Spielers, auch die in Bündeln, und ändert sie an Ort und Stelle
public final class TransportMapInventory {

    public interface MapItemRef {
        TransportFlameLogic.FlameTarget getTarget();

        void modify(Consumer<ItemStack> change);

        default void updateTarget(TransportFlameLogic.FlameTarget newTarget) {
            modify(stack -> applyTarget(stack, newTarget));
        }

        default void rename(String name) {
            modify(stack -> stack.set(DataComponents.CUSTOM_NAME, Component.literal(name)));
        }
    }

    // Gibt allen Karten, die auf die Flamme an dieser Stelle zeigen, ihren Namen
    public static void nameMapsFor(List<MapItemRef> mapRefs, int x, int y, int z, String dimension, String name) {
        for (MapItemRef ref : mapRefs) {
            TransportFlameLogic.FlameTarget target = ref.getTarget();
            if (target != null && target.isAt(x, y, z, dimension)) {
                ref.rename(name);
            }
        }
    }

    private TransportMapInventory() {}

    public static ItemStack createMap(TransportFlameLogic.FlameTarget target) {
        ItemStack mapStack = new ItemStack(ModItems.TRANSPORT_MAP);
        applyTarget(mapStack, target);
        return mapStack;
    }

    public static void applyTarget(ItemStack stack, TransportFlameLogic.FlameTarget target) {
        stack.set(ModDataComponents.FLAME_TARGET, target);
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(target.colorRgb()));
    }

    // Scannt Inventar, Hotbar und Nebenhand; Bündel werden nach Transportkarten durchsucht
    public static List<MapItemRef> scan(Player player) {
        List<MapItemRef> mapRefs = new ArrayList<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack slotStack = player.getInventory().getItem(slot);
            if (slotStack.isEmpty()) continue;

            if (slotStack.is(ModItems.TRANSPORT_MAP)) {
                mapRefs.add(new MapItemRef() {
                    @Override
                    public TransportFlameLogic.FlameTarget getTarget() {
                        return slotStack.get(ModDataComponents.FLAME_TARGET);
                    }

                    @Override
                    public void modify(Consumer<ItemStack> change) {
                        change.accept(slotStack);
                    }
                });
            } else {
                BundleContents bundle = slotStack.get(DataComponents.BUNDLE_CONTENTS);
                if (bundle != null && !bundle.isEmpty()) {
                    List<ItemStackTemplate> templates = bundle.items();
                    for (int i = 0; i < templates.size(); i++) {
                        ItemStackTemplate template = templates.get(i);
                        TransportFlameLogic.FlameTarget target = template.get(ModDataComponents.FLAME_TARGET);
                        if (target != null || template.item().value() == ModItems.TRANSPORT_MAP) {
                            final int templateIndex = i;
                            mapRefs.add(new MapItemRef() {
                                @Override
                                public TransportFlameLogic.FlameTarget getTarget() {
                                    return target;
                                }

                                @Override
                                public void modify(Consumer<ItemStack> change) {
                                    BundleContents currentBundle = slotStack.get(DataComponents.BUNDLE_CONTENTS);
                                    if (currentBundle != null
                                            && templateIndex
                                                    < currentBundle.items().size()) {
                                        List<ItemStackTemplate> updatedTemplates =
                                                new ArrayList<>(currentBundle.items());
                                        ItemStack stackInBundle = updatedTemplates
                                                .get(templateIndex)
                                                .create();
                                        change.accept(stackInBundle);
                                        updatedTemplates.set(
                                                templateIndex, ItemStackTemplate.fromNonEmptyStack(stackInBundle));
                                        slotStack.set(
                                                DataComponents.BUNDLE_CONTENTS, new BundleContents(updatedTemplates));
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
        return mapRefs;
    }

    // Bei vollem Inventar fällt die neue Karte beim Aufstellen zu Boden; dort soll sie den Namen trotzdem bekommen
    public static final double DROPPED_MAP_SEARCH_RADIUS = 16.0;

    public static List<MapItemRef> droppedNear(Level level, BlockPos pos) {
        List<MapItemRef> mapRefs = new ArrayList<>();
        AABB area = new AABB(pos).inflate(DROPPED_MAP_SEARCH_RADIUS);
        for (ItemEntity entity : level.getEntitiesOfClass(
                ItemEntity.class, area, e -> e.getItem().is(ModItems.TRANSPORT_MAP))) {
            mapRefs.add(new MapItemRef() {
                @Override
                public TransportFlameLogic.FlameTarget getTarget() {
                    return entity.getItem().get(ModDataComponents.FLAME_TARGET);
                }

                @Override
                public void modify(Consumer<ItemStack> change) {
                    // Kopie setzen, sonst erkennt die Entity-Synchronisation keine Änderung
                    ItemStack changed = entity.getItem().copy();
                    change.accept(changed);
                    entity.setItem(changed);
                }
            });
        }
        return mapRefs;
    }

    public static List<TransportFlameLogic.FlameTarget> targets(List<MapItemRef> mapRefs) {
        List<TransportFlameLogic.FlameTarget> targets = new ArrayList<>();
        for (MapItemRef ref : mapRefs) {
            targets.add(ref.getTarget());
        }
        return targets;
    }
}
