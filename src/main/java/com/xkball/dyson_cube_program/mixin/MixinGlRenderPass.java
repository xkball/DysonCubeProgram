package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import com.xkball.dyson_cube_program.client.render_pipeline.ExtendedRenderPipeline;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.SimpleSSBO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(GlRenderPass.class)
public class MixinGlRenderPass implements IExtendedRenderPass {
    
    @Shadow @Nullable protected GlRenderPipeline pipeline;
    @Unique
    protected final Map<String, SimpleSSBO> dysonCubeProgram$ssbo = new HashMap<>();
    
    @Inject(method = "setPipeline", at = @At("RETURN"))
    public void afterSetPipeline(RenderPipeline pipeline, CallbackInfo ci){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            extendedRenderPipeline.apply((RenderPass) this);
        }
    }
    
    @Override
    public void dysonCubeProgram$setSSBO(String name, SimpleSSBO ssbo) {
        dysonCubeProgram$ssbo.put(name, ssbo);
    }
    
    @Override
    public GlRenderPipeline dysonCubeProgram$getGLRenderPipeline() {
        return pipeline;
    }
    
    @Override
    public Map<String, SimpleSSBO> dysonCubeProgram$getSSBOs() {
        return dysonCubeProgram$ssbo;
    }
}
