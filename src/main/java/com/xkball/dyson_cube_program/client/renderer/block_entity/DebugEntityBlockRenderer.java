package com.xkball.dyson_cube_program.client.renderer.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.client.renderer.dysonsphere.DysonSphereRenderer;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.test.DysonBluePrintTest;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

@NonNullByDefault
public class DebugEntityBlockRenderer implements BlockEntityRenderer<DebugEntityBlockEntity,DebugEntityState> {
    
    public final DysonSphereRenderer sphereRenderer;
    
    @SuppressWarnings("unused")
    public DebugEntityBlockRenderer(BlockEntityRendererProvider.Context context) {
        var bp = DysonSpareBlueprintData.parse(DysonBluePrintTest.bp);
        this.sphereRenderer = new DysonSphereRenderer(bp);
        this.sphereRenderer.buildMeshes();
    }
    
    @Override
    public AABB getRenderBoundingBox(DebugEntityBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
    
    public void render(DebugEntityState blockEntity, PoseStack poseStack) {
        poseStack.pushPose();
        var scale = 1/5000f;
        poseStack.scale(-1,1,1);
        poseStack.scale(scale, scale, scale);
        sphereRenderer.render(poseStack);
        poseStack.popPose();
        
        TheSunRenderer.drawSunAt(poseStack,new Vector3f(blockEntity.blockPos.getX(), blockEntity.blockPos.getY(), blockEntity.blockPos.getZ()),TheSunRenderer.SUN_COLOR);
        
//        try(var renderPass = ClientUtils.createRenderPass("instance_test")){
//            renderPass.setPipeline(DCPRenderPipelines.POSITION_COLOR_INSTANCED);
//        }
    }
    
    @Override
    public DebugEntityState createRenderState() {
        return new DebugEntityState();
    }
    
    @Override
    public void submit(DebugEntityState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(),(p, v) -> render(renderState,ClientUtils.fromPose(p)));
    }
}
