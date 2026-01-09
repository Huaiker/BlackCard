package blackcard.blackcard.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import blackcard.blackcard.BlackCard;

public class UniversalNBTRecipe extends CustomRecipe {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BlackCard.MODID);

    public static final RegistryObject<RecipeSerializer<UniversalNBTRecipe>> UNIVERSAL_NBT_RECIPE =
            RECIPE_SERIALIZERS.register("universal_nbt_recipe", () -> new RecipeSerializer<>() {
                @Override
                public UniversalNBTRecipe fromJson(ResourceLocation id, JsonObject json) {
                    return new UniversalNBTRecipe(id, CraftingBookCategory.MISC);
                }

                @Override
                public UniversalNBTRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
                    return new UniversalNBTRecipe(id, CraftingBookCategory.MISC);
                }

                @Override
                public void toNetwork(FriendlyByteBuf buf, UniversalNBTRecipe recipe) {}
            });

    // ✅ 使用小驼峰命名（camelCase）
    private static final String NBT_TARGET_ITEM = "targetItem";
    private static final String NBT_TARGET_COUNT = "targetCount";

    public UniversalNBTRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack nbtItem = ItemStack.EMPTY;
        int totalItems = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                totalItems++;
                if (stack.hasTag() && stack.getTag() != null) {
                    if (nbtItem.isEmpty()) {
                        nbtItem = stack;
                    } else {
                        return false; // 多个带 NBT 的物品 → 不匹配
                    }
                }
            }
        }

        return !nbtItem.isEmpty()
                && totalItems == 1
                && isValidNBTItem(nbtItem);
    }

    private boolean isValidNBTItem(ItemStack stack) {
        if (!stack.hasTag()) return false;
        var tag = stack.getTag();
        if (tag == null) return false;

        // ✅ 严格检查小驼峰键名
        return tag.contains(NBT_TARGET_ITEM, net.minecraft.nbt.Tag.TAG_STRING)
                && tag.contains(NBT_TARGET_COUNT, net.minecraft.nbt.Tag.TAG_INT);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack nbtItem = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.hasTag() && isValidNBTItem(stack)) {
                nbtItem = stack;
                break;
            }
        }

        if (nbtItem.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var tag = nbtItem.getTag();
        String targetItemId = tag.getString(NBT_TARGET_ITEM); // "targetItem"
        int targetCount = tag.getInt(NBT_TARGET_COUNT);       // "targetCount"

        ResourceLocation itemId = ResourceLocation.tryParse(targetItemId);
        if (itemId == null) return ItemStack.EMPTY;

        Item targetItem = BuiltInRegistries.ITEM.get(itemId);
        if (targetItem == Items.AIR) return ItemStack.EMPTY;

        int maxStackSize = targetItem.getMaxStackSize();
        int actualCount = Math.min(Math.max(1, targetCount), maxStackSize);

        return new ItemStack(targetItem, actualCount);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.hasTag() && isValidNBTItem(stack)) {
                remaining.set(i, stack.copy()); // 不消耗原物品
                break;
            }
        }

        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return UNIVERSAL_NBT_RECIPE.get();
    }
}