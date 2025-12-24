package com.xkball.dyson_cube_program.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedGLProgram;
import com.xkball.dyson_cube_program.client.b3d.extension.FakeCommandEncoder;
import com.xkball.dyson_cube_program.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.dyson_cube_program.client.b3d.uniform.SSBOIndexStorage;
import org.lwjgl.opengl.GL43;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlDevice.class)
public class MixinGLDevice {
    
    @Inject(method = "<init>",at = @At("RETURN"))
    public void afterInit(long p_410629_, int p_410525_, boolean p_409747_, ShaderSource p_460964_, boolean p_410647_, CallbackInfo ci){
        FakeCommandEncoder.INSTANCE = new FakeCommandEncoder((GlDevice)(Object) this);
    }
    
    @Inject(method = "compileProgram", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlProgram;setupUniforms(Ljava/util/List;Ljava/util/List;)V", shift = At.Shift.AFTER))
    public void onCompilePipeline(RenderPipeline pipeline, ShaderSource p_461146_, CallbackInfoReturnable<GlRenderPipeline> cir, @Local GlProgram glprogram){
        if(pipeline instanceof ExtendedRenderPipeline extendedRenderPipeline){
            for(var ssboName : extendedRenderPipeline.SSBOs){
                var index = GL43.glGetProgramResourceIndex(glprogram.getProgramId(), org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BLOCK, ssboName);
                if(index != -1) IExtendedGLProgram.cast(glprogram).dysonCubeProgram$getSSBOByName().put(ssboName,new SSBOIndexStorage(index));
            }
        }
    }
}
