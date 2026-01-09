package blackcard.blackcard;

import blackcard.blackcard.config.BlackCardConfig;
import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.recipe.UniversalNBTRecipe;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BlackCard.MODID)
public class BlackCard {
    public static final String MODID = "blackcard";

    public BlackCard() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置
        BlackCardConfig.register();

        ItemInit.ITEMS.register(modEventBus);
        UniversalNBTRecipe.RECIPE_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(this::addItemsToTabs);
    }

    private void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemInit.BLACK_CARD);
        }
    }
}