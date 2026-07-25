package com.yuyuto.no_title_mod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.yuyuto.no_title_mod.NoTitleMod;
import com.yuyuto.no_title_mod.industry.conveyor.ConveyorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConveyorRenderer implements BlockEntityRenderer<ConveyorBlockEntity> {

    @SuppressWarnings("removal")
    private static final ResourceLocation BELT_TEXTURE = new ResourceLocation(NoTitleMod.MODID, "textures/block/conveyor_belt.png");
    private final ItemRenderer itemRenderer;

    @SuppressWarnings("unused")
    public ConveyorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull ConveyorBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        float offset = blockEntity.getRenderItemOffset(partialTick);
        renderBelt(blockEntity, partialTick, poseStack, buffer, packedLight);
        ItemStack stack = blockEntity.getStack(0);
        if(stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        Direction dir = blockEntity.getDirection();
        poseStack.translate(0.5 + dir.getStepX() * offset, 0.50, 0.5 + dir.getStepZ() * offset);
        // アイテムを寝かせる
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        // 少し小さく
        poseStack.scale(0.6f,0.6f,0.6f);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }

    private void renderBelt(@NotNull ConveyorBlockEntity entity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int light) {

        poseStack.pushPose();

        /*
         * コンベア方向へ回転
         *
         * モデル基準:
         * 北(NORTH)方向へ流れる想定
         */
        Direction direction = entity.getDirection();
        // ブロック中心へ移動
        poseStack.translate(0.5, 0, 0.5);
        // 回転
        switch(direction){
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case EAST -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            default -> {}
        }
        // 元の座標へ戻す
        poseStack.translate(-0.5, 0, -0.5);
        VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutout(BELT_TEXTURE));
        float offset = entity.getRenderBeltOffset(partialTick);
        float uvOffset = offset;
        PoseStack.Pose pose = poseStack.last();
        float minX = 2f / 16f;
        float maxX = 14f / 16f;
        float y = 8f / 16f;
        float minZ = 0f;
        float maxZ = 1f;

        /*
         * 上面
         *
         * A -------- B
         * |          |
         * |          |
         * D -------- C
         *
         */

        vertex.vertex(pose.pose(), minX,y,minZ)
                .color(255,255,255,255)
                .uv(0,uvOffset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(),0,1,0)
                .endVertex();

        vertex.vertex(pose.pose(), minX,y,maxZ)
                .color(255,255,255,255)
                .uv(0,1+uvOffset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(),0,1,0)
                .endVertex();

        vertex.vertex(pose.pose(), maxX,y,maxZ)
                .color(255,255,255,255)
                .uv(1,1+uvOffset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(),0,1,0)
                .endVertex();

        vertex.vertex(pose.pose(), maxX,y,minZ)
                .color(255,255,255,255)
                .uv(1,uvOffset)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(),0,1,0)
                .endVertex();

        poseStack.popPose();
    }
}