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
import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.item.LocalizedBlackCard;
import blackcard.blackcard.item.ModBlackCard;
import blackcard.blackcard.item.TagBlackCard;
import blackcard.blackcard.util.BlackCardUtil;


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
                        return false; // Multiple items with NBT -> no match
                    }
                }
            }
        }

        if (nbtItem.isEmpty() || totalItems != 1) return false;

        // Item black card (multi-item): ensure targetItem is synced
        if (nbtItem.getItem() == ItemInit.BLACK_CARD.get()) {
            LocalizedBlackCard.ensureTargetItemSynced(nbtItem);
        }

        // Tag black card: ensure targetItem is synced
        if (nbtItem.getItem() == ItemInit.TAG_BLACK_CARD.get()) {
            TagBlackCard.ensureTargetItemSynced(nbtItem);
        }

        // Mod black card: ensure targetItem is synced
        if (nbtItem.getItem() == ItemInit.MOD_BLACK_CARD.get()) {
            ModBlackCard.ensureTargetItemSynced(nbtItem);
        }

        return isValidNBTItem(nbtItem);
    }

    private boolean isValidNBTItem(ItemStack stack) {
        if (!stack.hasTag()) return false;
        var tag = stack.getTag();
        if (tag == null) return false;

        // Check for v2.0 NBT keys (bcitem, bctag, bcmodid)
        if (tag.contains(BlackCardUtil.NBT_ITEM)) return true;
        if (tag.contains(BlackCardUtil.NBT_TAG)) return true;
        if (tag.contains(BlackCardUtil.NBT_MODID)) return true;

        return false;
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

        // All card types: resolve from targetItem (synced by matches() or cycleTargetItem())
        return resolveFromTargetItem(nbtItem);
    }

    /**
     * Resolve output from targetItem string (used by all card types).
     * Checks blacklist before producing output.
     */
    private ItemStack resolveFromTargetItem(ItemStack cardStack) {
        var tag = cardStack.getTag();
        if (tag == null || !tag.contains(BlackCardUtil.NBT_TARGET_ITEM, 8)) return ItemStack.EMPTY;

        String targetItemId = tag.getString(BlackCardUtil.NBT_TARGET_ITEM);

        // Check blacklist
        if (BlackCardUtil.isBlacklisted(cardStack, targetItemId)) {
            return ItemStack.EMPTY;
        }

        int targetCount = BlackCardUtil.getTargetCount(cardStack);
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
                remaining.set(i, stack.copy()); // Don't consume the card
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
