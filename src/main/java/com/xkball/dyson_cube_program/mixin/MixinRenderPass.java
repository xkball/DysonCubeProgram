package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(RenderPass.class)
public interface MixinRenderPass extends IExtendedRenderPass {
    
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    default void setSSBO(String name, GpuBufferSlice ssbo) {}
    
    @Override
    default Map<String, GpuBufferSlice> dysonCubeProgram$getSSBOs(){
        return null;
    }
    
    @Override
    default GlRenderPipeline dysonCubeProgram$getGLRenderPipeline(){
        return null;
    }
}
