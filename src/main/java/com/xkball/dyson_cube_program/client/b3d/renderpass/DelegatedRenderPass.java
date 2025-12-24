package com.xkball.dyson_cube_program.client.b3d.renderpass;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.mixin.MixinValidationRenderPassAccess;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationRenderPass;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

@NonNullByDefault
public class DelegatedRenderPass implements RenderPass {
    
    protected final RenderPass delegate;
    
    public DelegatedRenderPass(RenderPass delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void pushDebugGroup(Supplier<String> name) {
        delegate.pushDebugGroup(name);
    }
    
    @Override
    public void popDebugGroup() {
        delegate.popDebugGroup();
    }
    
    @Override
    public void setPipeline(RenderPipeline pipeline) {
        delegate.setPipeline(pipeline);
    }
    
    @Override
    public void bindTexture(String p_410511_, @Nullable GpuTextureView p_423573_, @Nullable GpuSampler p_456187_) {
        delegate.bindTexture(p_410511_, p_423573_, p_456187_);
    }
    
    @Override
    public void setUniform(String name, GpuBuffer buffer) {
        delegate.setUniform(name, buffer);
    }
    
    @Override
    public void setUniform(String name, GpuBufferSlice bufferSlice) {
        delegate.setUniform(name, bufferSlice);
    }
    
    @Override
    public void setViewport(int x, int y, int width, int height) {
        delegate.setViewport(x, y, width, height);
    }
    
    @Override
    public void enableScissor(int x, int y, int width, int height) {
        delegate.enableScissor(x, y, width, height);
    }
    
    @Override
    public void disableScissor() {
        delegate.disableScissor();
    }
    
    @Override
    public void setVertexBuffer(int index, GpuBuffer buffer) {
        delegate.setVertexBuffer(index, buffer);
    }
    
    @Override
    public void setIndexBuffer(GpuBuffer indexBuffer, VertexFormat.IndexType indexType) {
        delegate.setIndexBuffer(indexBuffer, indexType);
    }
    
    @Override
    public void drawIndexed(int firstIndex, int index, int indexCount, int primCount) {
        delegate.drawIndexed(firstIndex, index, indexCount, primCount);
    }
    
    @Override
    public <T> void drawMultipleIndexed(Collection<Draw<T>> draws, @Nullable GpuBuffer indexBuffer, VertexFormat.@Nullable IndexType indexType, Collection<String> uniformNames, T userData) {
        delegate.drawMultipleIndexed(draws, indexBuffer, indexType, uniformNames, userData);
    }
    
    @Override
    public void draw(int firstIndex, int indexCount) {
        delegate.draw(firstIndex, indexCount);
    }
    
    @Override
    public void close() {
        delegate.close();
    }
    
    public GlRenderPass getGlRenderPass(){
        return (GlRenderPass) switch (delegate) {
            case GlRenderPass glRenderPass -> glRenderPass;
            case ValidationRenderPass validationRenderPass -> ((MixinValidationRenderPassAccess)validationRenderPass).getRealRenderPass();
            case DelegatedRenderPass delegatedRenderPass -> delegatedRenderPass.getGlRenderPass();
            default ->
                    throw new IllegalStateException("Cannot find GlRenderPass from: " + delegate.getClass().getName());
        };
    }
    
}
