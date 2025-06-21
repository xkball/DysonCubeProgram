package com.xkball.dyson_cube_program.client.renderer.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.joml.Vector3f;

@NonNullByDefault
public class DebugEntityBlockRenderer implements BlockEntityRenderer<DebugEntityBlockEntity> {
    
    @SuppressWarnings("unused")
    public DebugEntityBlockRenderer(BlockEntityRendererProvider.Context context) {
    
    }
    
    @Override
    public void render(DebugEntityBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        TheSunRenderer.drawSunAt(poseStack,new Vector3f(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()),TheSunRenderer.SUN_COLOR);
    }
    
}
