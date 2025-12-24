package com.xkball.dyson_cube_program.client.b3d.mesh;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class MeshBundleWithRenderType extends MeshBundle<RenderType> {
    
    private GpuBufferSlice contextTransform;
    
    public MeshBundleWithRenderType(String name, RenderType renderSettings) {
        super(name, renderSettings);
    }
    
    public MeshBundleWithRenderType(String name, RenderType renderSettings, List<MeshBlock> meshes) {
        super(name, renderSettings, meshes);
    }
    
    @Override
    public void beforeSetupRenderPass() {
        this.contextTransform = RenderSystem.getDynamicUniforms()
                .writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                        new Vector3f(),
                        this.renderSettings.state.textureTransform.getMatrix()
                );
    }
    
    @Override
    public void setupRenderPass(RenderPass renderPass) {
        renderPass.setPipeline(renderSettings.pipeline());
        ScissorState scissorstate = RenderSystem.getScissorStateForRenderTypeDraws();
        if (scissorstate.enabled()) {
            renderPass.enableScissor(scissorstate.x(), scissorstate.y(), scissorstate.width(), scissorstate.height());
        }
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", contextTransform);
        for(var entry : renderSettings.state.getTextures().entrySet()) {
            renderPass.bindTexture(entry.getKey(), entry.getValue().textureView(), entry.getValue().sampler());
        }
    }
    
    @Override
    public void endRenderPass(RenderPass renderPass) {
    }
    
    @Override
    public @Nullable GpuTextureView getColorTarget() {
        RenderTarget rendertarget = renderSettings.state.outputTarget.getRenderTarget();
        return RenderSystem.outputColorTextureOverride != null
                ? RenderSystem.outputColorTextureOverride
                : rendertarget.getColorTextureView();
        
    }
    
    @Override
    public @Nullable GpuTextureView getDepthTarget() {
        RenderTarget rendertarget =renderSettings.state.outputTarget.getRenderTarget();
        return rendertarget.useDepth
                ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : rendertarget.getDepthTextureView())
                : null;
        
    }
    
    @Override
    public VertexFormat.Mode getVertexFormatMode() {
        return renderSettings.mode();
    }
    
    @Override
    public VertexFormat getVertexFormat() {
        return renderSettings.format();
    }
}
