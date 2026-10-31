package net.frank.mc3ver.tent;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

// Zelt + ein Farbstoff ergibt ein gefaerbtes Zelt, der Inhalt (CONTAINER) bleibt erhalten
public class TentDyeRecipe extends CustomRecipe {

    public static final TentDyeRecipe INSTANCE = new TentDyeRecipe();
    public static final RecipeSerializer<TentDyeRecipe> SERIALIZER =
            new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.unit(INSTANCE));

    public static ItemStack dye(ItemStack tent, DyeColor color) {
        ItemStack result = tent.copyWithCount(1);
        result.set(DataComponents.DYED_COLOR, new DyedItemColor(ExplorerTentLogic.getColorRgb(color.getName())));
        return result;
    }

    private static boolean isTent(ItemStack stack) {
        return stack.is(ModTentItems.EXPLORER_TENT);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findIngredients(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack[] found = findIngredients(input);
        if (found == null) {
            return ItemStack.EMPTY;
        }
        return dye(found[0], found[1].get(DataComponents.DYE));
    }

    // {Zelt, Farbstoff} oder null, wenn das Raster nicht passt
    private static ItemStack[] findIngredients(CraftingInput input) {
        ItemStack tent = null;
        ItemStack dyeStack = null;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isTent(stack) && tent == null) {
                tent = stack;
            } else if (stack.get(DataComponents.DYE) != null && dyeStack == null) {
                dyeStack = stack;
            } else {
                return null;
            }
        }
        if (tent == null || dyeStack == null) {
            return null;
        }
        return new ItemStack[] {tent, dyeStack};
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return SERIALIZER;
    }
}
