package com.xkball.dyson_cube_program.client.render_pipeline.mesh;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
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
                        RenderSystem.getTextureMatrix(),
                        RenderSystem.getShaderLineWidth()
                );
    }
    
    @Override
    public void setupRenderPass(RenderPass renderPass) {
        if(!(this.renderSettings instanceof RenderType.CompositeRenderType renderType)){
            throw new IllegalStateException("Only support CompositeRenderType for now.");
        }
        else{
            this.renderSettings.setupRenderState();
            renderPass.setPipeline(renderType.renderPipeline);
            ScissorState scissorstate = RenderSystem.getScissorStateForRenderTypeDraws();
            if (scissorstate.enabled()) {
                renderPass.enableScissor(scissorstate.x(), scissorstate.y(), scissorstate.width(), scissorstate.height());
            }
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", contextTransform);
            for (int i = 0; i < 12; i++) {
                GpuTextureView sampler = RenderSystem.getShaderTexture(i);
                if (sampler != null) {
                    renderPass.bindSampler("Sampler" + i, sampler);
                }
            }
        }
      
    }
    
    @Override
    public void endRenderPass(RenderPass renderPass) {
        this.renderSettings.clearRenderState();
    }
    
    @Override
    public @Nullable GpuTextureView getColorTarget() {
        if(!(this.renderSettings instanceof RenderType.CompositeRenderType renderType)){
            return null;
        }
        else{
            RenderTarget rendertarget = renderType.state.outputState.getRenderTarget();
            return RenderSystem.outputColorTextureOverride != null
                    ? RenderSystem.outputColorTextureOverride
                    : rendertarget.getColorTextureView();
        }
    }
    
    @Override
    public @Nullable GpuTextureView getDepthTarget() {
        if(!(this.renderSettings instanceof RenderType.CompositeRenderType renderType)){
            return null;
        }
        else{
            RenderTarget rendertarget = renderType.state.outputState.getRenderTarget();
            return rendertarget.useDepth
                    ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : rendertarget.getDepthTextureView())
                    : null;
        }
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
