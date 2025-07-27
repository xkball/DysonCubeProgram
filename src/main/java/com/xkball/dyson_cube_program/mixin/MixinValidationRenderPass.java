package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationRenderPass;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ValidationRenderPass.class)
public class MixinValidationRenderPass implements IExtendedRenderPass {
    
    @Shadow @Final private RenderPass realRenderPass;
    
    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public void setSSBO(String name, GpuBufferSlice ssbo) {
        IExtendedRenderPass.cast(this.realRenderPass).setSSBO(name, ssbo);
    }
    
    @Override
    public GlRenderPipeline dysonCubeProgram$getGLRenderPipeline() {
        return IExtendedRenderPass.cast(this.realRenderPass).dysonCubeProgram$getGLRenderPipeline();
    }
    
    @Override
    public Map<String, GpuBufferSlice> dysonCubeProgram$getSSBOs() {
        return IExtendedRenderPass.cast(this.realRenderPass).dysonCubeProgram$getSSBOs();
    }
}
