package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedGLProgram;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import com.xkball.dyson_cube_program.client.b3d.pipeline.ExtendedRenderPipeline;
import com.xkball.dyson_cube_program.utils.client.GLUtils;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;

import static org.lwjgl.system.MemoryStack.stackGet;

@Mixin(GlCommandEncoder.class)
public class MixinGLCommandEncoder {
    
    @Unique
    private GpuTextureView dysonCubeProgram$fboColor;
    @Unique
    private boolean dysonCubeProgram$needClearDrawBuffers = false;
    
    @Inject(method = "createRenderPass(Ljava/util/function/Supplier;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalInt;Lcom/mojang/blaze3d/textures/GpuTextureView;Ljava/util/OptionalDouble;)Lcom/mojang/blaze3d/systems/RenderPass;",at = @At("HEAD"))
    public void onCreateRenderPass(Supplier<String> p_419957_, GpuTextureView color, OptionalInt p_410460_, @Nullable GpuTextureView p_423565_, OptionalDouble p_423486_, CallbackInfoReturnable<RenderPass> cir){
        this.dysonCubeProgram$fboColor = color;
    }
    
    @Inject(method = "trySetup",at = @At("RETURN"))
    public void onTrySetup(GlRenderPass renderPass, Collection<String> uniforms, CallbackInfoReturnable<Boolean> cir){
        if(cir.getReturnValueZ()){
            var pass = IExtendedRenderPass.cast(renderPass);
            var glprogram = pass.dysonCubeProgram$getGLRenderPipeline().program();
            var program = IExtendedGLProgram.cast(glprogram);
            var pipeline = pass.dysonCubeProgram$getGLRenderPipeline();
            for(var entry : program.dysonCubeProgram$getSSBOByName().entrySet()){
                if(pass.dysonCubeProgram$getSSBOs().containsKey(entry.getKey())){
                    var index = entry.getValue().binding();
                    var buffer = pass.dysonCubeProgram$getSSBOs().get(entry.getKey());
                    GL43.glBindBufferRange(GL43.GL_SHADER_STORAGE_BUFFER,index,((GlBuffer)buffer.buffer()).handle,buffer.offset(),buffer.length());
                }
            }
            if(pipeline.info() instanceof ExtendedRenderPipeline ePipeline){
                var mrtBindings = ePipeline.multiTargetBindings;
                if(!mrtBindings.isEmpty()){
                    this.dysonCubeProgram$needClearDrawBuffers = true;
                    MemoryStack stack = stackGet();
                    int stackPointer = stack.getPointer();
                    try {
                        IntBuffer buf = stack.mallocInt(mrtBindings.size() + 1);
                        buf.put(GL30.GL_COLOR_ATTACHMENT0);
                        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, GLUtils.getGLId(this.dysonCubeProgram$fboColor), 0);
                        for(var entry : mrtBindings){
                            buf.put(GL30.GL_COLOR_ATTACHMENT0 + entry.getFirst());
                            var tex = GLUtils.getGLId(entry.getSecond().get());
                            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + entry.getFirst(), GL30.GL_TEXTURE_2D, tex, 0);
                        }
                        buf.flip();
                        GL30.glDrawBuffers(buf);
                    } finally {
                        stack.setPointer(stackPointer);
                    }
                    
                }
            }
        }
    }
    
    @Inject(method = "finishRenderPass",at = @At("HEAD"))
    public void onEndRenderPass(CallbackInfo ci){
        if(this.dysonCubeProgram$needClearDrawBuffers){
            this.dysonCubeProgram$needClearDrawBuffers = false;
            GL30.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
        }
        this.dysonCubeProgram$fboColor = null;
    }
}
