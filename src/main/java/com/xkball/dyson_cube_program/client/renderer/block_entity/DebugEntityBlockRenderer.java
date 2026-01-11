package com.xkball.dyson_cube_program.client.renderer.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.b3d.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.client.renderer.dysonsphere.DysonSphereRenderer;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.test.DysonBluePrintTest;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
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
    
    public void render(Vector3f pos, PoseStack poseStack) {
        DCPUniforms.CUSTOM_COLOR_MODULATOR.updateUnsafe(b -> b.putVec4(ColorUtils.Vectorization.argbColor(TheSunRenderer.SUN_COLOR)));
        poseStack.pushPose();
        var scale = 1/5000f;
        poseStack.scale(-1,1,1);
        poseStack.scale(scale, scale, scale);
        sphereRenderer.render(poseStack);
        poseStack.popPose();
        TheSunRenderer.drawSunAt(poseStack,pos,TheSunRenderer.SUN_COLOR);
    }
    
    @Override
    public DebugEntityState createRenderState() {
        return new DebugEntityState();
    }
    
    @Override
    public void submit(DebugEntityState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(),(p, v) -> render(new Vector3f(renderState.blockPos.getX(), renderState.blockPos.getY(), renderState.blockPos.getZ()),ClientUtils.fromPose(p)));
    }
}
