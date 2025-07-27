package com.xkball.dyson_cube_program.api.client.mixin;

import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.SimpleSSBO;

import java.util.Map;

public interface IExtendedRenderPass {
    
    void dysonCubeProgram$setSSBO(String name, SimpleSSBO ssbo);
    
    Map<String, SimpleSSBO> dysonCubeProgram$getSSBOs();
    
    GlRenderPipeline dysonCubeProgram$getGLRenderPipeline();
    
    static IExtendedRenderPass cast(Object obj){
        return (IExtendedRenderPass)obj;
    }
}
