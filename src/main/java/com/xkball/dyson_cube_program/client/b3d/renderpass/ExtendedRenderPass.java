package com.xkball.dyson_cube_program.client.b3d.renderpass;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.ClientConfig;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedRenderPass;
import com.xkball.dyson_cube_program.client.b3d.extension.CmdListCommandEncoder;
import com.xkball.dyson_cube_program.client.b3d.extension.GLCommandList;
import com.xkball.dyson_cube_program.client.b3d.pipeline.ExtendedRenderPipeline;
import org.jetbrains.annotations.Nullable;

@NonNullByDefault
public class ExtendedRenderPass extends DelegatedRenderPass{
    
    @Nullable
    private RenderPipeline pipeline;
    @Nullable
    private GLCommandList cmdList;
    private VertexFormat.IndexType indexType = VertexFormat.IndexType.INT;
    
    public ExtendedRenderPass(RenderPass delegate) {
        super(delegate);
    }
    
    public void useCmdList(@Nullable GLCommandList cmdList){
        this.cmdList = cmdList;
    }
    
    public void setSSBO(String name, GpuBufferSlice ssbo){
        IExtendedRenderPass.cast(this.delegate).setSSBO(name, ssbo);
    }
    
    @Override
    public void setPipeline(RenderPipeline pipeline) {
        super.setPipeline(pipeline);
        this.pipeline = pipeline;
    }
    
    @Override
    public void setIndexBuffer(GpuBuffer indexBuffer, VertexFormat.IndexType indexType) {
        super.setIndexBuffer(indexBuffer, indexType);
        this.indexType = indexType;
    }
    
    @Override
    public void drawIndexed(int firstIndex, int index, int indexCount, int primCount) {
        if(cmdList != null && pipeline != null && !ExtendedRenderPipeline.haveSSBO(pipeline) && ClientConfig.useNvCommandList()) {
            CmdListCommandEncoder.INSTANCE.cmdList = cmdList;
            CmdListCommandEncoder.INSTANCE.executeDraw(this.getGlRenderPass(), firstIndex, index, indexCount, this.indexType, primCount);
        }
        else {
            super.drawIndexed(firstIndex, index, indexCount, primCount);
        }
    }
    
    @Override
    public void draw(int firstIndex, int indexCount) {
        if(cmdList != null && pipeline != null && !ExtendedRenderPipeline.haveSSBO(pipeline) && ClientConfig.useNvCommandList()) {
            CmdListCommandEncoder.INSTANCE.cmdList = cmdList;
            CmdListCommandEncoder.INSTANCE.executeDraw(this.getGlRenderPass(), firstIndex, 0, indexCount, null, 1);
        }
        else {
            super.draw(firstIndex, indexCount);
        }
    }
}
