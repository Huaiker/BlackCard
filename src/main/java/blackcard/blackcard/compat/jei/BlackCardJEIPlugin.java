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
        ItemStack stack = ItemInit.BLACK_CARD.get().getDefaultInstance();

        // ✅ 传入 Component...（可变参数），不要用 List.of()
        registration.addIngredientInfo(
                stack,
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.blackcard.information.description")
                // 可以加多行：
                // Component.literal("Second line"),
                // Component.literal("Third line")
        );
    }
}