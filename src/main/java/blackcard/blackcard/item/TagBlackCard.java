package blackcard.blackcard.item;

import blackcard.blackcard.config.BlackCardConfig;
import blackcard.blackcard.util.BlackCardUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
 * 标签型黑卡 - 根据物品标签（Tag）生成物品
 * Shift+滚轮可以切换标签内的具体物品
 *
 * NBT结构（Cell4风格，支持列表）：
 *   bctag: "minecraft:logs" 或 ["minecraft:logs", "forge:ingots/iron"]
 *   targetCount: 整数（默认1）
 *   currentItemIndex: 整数（当前循环索引）
 *   targetItem: 字符串（自动同步，当前选中的物品ID）
 *   bcblacklist: "minecraft:bedrock" 或 ["minecraft:bedrock", "minecraft:command_block"]
 *
 * 兼容旧NBT格式：
 *   targetTag: "minecraft:logs"（单字符串，自动兼容）
 */
public class TagBlackCard extends Item {

    public TagBlackCard() {
        super(new Item.Properties());
    }

    public TagBlackCard(Properties properties) {
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
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("targetItem", 8)) {
                String targetItemId = tag.getString("targetItem");
                String targetItemName = BlackCardUtil.getItemLocalizedName(targetItemId);

                String suffix = Component.translatable("item.blackcard.tag_black_card.suffix").getString();

                MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.AQUA);
                return itemName.append(suffixComp);
            }
        }
        return Component.translatable("item.blackcard.tag_black_card").withStyle(ChatFormatting.AQUA);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        List<String> tagIds = BlackCardUtil.getTagIdentifiers(stack);
        if (tagIds.isEmpty()) return;

        // Display all bound tags
        for (String tagId : tagIds) {
            tooltip.add(
                    Component.translatable("item.blackcard.tag_black_card.tag", tagId)
                            .withStyle(ChatFormatting.DARK_AQUA)
            );
        }

        // Display currently selected item
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("targetItem", 8)) {
            String targetItemId = tag.getString("targetItem");
            String targetItemName = BlackCardUtil.getItemLocalizedName(targetItemId);

            tooltip.add(
                    Component.translatable("item.blackcard.tag_black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );

            // Display count
            int targetCount = BlackCardUtil.getTargetCount(stack);
            if (targetCount > 1) {
                int maxStackSize = BlackCardUtil.getMaxStackSizeForItem(targetItemId);
                int displayCount = Math.min(targetCount, maxStackSize);

                String prefix = Component.translatable("item.blackcard.tag_black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }

        // Display order [1/20] and cycle hint (after filtering blacklist)
        List<Item> availableItems = collectAvailableItems(stack, tagIds);
        if (!availableItems.isEmpty()) {
            int currentIndex = BlackCardUtil.getCycleIndex(stack);
            String indexInfo = "[ " + (currentIndex + 1) + " / " + availableItems.size() + " ]";
            tooltip.add(Component.literal(indexInfo).withStyle(ChatFormatting.GRAY));

            tooltip.add(
                    Component.translatable("item.blackcard.tag_black_card.cycle_hint")
                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            );
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
     * 切换标签内的物品（Shift+滚轮调用），跳过黑名单物品
     */
    public static void cycleTargetItem(ItemStack stack, int delta) {
        CompoundTag tag = stack.getOrCreateTag();
        List<String> tagIds = BlackCardUtil.getTagIdentifiers(stack);
        if (tagIds.isEmpty()) return;

        List<Item> items = collectAvailableItems(stack, tagIds);
        if (items.isEmpty()) return;

        int currentIndex = BlackCardUtil.getCycleIndex(stack);
        int size = items.size();

        currentIndex = ((currentIndex + delta) % size + size) % size;
        BlackCardUtil.setCycleIndex(stack, currentIndex);

        Item selectedItem = items.get(currentIndex);
        ResourceLocation itemIdRL = BuiltInRegistries.ITEM.getKey(selectedItem);
        tag.putString("targetItem", itemIdRL.toString());
    }

    /**
     * 根据标签字符串列表获取所有物品（原始列表，不排除黑名单）
     */
    public static List<Item> collectAllItemsFromTags(List<String> tagStrings) {
        List<Item> items = new ArrayList<>();
        for (String tagString : tagStrings) {
            ResourceLocation tagRL = ResourceLocation.tryParse(tagString);
            if (tagRL == null) continue;

            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagRL);
            var tagHolder = BuiltInRegistries.ITEM.getTag(tagKey);
            if (tagHolder.isPresent()) {
                for (Holder<Item> holder : tagHolder.get()) {
                    Item item = holder.value();
                    if (item != Items.AIR && !items.contains(item)) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    /**
     * 获取可用物品列表（排除黑名单物品）
     */
    private static List<Item> collectAvailableItems(ItemStack stack, List<String> tagIds) {
        List<Item> allItems = collectAllItemsFromTags(tagIds);
        List<String> blacklist = BlackCardUtil.getBlacklistIds(stack);
        if (blacklist.isEmpty()) return allItems;

        List<Item> available = new ArrayList<>();
        for (Item item : allItems) {
            ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item);
            if (!blacklist.contains(rl.toString())) {
                available.add(item);
            }
        }
        return available;
    }

    /**
     * 根据标签字符串获取所有物品（保留向后兼容）
     */
    public static List<Item> getItemsInTag(String tagString) {
        return collectAllItemsFromTags(List.of(tagString));
    }

    /**
     * 初始化标签黑卡的targetItem，跳过黑名单物品
     */
    public static void ensureTargetItemSynced(ItemStack stack) {
        List<String> tagIds = BlackCardUtil.getTagIdentifiers(stack);
        if (tagIds.isEmpty()) return;

        List<Item> items = collectAvailableItems(stack, tagIds);
        if (items.isEmpty()) return;

        int currentIndex = BlackCardUtil.getCycleIndex(stack);
        if (currentIndex < 0 || currentIndex >= items.size()) {
            currentIndex = 0;
            BlackCardUtil.setCycleIndex(stack, 0);
        }

        Item selectedItem = items.get(currentIndex);
        ResourceLocation itemIdRL = BuiltInRegistries.ITEM.getKey(selectedItem);
        stack.getOrCreateTag().putString("targetItem", itemIdRL.toString());
    }
}
