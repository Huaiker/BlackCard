package blackcard.blackcard.init;

import blackcard.blackcard.BlackCard;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabInit {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BlackCard.MODID);

    public static final RegistryObject<CreativeModeTab> BLACK_CARD_TAB = CREATIVE_MODE_TABS.register("black_card_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.blackcard"))
                    .icon(() -> new ItemStack(ItemInit.BLACK_CARD.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ItemInit.BLACK_CARD.get());
                        output.accept(ItemInit.TAG_BLACK_CARD.get());
                        output.accept(ItemInit.MOD_BLACK_CARD.get());
                    })
                    .build());
}
