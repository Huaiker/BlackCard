package blackcard.blackcard.network;

import blackcard.blackcard.BlackCard;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 网络通信处理器 - 管理客户端与服务端之间的数据包传输
 */
public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BlackCard.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // 标签黑卡切换数据包
        CHANNEL.messageBuilder(CycleTagItemPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CycleTagItemPacket::encode)
                .decoder(CycleTagItemPacket::decode)
                .consumerMainThread(CycleTagItemPacket::handle)
                .add();

        // 模组黑卡切换数据包
        CHANNEL.messageBuilder(CycleModItemPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CycleModItemPacket::encode)
                .decoder(CycleModItemPacket::decode)
                .consumerMainThread(CycleModItemPacket::handle)
                .add();
    }
}
