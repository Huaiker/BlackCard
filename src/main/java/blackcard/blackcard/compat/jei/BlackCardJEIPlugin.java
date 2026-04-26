package blackcard.blackcard.compat.jei;

import blackcard.blackcard.init.ItemInit;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class BlackCardJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation("blackcard", "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 单物品黑卡
        ItemStack blackCardStack = ItemInit.BLACK_CARD.get().getDefaultInstance();
        registration.addIngredientInfo(
                blackCardStack,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.blackcard.information.description")
        );

        // 标签型黑卡
        ItemStack tagBlackCardStack = ItemInit.TAG_BLACK_CARD.get().getDefaultInstance();
        registration.addIngredientInfo(
                tagBlackCardStack,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.blackcard.tag_black_card.information.description")
        );

        // 模组型黑卡
        ItemStack modBlackCardStack = ItemInit.MOD_BLACK_CARD.get().getDefaultInstance();
        registration.addIngredientInfo(
                modBlackCardStack,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.blackcard.mod_black_card.information.description")
        );
    }
}
