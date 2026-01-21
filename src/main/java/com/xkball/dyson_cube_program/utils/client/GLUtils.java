package com.xkball.dyson_cube_program.utils.client;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationGpuTexture;
import org.lwjgl.opengl.NVShaderBufferLoad;

import java.util.Objects;

public class GLUtils {
    
    public static int getGLId(GpuTextureView view){
        return getGLId(view.texture());
    }
    
    public static int getGLId(GpuTexture texture){
        if(texture instanceof ValidationGpuTexture validationGpuTexture) return getGLId(validationGpuTexture.getRealTexture());
        if(texture instanceof GlTexture glTexture) return glTexture.id;
        throw new IllegalStateException("Cannot get texture id from: " + texture);
    }
    
    public static void clear(RenderTarget target){
        clear(target, true);
    }
    
    public static void clear(RenderTarget target, boolean clearDepth){
        if(target.useDepth && clearDepth){
            ClientUtils.getCommandEncoder().clearColorAndDepthTextures(Objects.requireNonNull(target.getColorTexture()),0,Objects.requireNonNull(target.getDepthTexture()),1d);
        }
        else {
            ClientUtils.getCommandEncoder().clearColorTexture(Objects.requireNonNull(target.getColorTexture()),0);
        }
    }
    
    public static void copyFrameBufferColorTo(RenderTarget from, RenderTarget to) {
            ClientUtils.getCommandEncoder().copyTextureToTexture(Objects.requireNonNull(from.getColorTexture()), Objects.requireNonNull(to.getColorTexture()),0, 0, 0, 0, 0, from.width, from.height);
    }
    
    public static void copyFrameBufferDepthTo(RenderTarget from, RenderTarget to) {
        to.copyDepthFrom(from);
    }
    
    public static long getNamedBufferAddrNV(int buffer){
        var result = NVShaderBufferLoad.glGetNamedBufferParameterui64NV(buffer, NVShaderBufferLoad.GL_BUFFER_GPU_ADDRESS_NV);
        NVShaderBufferLoad.glMakeNamedBufferResidentNV(buffer, GlConst.GL_READ_ONLY);
        return result;
    }
}
