package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.dyson_cube_program.client.render_pipeline.ExtendedRenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlRenderPass.class)
public class MixinGlRenderPass {
    
    @Inject(method = "setPipeline", at = @At("RETURN"))
    public void afterSetPipeline(RenderPipeline pipeline, CallbackInfo ci){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            extendedRenderPipeline.apply((RenderPass) this);
        }
    }
}
