package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import org.jspecify.annotations.Nullable;

@NonNullByDefault
public class FakeCommandEncoder extends GlCommandEncoder {
    
    public static FakeCommandEncoder INSTANCE;
    public @Nullable GLCommandList cmdList;
    
    public FakeCommandEncoder(GlDevice device){
        super(device);
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
        var mode = GlConst.toGl(pipeline.info().getVertexFormatMode());
        if(indexType != null){
            GlStateManager._glBindBuffer(34963, ((GlBuffer)renderPass.indexBuffer).handle);
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
