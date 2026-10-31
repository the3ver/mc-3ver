package net.frank.mc3ver.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;

// Findet die Transportkarten eines Spielers, auch die in Bündeln, und ändert sie an Ort und Stelle
public final class TransportMapInventory {

    public interface MapItemRef {
        TransportFlameLogic.FlameTarget getTarget();

        void modify(Consumer<ItemStack> change);

        default void updateTarget(TransportFlameLogic.FlameTarget newTarget) {
            modify(stack -> applyTarget(stack, newTarget));
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

    public static List<TransportFlameLogic.FlameTarget> targets(List<MapItemRef> mapRefs) {
        List<TransportFlameLogic.FlameTarget> targets = new ArrayList<>();
        for (MapItemRef ref : mapRefs) {
            targets.add(ref.getTarget());
        }
        return targets;
    }
}
