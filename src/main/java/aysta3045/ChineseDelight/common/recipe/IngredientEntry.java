package aysta3045.ChineseDelight.common.recipe;

import net.minecraft.world.item.crafting.Ingredient;

public class IngredientEntry {
    private final Ingredient ingredient;
    private final int count;

    public IngredientEntry(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public int getCount() {
        return count;
    }
}