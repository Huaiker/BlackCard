package blackcard.blackcard;

import blackcard.blackcard.compat.cell4.Cell4IntegrationRecipe;
import blackcard.blackcard.config.BlackCardConfig;
import blackcard.blackcard.init.ItemInit;
import blackcard.blackcard.network.NetworkHandler;
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

        // Register config
        BlackCardConfig.register();

        // Register items and recipe serializers
        ItemInit.ITEMS.register(modEventBus);
        UniversalNBTRecipe.RECIPE_SERIALIZERS.register(modEventBus);
        Cell4IntegrationRecipe.RECIPE_SERIALIZERS.register(modEventBus);

        // Register network communication
        NetworkHandler.register();

        // Add items to creative mode tab
        modEventBus.addListener(this::addItemsToTabs);
    }

    private void addItemsToTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ItemInit.BLACK_CARD);
            event.accept(ItemInit.TAG_BLACK_CARD);
            event.accept(ItemInit.MOD_BLACK_CARD);
        }
    }
}
