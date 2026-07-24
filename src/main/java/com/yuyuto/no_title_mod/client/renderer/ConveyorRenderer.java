package com.yuyuto.no_title_mod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yuyuto.no_title_mod.industry.conveyor.ConveyorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConveyorRenderer implements BlockEntityRenderer<ConveyorBlockEntity> {

    private final ItemRenderer itemRenderer;
    public ConveyorRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(@NotNull ConveyorBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        float offset = blockEntity.getItemOffset(partialTick);
        ItemStack stack = blockEntity.getStack(0);
        if(stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        Direction dir = blockEntity.getDirection();
        poseStack.translate(0.5 + dir.getStepX() * offset, 0.60, 0.5 + dir.getStepZ() * offset);
        // アイテムを寝かせる
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        // 少し小さく
        poseStack.scale(0.6f,0.6f,0.6f);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }
}