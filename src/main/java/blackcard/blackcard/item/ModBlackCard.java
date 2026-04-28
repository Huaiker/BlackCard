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
import java.util.Comparator;
import java.util.List;

/**
 * 模组型黑卡 - 根据模组ID（modid）生成该模组下的物品
 * Shift+滚轮可以切换模组内的具体物品
 *
 * NBT结构（Cell4风格，支持列表）：
 *   bcmodid: "mekanism" 或 ["mekanism", "thermal"]
 *   targetCount: 整数（默认1）
 *   currentItemIndex: 整数（当前循环索引）
 *   targetItem: 字符串（自动同步，当前选中的物品ID）
 *   bcblacklist: "minecraft:bedrock" 或 ["minecraft:bedrock", "minecraft:command_block"]
 *
 * 兼容旧NBT格式：
 *   targetMod: "mekanism"（单字符串，自动兼容）
 */
public class ModBlackCard extends Item {

    public ModBlackCard() {
        super(new Item.Properties());
    }

    public ModBlackCard(Properties properties) {
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

                String suffix = Component.translatable("item.blackcard.mod_black_card.suffix").getString();

                MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.LIGHT_PURPLE);
                return itemName.append(suffixComp);
            }
        }
        return Component.translatable("item.blackcard.mod_black_card").withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        List<String> modIds = BlackCardUtil.getModIdIdentifiers(stack);
        if (modIds.isEmpty()) return;

        // Display all bound mod IDs
        for (String modId : modIds) {
            tooltip.add(
                    Component.translatable("item.blackcard.mod_black_card.mod", modId)
                            .withStyle(ChatFormatting.DARK_PURPLE)
            );
        }

        // Display currently selected item
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("targetItem", 8)) {
            String targetItemId = tag.getString("targetItem");
            String targetItemName = BlackCardUtil.getItemLocalizedName(targetItemId);

            tooltip.add(
                    Component.translatable("item.blackcard.mod_black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );

            // Display count
            int targetCount = BlackCardUtil.getTargetCount(stack);
            if (targetCount > 1) {
                int maxStackSize = BlackCardUtil.getMaxStackSizeForItem(targetItemId);
                int displayCount = Math.min(targetCount, maxStackSize);

                String prefix = Component.translatable("item.blackcard.mod_black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }

        // Display order [1/200] and cycle hint (after filtering blacklist)
        List<Item> availableItems = collectAvailableItems(stack, modIds);
        if (!availableItems.isEmpty()) {
            int currentIndex = BlackCardUtil.getCycleIndex(stack);
            String indexInfo = "[ " + (currentIndex + 1) + " / " + availableItems.size() + " ]";
            tooltip.add(Component.literal(indexInfo).withStyle(ChatFormatting.GRAY));

            tooltip.add(
                    Component.translatable("item.blackcard.mod_black_card.cycle_hint")
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
     * 切换模组内的物品（Shift+滚轮调用），跳过黑名单物品
     */
    public static void cycleTargetItem(ItemStack stack, int delta) {
        CompoundTag tag = stack.getOrCreateTag();
        List<String> modIds = BlackCardUtil.getModIdIdentifiers(stack);
        if (modIds.isEmpty()) return;

        List<Item> items = collectAvailableItems(stack, modIds);
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
     * 根据模组ID列表获取所有已注册的物品（原始列表，不排除黑名单）
     */
    public static List<Item> collectAllItemsFromMods(List<String> modIds) {
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) continue;
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            for (String modId : modIds) {
                if (itemId.getNamespace().equals(modId)) {
                    if (!items.contains(item)) {
                        items.add(item);
                    }
                    break;
                }
            }
        }
        items.sort(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()));
        return items;
    }

    /**
     * 获取可用物品列表（排除黑名单物品）
     */
    private static List<Item> collectAvailableItems(ItemStack stack, List<String> modIds) {
        List<Item> allItems = collectAllItemsFromMods(modIds);
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
     * 根据模组ID获取该模组下所有已注册的物品（保留向后兼容）
     */
    public static List<Item> getItemsInMod(String modId) {
        return collectAllItemsFromMods(List.of(modId));
    }

    /**
     * 初始化模组黑卡的targetItem，跳过黑名单物品
     */
    public static void ensureTargetItemSynced(ItemStack stack) {
        List<String> modIds = BlackCardUtil.getModIdIdentifiers(stack);
        if (modIds.isEmpty()) return;

        List<Item> items = collectAvailableItems(stack, modIds);
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
