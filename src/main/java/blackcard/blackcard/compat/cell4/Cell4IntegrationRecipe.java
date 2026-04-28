package blackcard.blackcard.compat.cell4;

import blackcard.blackcard.BlackCard;
import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.util.BlackCardUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

import javax.annotation.Nullable;
import java.util.List;

/**
 * Cell4 Integration Recipe - Black Card + AE2 Item Cell Housing = Cell4 Infinity Cell
 *
 * This recipe allows combining a Black Card with an AE2 item_cell_housing
 * to create the corresponding Cell4 Infinity Cell, transferring all NBT data
 * (identifiers and blacklist) from the Black Card.
 *
 * Recipe pattern (shapeless):
 *   [Black Card] + [AE2 Item Cell Housing] -> [Cell4 Infinity Cell]
 *
 * Mapping:
 *   Black Card (bcitem)     + housing -> Cell4 Infinity Item Cell (cell4item)
 *   Tag Black Card (bctag)  + housing -> Cell4 Infinity Tag Cell (cell4tag)
 *   Mod Black Card (bcmodid)+ housing -> Cell4 Infinity ModID Cell (cell4modid)
 *
 * The blacklist (bcblacklist) is transferred as cell4blacklist.
 */
public class Cell4IntegrationRecipe extends CustomRecipe {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BlackCard.MODID);

    public static final RegistryObject<RecipeSerializer<Cell4IntegrationRecipe>> CELL4_INTEGRATION_RECIPE =
            RECIPE_SERIALIZERS.register("cell4_integration", () -> new RecipeSerializer<>() {
                @Override
                public Cell4IntegrationRecipe fromJson(ResourceLocation id, JsonObject json) {
                    return new Cell4IntegrationRecipe(id, CraftingBookCategory.MISC);
                }

                @Override
                public Cell4IntegrationRecipe fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buf) {
                    return new Cell4IntegrationRecipe(id, CraftingBookCategory.MISC);
                }

                @Override
                public void toNetwork(net.minecraft.network.FriendlyByteBuf buf, Cell4IntegrationRecipe recipe) {}
            });

    // AE2 item_cell_housing resource location
    private static final ResourceLocation AE2_ITEM_CELL_HOUSING = new ResourceLocation("ae2", "item_cell_housing");

    // Cell4 item resource locations
    private static final ResourceLocation CELL4_INFINITY_ITEM_CELL = new ResourceLocation("cell4", "infinity_item_cell");
    private static final ResourceLocation CELL4_INFINITY_TAG_CELL = new ResourceLocation("cell4", "infinity_tag_cell");
    private static final ResourceLocation CELL4_INFINITY_MODID_CELL = new ResourceLocation("cell4", "infinity_modid_cell");

    // Cell4 NBT key names (aligned with Cell4's format)
    private static final String CELL4_ITEM_KEY = "cell4item";
    private static final String CELL4_TAG_KEY = "cell4tag";
    private static final String CELL4_MODID_KEY = "cell4modid";
    private static final String CELL4_BLACKLIST_KEY = "cell4blacklist";

    public Cell4IntegrationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack blackCard = findBlackCard(container);
        ItemStack housing = findHousing(container);

        return !blackCard.isEmpty() && !housing.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack blackCard = findBlackCard(container);
        if (blackCard.isEmpty()) return ItemStack.EMPTY;

        return createCell4Item(blackCard);
    }

    /**
     * Create a Cell4 Infinity Cell from the Black Card's NBT data.
     * Transfers identifiers and blacklist from BlackCard format to Cell4 format.
     */
    private ItemStack createCell4Item(ItemStack blackCard) {
        Item cellItem = getTargetCell4Item(blackCard);
        if (cellItem == null || cellItem == Items.AIR) return ItemStack.EMPTY;

        ItemStack result = new ItemStack(cellItem);
        CompoundTag resultTag = result.getOrCreateTag();

        // Transfer identifiers based on card type
        if (blackCard.getItem() == ItemInit.BLACK_CARD.get()) {
            // Single-item black card -> Infinity Item Cell
            List<String> itemIds = BlackCardUtil.getItemIdentifiers(blackCard);
            transferList(resultTag, CELL4_ITEM_KEY, itemIds);
        } else if (blackCard.getItem() == ItemInit.TAG_BLACK_CARD.get()) {
            // Tag black card -> Infinity Tag Cell
            List<String> tagIds = BlackCardUtil.getTagIdentifiers(blackCard);
            transferList(resultTag, CELL4_TAG_KEY, tagIds);
        } else if (blackCard.getItem() == ItemInit.MOD_BLACK_CARD.get()) {
            // Mod black card -> Infinity ModID Cell
            List<String> modIds = BlackCardUtil.getModIdIdentifiers(blackCard);
            transferList(resultTag, CELL4_MODID_KEY, modIds);
        }

        // Transfer blacklist
        List<String> blacklist = BlackCardUtil.getBlacklistIds(blackCard);
        if (!blacklist.isEmpty()) {
            transferList(resultTag, CELL4_BLACKLIST_KEY, blacklist);
        }

        return result;
    }

    /**
     * Transfer a string list to a target NBT tag using Cell4's list format.
     */
    private void transferList(CompoundTag targetTag, String key, List<String> ids) {
        if (ids.isEmpty()) return;

        if (ids.size() == 1) {
            // Single item: use string format for Cell4 compatibility
            targetTag.putString(key, ids.get(0));
        } else {
            // Multiple items: use list format
            ListTag listTag = new ListTag();
            for (String id : ids) {
                listTag.add(StringTag.valueOf(id));
            }
            targetTag.put(key, listTag);
        }
    }

    /**
     * Determine which Cell4 cell item to create based on the Black Card type.
     */
    @Nullable
    private Item getTargetCell4Item(ItemStack blackCard) {
        ResourceLocation targetRl;

        if (blackCard.getItem() == ItemInit.BLACK_CARD.get()) {
            targetRl = CELL4_INFINITY_ITEM_CELL;
        } else if (blackCard.getItem() == ItemInit.TAG_BLACK_CARD.get()) {
            targetRl = CELL4_INFINITY_TAG_CELL;
        } else if (blackCard.getItem() == ItemInit.MOD_BLACK_CARD.get()) {
            targetRl = CELL4_INFINITY_MODID_CELL;
        } else {
            return null;
        }

        var item = BuiltInRegistries.ITEM.getOptional(targetRl);
        return item.orElse(null);
    }

    /**
     * Find the Black Card in the crafting container.
     */
    private ItemStack findBlackCard(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() &&
                (stack.getItem() == ItemInit.BLACK_CARD.get() ||
                 stack.getItem() == ItemInit.TAG_BLACK_CARD.get() ||
                 stack.getItem() == ItemInit.MOD_BLACK_CARD.get())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Find the AE2 item_cell_housing in the crafting container.
     */
    private ItemStack findHousing(CraftingContainer container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (rl.equals(AE2_ITEM_CELL_HOUSING)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public net.minecraft.core.NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        net.minecraft.core.NonNullList<ItemStack> remaining =
                net.minecraft.core.NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        // Don't return any items - both the card and housing are consumed
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CELL4_INTEGRATION_RECIPE.get();
    }
}
