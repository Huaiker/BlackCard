package blackcard.blackcard.client;

import blackcard.blackcard.BlackCard;
import blackcard.blackcard.init.ItemInit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 黑卡自定义渲染器 - 仅渲染基础卡片模型
 * 已移除材质叠加渲染（目标物品缩略图）和信息栏中的材质渲染
 */
public class BlackCardRenderer extends BlockEntityWithoutLevelRenderer {

    public BlackCardRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
                             PoseStack poseStack, MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();

        // 确定基础模型名称
        String modelPath = getModelPath(stack.getItem());

        // 获取基础模型
        BakedModel baseModel = mc.getModelManager().getModel(
                new ModelResourceLocation(BlackCard.MODID, modelPath, "inventory")
        );

        // 渲染基础卡片模型
        VertexConsumer baseConsumer = bufferSource.getBuffer(RenderType.translucent());
        renderModelQuads(poseStack, baseConsumer, baseModel, stack, packedLight, packedOverlay);
    }

    /**
     * 根据物品类型确定模型路径
     */
    private String getModelPath(Item item) {
        if (item == ItemInit.TAG_BLACK_CARD.get()) {
            return "tag_black_card";
        } else if (item == ItemInit.MOD_BLACK_CARD.get()) {
            return "mod_black_card";
        }
        return "black_card";
    }

    /**
     * 手动渲染BakedModel的所有四边形
     */
    private void renderModelQuads(PoseStack poseStack, VertexConsumer consumer,
                                  BakedModel model, ItemStack stack,
                                  int packedLight, int packedOverlay) {
        RandomSource random = RandomSource.create();
        long seed = 42L;

        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            List<BakedQuad> quads = model.getQuads(null, direction, random);
            renderQuadList(poseStack, consumer, quads, stack, packedLight, packedOverlay);
        }

        random.setSeed(seed);
        List<BakedQuad> quads = model.getQuads(null, null, random);
        renderQuadList(poseStack, consumer, quads, stack, packedLight, packedOverlay);
    }

    /**
     * 渲染一组四边形，处理物品染色
     */
    private void renderQuadList(PoseStack poseStack, VertexConsumer consumer,
                                List<BakedQuad> quads, ItemStack stack,
                                int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        for (BakedQuad quad : quads) {
            int color = -1;
            if (quad.isTinted()) {
                color = mc.getItemColors().getColor(stack, quad.getTintIndex());
                if (color == -1) color = 0xFFFFFF;
            }
            float r = (color >> 16 & 0xFF) / 255.0f;
            float g = (color >> 8 & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;
            consumer.putBulkData(poseStack.last(), quad, r, g, b, packedLight, packedOverlay);
        }
    }
}
