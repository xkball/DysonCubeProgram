package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33C;

import java.util.Collection;
import java.util.Map;

@NonNullByDefault
public class CmdListCommandEncoder extends GlCommandEncoder {
    
    public static CmdListCommandEncoder INSTANCE;
    public @Nullable GLCommandList cmdList;
    
    public CmdListCommandEncoder(GlDevice device){
        super(device);
    }
    
    @Override
    //todo 升版本时记得检查
    public boolean trySetup(GlRenderPass renderPass, Collection<String> uniforms) {
        if (renderPass.pipeline == null || renderPass.pipeline.program() == GlProgram.INVALID_PROGRAM) {
            return false;
        }
        
        RenderPipeline renderpipeline = renderPass.pipeline.info();
        GlProgram glprogram = renderPass.pipeline.program();
        this.applyPipelineState(renderpipeline);
        boolean flag1 = this.lastProgram != glprogram;
        if (flag1) {
            GlStateManager._glUseProgram(glprogram.getProgramId());
            this.lastProgram = glprogram;
        }
        
        for (Map.Entry<String, Uniform> entry1 : glprogram.getUniforms().entrySet()) {
            String s = entry1.getKey();
            boolean flag = renderPass.dirtyUniforms.contains(s);
            switch ((Uniform)entry1.getValue()) {
                case Uniform.Ubo(int j2):
                    if (flag) {
                        GpuBufferSlice gpubufferslice1 = renderPass.uniforms.get(s);
                        if(cmdList != null) cmdList.collectBindUBO(((GlBuffer)gpubufferslice1.buffer()).handle, j2, (int) gpubufferslice1.offset());
                        else GL32.glBindBufferRange(35345, j2, ((GlBuffer)gpubufferslice1.buffer()).handle, gpubufferslice1.offset(), gpubufferslice1.length());
                    }
                    break;
                case Uniform.Utb(int l, int i1, TextureFormat textureformat, int i2):
                    if (flag1 || flag) {
                        GlStateManager._glUniform1i(l, i1);
                    }
                    
                    GlStateManager._activeTexture(33984 + i1);
                    GL11C.glBindTexture(35882, i2);
                    if (flag) {
                        GpuBufferSlice gpubufferslice2 = renderPass.uniforms.get(s);
                        GL31.glTexBuffer(35882, GlConst.toGlInternalId(textureformat), ((GlBuffer)gpubufferslice2.buffer()).handle);
                    }
                    break;
                case Uniform.Sampler(int $$23, int l1):
                    GlRenderPass.TextureViewAndSampler glrenderpass$textureviewandsampler1 = renderPass.samplers.get(s);
                    if (glrenderpass$textureviewandsampler1 == null) {
                        break;
                    }
                    
                    GlTextureView gltextureview1 = glrenderpass$textureviewandsampler1.view();
                    if (flag1 || flag) {
                        GlStateManager._glUniform1i($$23, l1);
                    }
                    
                    GlStateManager._activeTexture(33984 + l1);
                    GlTexture gltexture = gltextureview1.texture();
                    int j;
                    if ((gltexture.usage() & 16) != 0) {
                        j = 34067;
                        GL11.glBindTexture(34067, gltexture.id);
                    } else {
                        j = 3553;
                        GlStateManager._bindTexture(gltexture.id);
                    }
                    
                    GL33C.glBindSampler(l1, glrenderpass$textureviewandsampler1.sampler().getId());
                    GlStateManager._texParameter(j, 33084, gltextureview1.baseMipLevel());
                    GlStateManager._texParameter(j, 33085, gltextureview1.baseMipLevel() + gltextureview1.mipLevels() - 1);
                    break;
                default:
                    throw new MatchException(null, null);
            }
        }
        
        renderPass.dirtyUniforms.clear();
        if (renderPass.isScissorEnabled()) {
            GlStateManager._enableScissorTest();
            GlStateManager._scissorBox(renderPass.getScissorX(), renderPass.getScissorY(), renderPass.getScissorWidth(), renderPass.getScissorHeight());
        } else {
            GlStateManager._disableScissorTest();
        }
        
        var stencilTestOpt = renderPass.pipeline.info().getStencilTest();
        if (stencilTestOpt.isPresent()) {
            var stencilTest = stencilTestOpt.get();
            GlStateManager._enableStencilTest();
            var front = stencilTest.front();
            var back = stencilTest.back();
            if (front.equals(back)) {
                GlStateManager._stencilFunc(GlConst.toGl(front.compare()), stencilTest.referenceValue(), stencilTest.readMask());
                GlStateManager._stencilOp(GlConst.toGl(front.fail()), GlConst.toGl(front.depthFail()), GlConst.toGl(front.pass()));
            } else {
                GlStateManager._stencilFuncFront(GlConst.toGl(front.compare()), stencilTest.referenceValue(), stencilTest.readMask());
                GlStateManager._stencilFuncBack(GlConst.toGl(back.compare()), stencilTest.referenceValue(), stencilTest.readMask());
                GlStateManager._stencilOpFront(GlConst.toGl(front.fail()), GlConst.toGl(front.depthFail()), GlConst.toGl(front.pass()));
                GlStateManager._stencilOpBack(GlConst.toGl(back.fail()), GlConst.toGl(back.depthFail()), GlConst.toGl(back.pass()));
            }
            GlStateManager._stencilMask(stencilTest.writeMask());
        } else {
            GlStateManager._disableStencilTest();
        }
        
        return true;
    }
    
    @Override
    public void drawFromBuffers(
            GlRenderPass renderPass,
            int firstIndex,
            int index,
            int indexCount,
            VertexFormat.@Nullable IndexType indexType,
            GlRenderPipeline pipeline,
            int primCount
    ){
        if(cmdList == null) return;
        //todo bind vao vbo
        var mode = GlConst.toGl(pipeline.info().getVertexFormatMode());
        if(indexType != null){
            cmdList.collectBindEBO(((GlBuffer)renderPass.indexBuffer).handle, indexType);
            if(primCount > 1){
                cmdList.collectDrawElementsInstanced(mode, indexCount, primCount, 0, firstIndex, 0);
            }
            else {
                cmdList.collectDrawElements(mode, indexCount, 0, firstIndex);
            }
        }
        else if(primCount > 1){
            cmdList.collectDrawArraysInstanced(mode, firstIndex, indexCount, primCount);
        }
        else {
            cmdList.collectDrawArrays(mode, firstIndex, indexCount);
        }
    }
}
