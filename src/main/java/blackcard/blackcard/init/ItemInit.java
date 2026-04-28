package blackcard.blackcard.init;

import blackcard.blackcard.item.LocalizedBlackCard;
import blackcard.blackcard.item.ModBlackCard;
import blackcard.blackcard.item.TagBlackCard;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import blackcard.blackcard.BlackCard;
import net.minecraft.world.item.Item;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BlackCard.MODID);

    // 单物品黑卡（原有）- 指定一个具体物品生成
    public static final RegistryObject<Item> BLACK_CARD = ITEMS.register("black_card",
            () -> new LocalizedBlackCard(
                    new Item.Properties()
            )
    );

    // 标签型黑卡 - 通过检测tag生成物品，Shift+滚轮切换
    public static final RegistryObject<Item> TAG_BLACK_CARD = ITEMS.register("tag_black_card",
            () -> new TagBlackCard(
                    new Item.Properties()
            )
    );

    // 模组型黑卡 - 通过检测modid生成该模组下所有物品，Shift+滚轮切换
    public static final RegistryObject<Item> MOD_BLACK_CARD = ITEMS.register("mod_black_card",
            () -> new ModBlackCard(
                    new Item.Properties()
            )
    );
}
