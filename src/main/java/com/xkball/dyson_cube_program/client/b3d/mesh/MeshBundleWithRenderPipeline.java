package com.xkball.dyson_cube_program.client.b3d.mesh;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeshBundleWithRenderPipeline extends MeshBundle<RenderPipeline> {
    
    public MeshBundleWithRenderPipeline(String name, RenderPipeline renderSettings) {
        super(name, renderSettings);
    }
    
    public MeshBundleWithRenderPipeline(String name, RenderPipeline renderSettings, List<MeshBlock> meshes) {
        super(name, renderSettings, meshes);
    }
    
    @Override
    public void setupRenderPass(RenderPass renderPass) {
        renderPass.setPipeline(renderSettings);
    }
    
    @Override
    public void endRenderPass(RenderPass renderPass) {
    
    }
    
    @Override
    public @Nullable GpuTextureView getColorTarget() {
        return RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
    }
    
    @Override
    public @Nullable GpuTextureView getDepthTarget() {
        return RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
    }
    
    @Override
    public VertexFormat.Mode getVertexFormatMode() {
        return renderSettings.getVertexFormatMode();
    }
    
    @Override
    public VertexFormat getVertexFormat() {
        return renderSettings.getVertexFormat();
    }
}
