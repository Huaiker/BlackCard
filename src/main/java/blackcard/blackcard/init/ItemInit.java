package blackcard.blackcard.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import blackcard.blackcard.BlackCard;
import net.minecraft.world.item.Item;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BlackCard.MODID);

    public static final RegistryObject<Item> BLACK_CARD = ITEMS.register("black_card",
            () -> new blackcard.blackcard.item.LocalizedBlackCard()
    );
}