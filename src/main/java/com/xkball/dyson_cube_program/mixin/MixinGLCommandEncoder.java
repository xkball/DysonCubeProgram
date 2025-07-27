package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedGLProgram;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(GlCommandEncoder.class)
public class MixinGLCommandEncoder {
    
    @Inject(method = "trySetup",at = @At("RETURN"))
    public void onTrySetup(GlRenderPass renderPass, Collection<String> uniforms, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValueZ()){
            var extendedRenderPass = IExtendedRenderPass.cast(renderPass);
            var glprogram = extendedRenderPass.dysonCubeProgram$getGLRenderPipeline().program();
            var extendedGLProgram = IExtendedGLProgram.cast(glprogram);
            
            for(var entry : extendedGLProgram.dysonCubeProgram$getSSBOByName().entrySet()){
                if(extendedRenderPass.dysonCubeProgram$getSSBOs().containsKey(entry.getKey())){
                    var index = entry.getValue().binding();
                    var buffer = extendedRenderPass.dysonCubeProgram$getSSBOs().get(entry.getKey());
                    //todo 绑定ssbo
                }
            }
        }
    }
}
