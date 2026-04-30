package blackcard.blackcard.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

/**
 * Shared NBT utility for BlackCard v2.0.
 * Provides parsing of list-format NBT keys and blacklist support,
 * following the same pattern as Cell4's Cell4Util.
 *
 * NBT key conventions (aligned with Cell4):
 * - "bcitem"          : item identifiers (string or string list)
 * - "bctag"           : tag identifiers (string or string list)
 * - "bcmodid"         : mod ID identifiers (string or string list)
 * - "bcblacklist"     : blacklisted item identifiers (string or string list)
 * - "targetItem"      : currently selected item ID for crafting (auto-synced)
 * - "targetCount"     : output count (integer, default 1)
 * - "currentItemIndex": current cycle index (integer)
 */
public class BlackCardUtil {

    // NBT keys (Cell4-aligned)
    public static final String NBT_ITEM = "bcitem";
    public static final String NBT_TAG = "bctag";
    public static final String NBT_MODID = "bcmodid";
    public static final String NBT_BLACKLIST = "bcblacklist";
    public static final String NBT_TARGET_ITEM = "targetItem";
    public static final String NBT_COUNT = "targetCount";
    public static final String NBT_CYCLE_INDEX = "currentItemIndex";



    /**
     * Parse a string list from NBT, supporting both single string and list formats.
     * This mirrors Cell4's Cell4Util.parseStringList().
     */
    public static List<String> parseStringList(CompoundTag tag, String key) {
        if (!tag.contains(key)) {
            return Collections.emptyList();
        }

        // List format: {key:["a","b","c"]}
        if (tag.get(key) instanceof ListTag listTag) {
            List<String> result = new ArrayList<>(listTag.size());
            for (int i = 0; i < listTag.size(); i++) {
                String str = listTag.getString(i);
                if (!str.isEmpty()) {
                    result.add(str);
                }
            }
            return result;
        }

        // Single string format: {key:"a"}
        String single = tag.getString(key);
        if (!single.isEmpty()) {
            return Collections.singletonList(single);
        }

        return Collections.emptyList();
    }

    /**
     * Set identifiers using list format on an existing ItemStack.
     */
    public static void setStringList(ItemStack stack, String key, List<String> ids) {
        var tag = stack.getOrCreateTag();
        ListTag listTag = new ListTag();
        for (String id : ids) {
            listTag.add(net.minecraft.nbt.StringTag.valueOf(id));
        }
        tag.put(key, listTag);
    }

    /**
     * Set a single identifier on an existing ItemStack.
     */
    public static void setString(ItemStack stack, String key, String id) {
        var tag = stack.getOrCreateTag();
        tag.putString(key, id);
    }

    /**
     * Get item identifiers from NBT (bcitem key).
     */
    public static List<String> getItemIdentifiers(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) return Collections.emptyList();

        return parseStringList(tag, NBT_ITEM);
    }

    /**
     * Get tag identifiers from NBT (bctag key).
     */
    public static List<String> getTagIdentifiers(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) return Collections.emptyList();

        return parseStringList(tag, NBT_TAG);
    }

    /**
     * Get mod ID identifiers from NBT (bcmodid key).
     */
    public static List<String> getModIdIdentifiers(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null) return Collections.emptyList();

        return parseStringList(tag, NBT_MODID);
    }

    /**
     * Get blacklisted item identifier strings from NBT.
     */
    public static List<String> getBlacklistIds(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_BLACKLIST)) {
            return Collections.emptyList();
        }
        return parseStringList(tag, NBT_BLACKLIST);
    }

    /**
     * Check if a specific item ID is in the blacklist.
     */
    public static boolean isBlacklisted(ItemStack stack, String itemId) {
        List<String> blacklist = getBlacklistIds(stack);
        return blacklist.contains(itemId);
    }

    /**
     * Check if a specific Item is in the blacklist (by registry name).
     */
    public static boolean isBlacklisted(ItemStack stack, Item item) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
        return isBlacklisted(stack, rl.toString());
    }

    /**
     * Check if a specific ItemStack's item is in the blacklist.
     */
    public static boolean isBlacklisted(ItemStack cardStack, ItemStack targetStack) {
        if (targetStack.isEmpty()) return false;
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(targetStack.getItem());
        return isBlacklisted(cardStack, rl.toString());
    }

    /**
     * Get the target count from NBT, defaulting to 1.
     */
    public static int getTargetCount(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains(NBT_COUNT, 3)) {
            return tag.getInt(NBT_COUNT);
        }
        return 1;
    }

    /**
     * Set the target count in NBT.
     */
    public static void setTargetCount(ItemStack stack, int count) {
        stack.getOrCreateTag().putInt(NBT_COUNT, count);
    }

    /**
     * Get the current cycle index from NBT, defaulting to 0.
     */
    public static int getCycleIndex(ItemStack stack) {
        var tag = stack.getTag();
        if (tag != null && tag.contains(NBT_CYCLE_INDEX, 3)) {
            return tag.getInt(NBT_CYCLE_INDEX);
        }
        return 0;
    }

    /**
     * Set the cycle index in NBT.
     */
    public static void setCycleIndex(ItemStack stack, int index) {
        stack.getOrCreateTag().putInt(NBT_CYCLE_INDEX, index);
    }

    /**
     * Add an item to the blacklist.
     */
    public static void addBlacklist(ItemStack stack, String itemId) {
        List<String> blacklist = new ArrayList<>(getBlacklistIds(stack));
        if (!blacklist.contains(itemId)) {
            blacklist.add(itemId);
            setStringList(stack, NBT_BLACKLIST, blacklist);
        }
    }

    /**
     * Remove an item from the blacklist.
     */
    public static void removeBlacklist(ItemStack stack, String itemId) {
        List<String> blacklist = new ArrayList<>(getBlacklistIds(stack));
        blacklist.remove(itemId);
        if (blacklist.isEmpty()) {
            stack.getOrCreateTag().remove(NBT_BLACKLIST);
        } else {
            setStringList(stack, NBT_BLACKLIST, blacklist);
        }
    }

    /**
     * Resolve the first valid target item from the card's NBT.
     * For single-item cards, returns the first non-blacklisted item.
     * Respects the blacklist - skips blacklisted items.
     */
    public static ItemStack resolveTargetItem(ItemStack cardStack) {
        List<String> itemIds = getItemIdentifiers(cardStack);
        if (!itemIds.isEmpty()) {
            for (String id : itemIds) {
                if (!isBlacklisted(cardStack, id)) {
                    ResourceLocation rl = ResourceLocation.tryParse(id);
                    if (rl != null) {
                        Item item = BuiltInRegistries.ITEM.get(rl);
                        if (item != null && item != Items.AIR) {
                            int count = getTargetCount(cardStack);
                            int maxStack = item.getMaxStackSize();
                            return new ItemStack(item, Math.min(Math.max(1, count), maxStack));
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Get the localized display name for an item ID string.
     */
    public static String getItemLocalizedName(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return "Unknown Item";
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null || item == Items.AIR) return "Unknown Item";
        return new ItemStack(item).getDisplayName().getString();
    }

    /**
     * Get the max stack size for an item ID string.
     */
    public static int getMaxStackSizeForItem(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return 64;
        Item item = BuiltInRegistries.ITEM.get(rl);
        return (item == null || item == Items.AIR) ? 64 : item.getMaxStackSize();
    }
}
