package blackcard.blackcard.client;

import blackcard.blackcard.BlackCard;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端设置 - 已移除ISTER注册（不再使用自定义渲染器叠加渲染）
 */
@Mod.EventBusSubscriber(modid = BlackCard.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 已移除ISTER注册 - 使用默认物品渲染
        });
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        // 注册额外的模型（如果需要的话）
    }
}
