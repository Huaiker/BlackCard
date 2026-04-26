package blackcard.blackcard.item;

import blackcard.blackcard.config.BlackCardConfig;
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
 * NBT结构：targetTag(字符串), targetCount(整数), currentItemIndex(整数), targetItem(字符串-自动同步)
 *
 * 信息栏显示：标签名称、当前物品、顺序[1|20]
 */
public class TagBlackCard extends Item {

    public TagBlackCard() {
        super(new Item.Properties().rarity(Rarity.EPIC));
    }

    public TagBlackCard(Properties properties) {
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

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        // 显示标签
        if (tag.contains("targetTag", 8)) {
            String targetTag = tag.getString("targetTag");
            tooltip.add(
                    Component.translatable("item.blackcard.tag_black_card.tag", targetTag)
                            .withStyle(ChatFormatting.DARK_AQUA)
            );
        }

        // 显示当前选中的物品
        if (tag.contains("targetItem", 8)) {
            String targetItemId = tag.getString("targetItem");
            String targetItemName = getItemLocalizedName(targetItemId);

            tooltip.add(
                    Component.translatable("item.blackcard.tag_black_card.generates", targetItemName)
                            .withStyle(ChatFormatting.WHITE)
            );

            // 显示数量
            if (tag.contains("targetCount", 3)) {
                int targetCount = tag.getInt("targetCount");
                int maxStackSize = getMaxStackSizeForItem(targetItemId);
                int displayCount = Math.min(targetCount, maxStackSize);

                String prefix = Component.translatable("item.blackcard.tag_black_card.amount.prefix").getString();
                MutableComponent line = Component.literal(prefix).withStyle(ChatFormatting.WHITE);
                line.append(Component.literal(String.valueOf(displayCount)).withStyle(ChatFormatting.RED));
                tooltip.add(line);
            }
        }

        // 显示顺序 [ 1 / 20 ] 及切换提示
        if (tag.contains("targetTag", 8)) {
            String targetTagStr = tag.getString("targetTag");
            List<Item> items = getItemsInTag(targetTagStr);
            if (!items.isEmpty()) {
                int currentIndex = tag.contains("currentItemIndex", 3) ? tag.getInt("currentItemIndex") : 0;
                // 显示顺序：[ 当前+1 / 总数 ]
                String indexInfo = "[ " + (currentIndex + 1) + " / " + items.size() + " ]";
                tooltip.add(Component.literal(indexInfo).withStyle(ChatFormatting.GRAY));

                // 切换提示
                tooltip.add(
                        Component.translatable("item.blackcard.tag_black_card.cycle_hint")
                                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                );
            }
        }
    }

    /**
     * 切换标签内的物品（Shift+滚轮调用）
     */
    public static void cycleTargetItem(ItemStack stack, int delta) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("targetTag", 8)) return;

        String targetTagStr = tag.getString("targetTag");
        List<Item> items = getItemsInTag(targetTagStr);
        if (items.isEmpty()) return;

        int currentIndex = tag.contains("currentItemIndex", 3) ? tag.getInt("currentItemIndex") : 0;
        int size = items.size();

        currentIndex = ((currentIndex + delta) % size + size) % size;
        tag.putInt("currentItemIndex", currentIndex);

        Item selectedItem = items.get(currentIndex);
        ResourceLocation itemIdRL = BuiltInRegistries.ITEM.getKey(selectedItem);
        tag.putString("targetItem", itemIdRL.toString());
    }

    /**
     * 根据标签字符串获取所有物品
     */
    public static List<Item> getItemsInTag(String tagString) {
        List<Item> items = new ArrayList<>();
        ResourceLocation tagRL = ResourceLocation.tryParse(tagString);
        if (tagRL == null) return items;

        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagRL);
        var tagHolder = BuiltInRegistries.ITEM.getTag(tagKey);
        if (tagHolder.isPresent()) {
            for (Holder<Item> holder : tagHolder.get()) {
                Item item = holder.value();
                if (item != Items.AIR) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * 初始化标签黑卡的targetItem
     */
    public static void ensureTargetItemSynced(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("targetTag", 8)) return;

        String targetTagStr = tag.getString("targetTag");
        List<Item> items = getItemsInTag(targetTagStr);
        if (items.isEmpty()) return;

        int currentIndex = tag.contains("currentItemIndex", 3) ? tag.getInt("currentItemIndex") : 0;
        if (currentIndex < 0 || currentIndex >= items.size()) {
            currentIndex = 0;
            tag.putInt("currentItemIndex", 0);
        }

        Item selectedItem = items.get(currentIndex);
        ResourceLocation itemIdRL = BuiltInRegistries.ITEM.getKey(selectedItem);
        tag.putString("targetItem", itemIdRL.toString());
    }

    private int getMaxStackSizeForItem(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return 64;
        Item item = BuiltInRegistries.ITEM.get(rl);
        return (item == null || item == Items.AIR) ? 64 : item.getMaxStackSize();
    }

    private String getItemLocalizedName(String itemId) {
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return "Unknown Item";
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null || item == Items.AIR) return "Unknown Item";
        return new ItemStack(item).getDisplayName().getString();
    }
}
