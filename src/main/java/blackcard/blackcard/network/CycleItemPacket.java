package blackcard.blackcard.network;

import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.item.LocalizedBlackCard;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2S数据包 - 客户端请求切换物品黑卡中的物品（多物品绑定时）
 * 携带物品所在槽位和滚轮方向
 */
public class CycleItemPacket {

    private final int slotIndex;
    private final int delta; // 滚轮方向：正数向上，负数向下

    public CycleItemPacket(int slotIndex, int delta) {
        this.slotIndex = slotIndex;
        this.delta = delta;
    }

    public static void encode(CycleItemPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slotIndex);
        buf.writeInt(msg.delta);
    }

    public static CycleItemPacket decode(FriendlyByteBuf buf) {
        return new CycleItemPacket(buf.readInt(), buf.readInt());
    }

    public static void handle(CycleItemPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            // 验证槽位范围
            if (msg.slotIndex < 0 || msg.slotIndex >= player.getInventory().getContainerSize()) return;

            ItemStack stack = player.getInventory().getItem(msg.slotIndex);

            // 确保是物品黑卡
            if (stack.getItem() != ItemInit.BLACK_CARD.get()) return;

            // 执行切换
            LocalizedBlackCard.cycleTargetItem(stack, msg.delta);
        });
        context.setPacketHandled(true);
    }
}
