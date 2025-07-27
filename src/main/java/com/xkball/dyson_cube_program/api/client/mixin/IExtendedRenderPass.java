package com.xkball.dyson_cube_program.api.client.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;

import java.util.Map;

public interface IExtendedRenderPass {
    
    void setSSBO(String name, GpuBufferSlice ssbo);
    
    Map<String, GpuBufferSlice> dysonCubeProgram$getSSBOs();
    
    GlRenderPipeline dysonCubeProgram$getGLRenderPipeline();
    
    static IExtendedRenderPass cast(Object obj){
        return (IExtendedRenderPass)obj;
    }
}
