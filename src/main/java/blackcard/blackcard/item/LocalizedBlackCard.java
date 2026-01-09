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

public class LocalizedBlackCard extends Item {
    public LocalizedBlackCard() {
        super(new Item.Properties().rarity(Rarity.EPIC));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public Component getName(ItemStack stack) {
        // 读取配置：是否启用自定义显示名称
        if (BlackCardConfig.COMMON.enableCustomDisplayName.get()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("targetItem", 8)) { // ✅ 修复：使用小驼峰
                String targetItemId = tag.getString("targetItem"); // ✅ 修复：使用小驼峰
                String targetItemName = getItemLocalizedName(targetItemId);

                // 获取本地化后缀（如 "黑卡" 或 "BlackCard"）
                String suffix = Component.translatable("item.blackcard.black_card.suffix").getString();

                MutableComponent itemName = Component.literal(targetItemName).withStyle(ChatFormatting.WHITE);
                MutableComponent suffixComp = Component.literal(" " + suffix).withStyle(ChatFormatting.YELLOW);
                return itemName.append(suffixComp);
            }
        }

        // 配置关闭 或 无 NBT → 统一显示基础名称
        return Component.translatable("item.blackcard.black_card").withStyle(ChatFormatting.YELLOW);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("targetItem", 8)) { // ✅ 修复：使用小驼峰
            String targetItemId = tag.getString("targetItem"); // ✅ 修复：使用小驼峰
            String targetItemName = getItemLocalizedName(targetItemId);

            // 显示生成目标
            tooltip.add(
                    Component.translatable("item.blackcard.black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );

            if (tag.contains("targetCount", 3)) { // ✅ 修复：使用小驼峰
                int targetCount = tag.getInt("targetCount"); // ✅ 修复：使用小驼峰
                int maxStackSize = getMaxStackSizeForItem(targetItemId);
                int displayCount = Math.min(targetCount, maxStackSize); // 限制为最大堆叠

                // 构建 "Amount: 64" 行
                String prefix = Component.translatable("item.blackcard.black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }
    }

    // 工具方法：获取物品最大堆叠数
    private int getMaxStackSizeForItem(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return 64;
        Item item = BuiltInRegistries.ITEM.get(rl);
        return (item == null || item == net.minecraft.world.item.Items.AIR) ? 64 : item.getMaxStackSize();
    }

    // 工具方法：获取物品本地化名称
    private String getItemLocalizedName(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return "Unknown Item";
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null || item == net.minecraft.world.item.Items.AIR) return "Unknown Item";
        return new ItemStack(item).getDisplayName().getString();
    }
}