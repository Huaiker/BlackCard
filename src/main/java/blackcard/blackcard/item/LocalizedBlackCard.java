package blackcard.blackcard.item;

import blackcard.blackcard.config.BlackCardConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
 * 单物品黑卡 - 指定一个具体物品生成
 * NBT结构：targetItem(字符串), targetCount(整数)
 *
 * 信息栏显示：物品名称
 */
public class LocalizedBlackCard extends Item {

    public LocalizedBlackCard() {
        super(new Item.Properties().rarity(Rarity.EPIC));
    }

    public LocalizedBlackCard(Properties properties) {
        super(properties);
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
                String targetItemName = getItemLocalizedName(targetItemId);

                String suffix = Component.translatable("item.blackcard.black_card.suffix").getString();

                MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.YELLOW);
                return itemName.append(suffixComp);
            }
        }

        return Component.translatable("item.blackcard.black_card").withStyle(ChatFormatting.YELLOW);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("targetItem", 8)) {
            String targetItemId = tag.getString("targetItem");
            String targetItemName = getItemLocalizedName(targetItemId);

            // 显示物品名称
            tooltip.add(
                    Component.translatable("item.blackcard.black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );

            // 显示数量
            if (tag.contains("targetCount", 3)) {
                int targetCount = tag.getInt("targetCount");
                int maxStackSize = getMaxStackSizeForItem(targetItemId);
                int displayCount = Math.min(targetCount, maxStackSize);

                String prefix = Component.translatable("item.blackcard.black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }
    }

    private int getMaxStackSizeForItem(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return 64;
        Item item = BuiltInRegistries.ITEM.get(rl);
        return (item == null || item == net.minecraft.world.item.Items.AIR) ? 64 : item.getMaxStackSize();
    }

    private String getItemLocalizedName(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return "Unknown Item";
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null || item == net.minecraft.world.item.Items.AIR) return "Unknown Item";
        return new ItemStack(item).getDisplayName().getString();
    }
}
