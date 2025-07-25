package com.xkball.dyson_cube_program.client.render_pipeline.mesh;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class MeshBundleWithRenderPipeline extends MeshBundle<RenderPipeline> {
    
    public MeshBundleWithRenderPipeline(String name, RenderPipeline renderSettings) {
        super(name, renderSettings);
    }
    
    public MeshBundleWithRenderPipeline(String name, RenderPipeline renderSettings, List<Pair<Consumer<PoseStack>, CachedMesh>> meshes) {
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
        return Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
    }
    
    @Override
    public @Nullable GpuTextureView getDepthTarget() {
        return Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
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
