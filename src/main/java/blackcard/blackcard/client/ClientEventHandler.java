package blackcard.blackcard.client;

import blackcard.blackcard.BlackCard;
import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.item.ModBlackCard;
import blackcard.blackcard.item.TagBlackCard;
import blackcard.blackcard.network.CycleModItemPacket;
import blackcard.blackcard.network.CycleTagItemPacket;
import blackcard.blackcard.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端事件处理器 - 监听Shift+滚轮切换标签黑卡/模组黑卡物品
 */
@Mod.EventBusSubscriber(modid = BlackCard.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // 必须按住Shift键
        if (!mc.options.keyShift.isDown()) return;

        double scrollDelta = event.getScrollDelta();
        if (scrollDelta == 0) return;

        // 检查主手和副手是否持有标签黑卡或模组黑卡
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        int direction = scrollDelta > 0 ? -1 : 1; // 向上滚动=上一个物品，向下=下一个物品

        boolean handled = false;

        // 标签黑卡处理
        if (mainHand.getItem() == ItemInit.TAG_BLACK_CARD.get()) {
            int slot = player.getInventory().selected;
            NetworkHandler.CHANNEL.sendToServer(new CycleTagItemPacket(slot, direction));
            TagBlackCard.cycleTargetItem(mainHand, direction);
            handled = true;
        } else if (offHand.getItem() == ItemInit.TAG_BLACK_CARD.get()) {
            int slot = 40; // 副手槽位索引
            NetworkHandler.CHANNEL.sendToServer(new CycleTagItemPacket(slot, direction));
            TagBlackCard.cycleTargetItem(offHand, direction);
            handled = true;
        }

        // 模组黑卡处理
        if (mainHand.getItem() == ItemInit.MOD_BLACK_CARD.get()) {
            int slot = player.getInventory().selected;
            NetworkHandler.CHANNEL.sendToServer(new CycleModItemPacket(slot, direction));
            ModBlackCard.cycleTargetItem(mainHand, direction);
            handled = true;
        } else if (offHand.getItem() == ItemInit.MOD_BLACK_CARD.get()) {
            int slot = 40; // 副手槽位索引
            NetworkHandler.CHANNEL.sendToServer(new CycleModItemPacket(slot, direction));
            ModBlackCard.cycleTargetItem(offHand, direction);
            handled = true;
        }

        if (handled) {
            event.setCanceled(true); // 取消默认的滚轮行为（如快捷栏切换）
        }
    }
}
