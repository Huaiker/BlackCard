package blackcard.blackcard.item;

import blackcard.blackcard.config.BlackCardConfig;
import blackcard.blackcard.util.BlackCardUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 单物品黑卡 - 指定一个或多个具体物品生成
 *
 * NBT结构（Cell4风格，支持列表）：
 *   bcitem: "minecraft:diamond" 或 ["minecraft:diamond", "minecraft:iron_ingot"]
 *   targetCount: 整数（默认1）
 *   bcblacklist: "minecraft:bedrock" 或 ["minecraft:bedrock", "minecraft:command_block"]
 *
 * 兼容旧NBT格式：
 *   targetItem: "minecraft:diamond"（单字符串，自动兼容）
 *
 * 合成时生成第一个非黑名单的物品
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
                    // Multiple items bound
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

        // Display all bound items
        if (itemIds.size() == 1) {
            String targetItemName = BlackCardUtil.getItemLocalizedName(itemIds.get(0));
            tooltip.add(
                    Component.translatable("item.blackcard.black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );
        } else {
            // Multiple items
            tooltip.add(
                    Component.translatable("tooltip.blackcard.item_count", itemIds.size())
                            .withStyle(ChatFormatting.AQUA)
            );
            for (String id : itemIds) {
                String name = BlackCardUtil.getItemLocalizedName(id);
                tooltip.add(
                        Component.translatable("item.blackcard.black_card.generates", name)
                                .withStyle(ChatFormatting.WHITE)
                );
            }
        }

        // Display count
        int targetCount = BlackCardUtil.getTargetCount(stack);
        if (targetCount > 1) {
            String firstItemId = itemIds.get(0);
            int maxStackSize = BlackCardUtil.getMaxStackSizeForItem(firstItemId);
            int displayCount = Math.min(targetCount, maxStackSize);

            String prefix = Component.translatable("item.blackcard.black_card.amount.prefix").getString();
            MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
            line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
            tooltip.add(line);
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
     * Create a new LocalizedBlackCard bound to the specified item identifiers.
     */
    public static ItemStack createStack(List<String> identifiers, int count) {
        ItemStack stack = new ItemStack(blackcard.blackcard.init.ItemInit.BLACK_CARD.get());
        BlackCardUtil.setStringList(stack, BlackCardUtil.NBT_ITEM, identifiers);
        BlackCardUtil.setTargetCount(stack, count);
        return stack;
    }

    /**
     * Create a new LocalizedBlackCard with a single identifier.
     */
    public static ItemStack createStack(String identifier, int count) {
        ItemStack stack = new ItemStack(blackcard.blackcard.init.ItemInit.BLACK_CARD.get());
        BlackCardUtil.setString(stack, BlackCardUtil.NBT_ITEM, identifier);
        BlackCardUtil.setTargetCount(stack, count);
        return stack;
    }
}
