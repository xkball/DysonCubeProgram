package com.xkball.dyson_cube_program.client.renderer.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.ClientEvent;
import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.client.renderer.dysonsphere.DysonSphereRenderer;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.test.DysonBluePrintTest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;

@NonNullByDefault
public class DebugEntityBlockRenderer implements BlockEntityRenderer<DebugEntityBlockEntity> {
    
    private final DysonSphereRenderer sphereRenderer;
    
    @SuppressWarnings("unused")
    public DebugEntityBlockRenderer(BlockEntityRendererProvider.Context context) {
        var bp = DysonSpareBlueprintData.parse(DysonBluePrintTest.bp);
        this.sphereRenderer = new DysonSphereRenderer(bp);
        this.sphereRenderer.buildMeshes();
    }
    
    @Override
    public void render(DebugEntityBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        var scale = 1/6000f;
        poseStack.scale(-1,1,1);
        poseStack.scale(scale, scale, scale);
        sphereRenderer.render(poseStack);
        poseStack.popPose();
        
        TheSunRenderer.drawSunAt(poseStack,new Vector3f(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()),TheSunRenderer.SUN_COLOR);
        
        //var modelManager = Minecraft.getInstance().getModelManager();
        //var nodeModel = modelManager.getModel(ClientEvent.Models.DYSON_NODE);
        //Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),bufferSource.getBuffer(RenderType.DEBUG_QUADS),null,nodeModel,25,25,255,0,0, ModelData.EMPTY,RenderType.DEBUG_QUADS);
    }
    
    @Override
    public AABB getRenderBoundingBox(DebugEntityBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
