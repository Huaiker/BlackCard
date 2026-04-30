package blackcard.blackcard.item;

import blackcard.blackcard.config.BlackCardConfig;
import blackcard.blackcard.util.BlackCardUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 单物品黑卡 - 指定一个或多个具体物品生成
 * 当绑定多个物品时，Shift+滚轮可以切换当前选中的物品
 *
 * NBT结构（Cell4风格，支持列表）：
 *   bcitem: "minecraft:diamond" 或 ["minecraft:diamond", "minecraft:iron_ingot"]
 *   targetCount: 整数（默认1）
 *   currentItemIndex: 整数（当前循环索引，多物品时使用）
 *   targetItem: 字符串（自动同步，当前选中的物品ID）
 *   bcblacklist: "minecraft:bedrock" 或 ["minecraft:bedrock", "minecraft:command_block"]
 *
 * 单物品时合成直接产出；多物品时合成产出当前选中物品
 */
public class LocalizedBlackCard extends Item {

    public LocalizedBlackCard() {
        super(new Item.Properties());
    }

    public LocalizedBlackCard(Properties properties) {
        super(properties);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (BlackCardConfig.COMMON.enableCustomDisplayName.get()) {
            List<String> itemIds = BlackCardUtil.getItemIdentifiers(stack);
            if (!itemIds.isEmpty()) {
                String suffix = Component.translatable("item.blackcard.black_card.suffix").getString();

                if (itemIds.size() == 1) {
                    String targetItemName = BlackCardUtil.getItemLocalizedName(itemIds.get(0));
                    MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                    MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.YELLOW);
                    return itemName.append(suffixComp);
                } else {
                    // Multiple items bound - show current selected item name
                    CompoundTag tag = stack.getTag();
                    if (tag != null && tag.contains(BlackCardUtil.NBT_TARGET_ITEM, 8)) {
                        String targetItemId = tag.getString(BlackCardUtil.NBT_TARGET_ITEM);
                        String targetItemName = BlackCardUtil.getItemLocalizedName(targetItemId);
                        MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                        MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.YELLOW);
                        return itemName.append(suffixComp);
                    }

                    // Fallback: show multi-item name
                    MutableComponent name = Component.translatable("item.blackcard.black_card_name_multi", itemIds.size())
                            .withStyle(ChatFormatting.YELLOW);
                    return name;
                }
            }
        }

        return Component.translatable("item.blackcard.black_card").withStyle(ChatFormatting.YELLOW);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        List<String> itemIds = BlackCardUtil.getItemIdentifiers(stack);
        if (itemIds.isEmpty()) return;

        if (itemIds.size() == 1) {
            // Single item - simple display
            String targetItemName = BlackCardUtil.getItemLocalizedName(itemIds.get(0));
            tooltip.add(
                    Component.translatable("item.blackcard.black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );
        } else {
            // Multiple items - show current selection and cycle hint
            tooltip.add(
                    Component.translatable("tooltip.blackcard.item_count", itemIds.size())
                            .withStyle(ChatFormatting.AQUA)
            );

            // Display currently selected item
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(BlackCardUtil.NBT_TARGET_ITEM, 8)) {
                String targetItemId = tag.getString(BlackCardUtil.NBT_TARGET_ITEM);
                String targetItemName = BlackCardUtil.getItemLocalizedName(targetItemId);
                tooltip.add(
                        Component.translatable("item.blackcard.black_card.generates", targetItemName)
                                .withStyle(ChatFormatting.WHITE)
                );
            }

            // Display order [1/5] and cycle hint (after filtering blacklist)
            List<String> availableIds = getAvailableItemIds(stack, itemIds);
            if (!availableIds.isEmpty()) {
                int currentIndex = BlackCardUtil.getCycleIndex(stack);
                String indexInfo = "[ " + (currentIndex + 1) + " / " + availableIds.size() + " ]";
                tooltip.add(Component.literal(indexInfo).withStyle(ChatFormatting.GRAY));

                tooltip.add(
                        Component.translatable("item.blackcard.black_card.cycle_hint")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                );
            }
        }

        // Display count
        int targetCount = BlackCardUtil.getTargetCount(stack);
        if (targetCount > 1) {
            // Determine which item to check max stack size for
            String checkItemId;
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(BlackCardUtil.NBT_TARGET_ITEM, 8)) {
                checkItemId = tag.getString(BlackCardUtil.NBT_TARGET_ITEM);
            } else if (!itemIds.isEmpty()) {
                checkItemId = itemIds.get(0);
            } else {
                checkItemId = null;
            }

            if (checkItemId != null) {
                int maxStackSize = BlackCardUtil.getMaxStackSizeForItem(checkItemId);
                int displayCount = Math.min(targetCount, maxStackSize);

                String prefix = Component.translatable("item.blackcard.black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }

        // Display blacklist
        List<String> blacklist = BlackCardUtil.getBlacklistIds(stack);
        for (String blId : blacklist) {
            String blName = BlackCardUtil.getItemLocalizedName(blId);
            tooltip.add(
                    Component.translatable("tooltip.blackcard.blacklist_item", blName)
                            .withStyle(ChatFormatting.RED)
            );
        }
    }

    /**
     * 获取可用物品ID列表（排除黑名单物品）
     */
    private static List<String> getAvailableItemIds(ItemStack stack, List<String> itemIds) {
        List<String> blacklist = BlackCardUtil.getBlacklistIds(stack);
        if (blacklist.isEmpty()) return itemIds;

        List<String> available = new ArrayList<>();
        for (String id : itemIds) {
            if (!blacklist.contains(id)) {
                available.add(id);
            }
        }
        return available;
    }

    /**
     * 切换物品黑卡中的物品（Shift+滚轮调用），跳过黑名单物品
     * 仅在绑定多个物品时有效
     */
    public static void cycleTargetItem(ItemStack stack, int delta) {
        List<String> itemIds = BlackCardUtil.getItemIdentifiers(stack);
        if (itemIds.size() <= 1) return; // 单物品无需切换

        List<String> availableIds = getAvailableItemIds(stack, itemIds);
        if (availableIds.isEmpty()) return;

        int currentIndex = BlackCardUtil.getCycleIndex(stack);
        int size = availableIds.size();

        currentIndex = ((currentIndex + delta) % size + size) % size;
        BlackCardUtil.setCycleIndex(stack, currentIndex);

        String selectedId = availableIds.get(currentIndex);
        stack.getOrCreateTag().putString(BlackCardUtil.NBT_TARGET_ITEM, selectedId);
    }

    /**
     * 初始化物品黑卡的targetItem（多物品时同步当前索引到targetItem）
     */
    public static void ensureTargetItemSynced(ItemStack stack) {
        List<String> itemIds = BlackCardUtil.getItemIdentifiers(stack);
        if (itemIds.isEmpty()) return;

        if (itemIds.size() == 1) {
            // Single item: set targetItem directly
            stack.getOrCreateTag().putString(BlackCardUtil.NBT_TARGET_ITEM, itemIds.get(0));
            return;
        }

        // Multiple items: sync from cycle index
        List<String> availableIds = getAvailableItemIds(stack, itemIds);
        if (availableIds.isEmpty()) return;

        int currentIndex = BlackCardUtil.getCycleIndex(stack);
        if (currentIndex < 0 || currentIndex >= availableIds.size()) {
            currentIndex = 0;
            BlackCardUtil.setCycleIndex(stack, 0);
        }

        String selectedId = availableIds.get(currentIndex);
        stack.getOrCreateTag().putString(BlackCardUtil.NBT_TARGET_ITEM, selectedId);
    }

    /**
     * Create a new LocalizedBlackCard bound to the specified item identifiers.
     */
    public static ItemStack createStack(List<String> identifiers, int count) {
        ItemStack stack = new ItemStack(blackcard.blackcard.init.ItemInit.BLACK_CARD.get());
        BlackCardUtil.setStringList(stack, BlackCardUtil.NBT_ITEM, identifiers);
        BlackCardUtil.setTargetCount(stack, count);
        // Initialize targetItem
        ensureTargetItemSynced(stack);
        return stack;
    }

    /**
     * Create a new LocalizedBlackCard with a single identifier.
     */
    public static ItemStack createStack(String identifier, int count) {
        ItemStack stack = new ItemStack(blackcard.blackcard.init.ItemInit.BLACK_CARD.get());
        BlackCardUtil.setString(stack, BlackCardUtil.NBT_ITEM, identifier);
        BlackCardUtil.setTargetCount(stack, count);
        stack.getOrCreateTag().putString(BlackCardUtil.NBT_TARGET_ITEM, identifier);
        return stack;
    }
}
