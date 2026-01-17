package com.xkball.dyson_cube_program.client.b3d.mesh;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeshBundleWrapped<T> extends MeshBundle<T>{
    
    private final MeshBundle<T> inner;
    
    public MeshBundleWrapped(String name, T renderSettings, MeshBundle<T> inner) {
        super(name, renderSettings);
        this.inner = inner;
    }
    
    @Override
    public void setupRenderPass(RenderPass renderPass) {
        var old = inner.renderSettings;
        inner.renderSettings = this.renderSettings;
        this.inner.setupRenderPass(renderPass);
        inner.renderSettings = old;
    }
    
    @Override
    public void endRenderPass(RenderPass renderPass) {
        var old = inner.renderSettings;
        inner.renderSettings = this.renderSettings;
        this.inner.endRenderPass(renderPass);
        inner.renderSettings = old;
    }
    
    @Override
    public @Nullable GpuTextureView getColorTarget() {
        return this.inner.getColorTarget();
    }
    
    @Override
    public @Nullable GpuTextureView getDepthTarget() {
        return this.inner.getDepthTarget();
    }
    
    @Override
    public VertexFormat.Mode getVertexFormatMode() {
        return this.inner.getVertexFormatMode();
    }
    
    @Override
    public VertexFormat getVertexFormat() {
        return this.inner.getVertexFormat();
    }
    
    @Override
    public List<MeshBlock> getMeshes() {
        return this.inner.getMeshes();
    }
}
