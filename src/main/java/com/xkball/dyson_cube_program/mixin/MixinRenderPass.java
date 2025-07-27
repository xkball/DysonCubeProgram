package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.SimpleSSBO;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(RenderPass.class)
public interface MixinRenderPass extends IExtendedRenderPass {
    
    @Override
    default void dysonCubeProgram$setSSBO(String name, SimpleSSBO ssbo) {}
    
    @Override
    default Map<String, SimpleSSBO> dysonCubeProgram$getSSBOs(){
        return null;
    }
    
    @Override
    default GlRenderPipeline dysonCubeProgram$getGLRenderPipeline(){
        return null;
    }
}
