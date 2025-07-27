package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedGLProgram;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import org.lwjgl.opengl.GL43;
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
            var pass = IExtendedRenderPass.cast(renderPass);
            var glprogram = pass.dysonCubeProgram$getGLRenderPipeline().program();
            var program = IExtendedGLProgram.cast(glprogram);
            
            for(var entry : program.dysonCubeProgram$getSSBOByName().entrySet()){
                if(pass.dysonCubeProgram$getSSBOs().containsKey(entry.getKey())){
                    var index = entry.getValue().binding();
                    var buffer = pass.dysonCubeProgram$getSSBOs().get(entry.getKey());
                    GL43.glBindBufferRange(GL43.GL_SHADER_STORAGE_BUFFER,index,((GlBuffer)buffer.buffer()).handle,buffer.offset(),buffer.length());
                }
            }
        }
    }
}
