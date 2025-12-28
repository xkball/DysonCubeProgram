package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.lwjgl.opengl.NVCommandList;
import org.lwjgl.system.MemoryUtil;

public class CommandBufferBuilder {
    
    private static final int[] COMMAND_HEADER = new int[19];
    private final ByteBufferBuilder buffer = new ByteBufferBuilder(1024);
    private final LongArrayList indirects = new LongArrayList();
    private final IntArrayList sizes = new IntArrayList();
    
    public CommandBufferBuilder(){
        if(COMMAND_HEADER[0] == 0){
            initCommandHeader();
        }
    }
    
    private void initCommandHeader(){
        COMMAND_HEADER[NVCommandList.GL_TERMINATE_SEQUENCE_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_TERMINATE_SEQUENCE_COMMAND_NV,4);
        COMMAND_HEADER[NVCommandList.GL_NOP_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_NOP_COMMAND_NV,4);
        COMMAND_HEADER[NVCommandList.GL_DRAW_ELEMENTS_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ELEMENTS_COMMAND_NV,16);
        COMMAND_HEADER[NVCommandList.GL_DRAW_ARRAYS_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ARRAYS_COMMAND_NV,12);
        //这俩是啥?
        //COMMAND_HEADER[NVCommandList.GL_DRAW_ELEMENTS_STRIP_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ELEMENTS_STRIP_COMMAND_NV,16);
        //COMMAND_HEADER[NVCommandList.GL_DRAW_ARRAYS_STRIP_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ARRAYS_STRIP_COMMAND_NV,12);
        COMMAND_HEADER[NVCommandList.GL_DRAW_ELEMENTS_INSTANCED_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ELEMENTS_INSTANCED_COMMAND_NV,28);
        COMMAND_HEADER[NVCommandList.GL_DRAW_ARRAYS_INSTANCED_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ARRAYS_INSTANCED_COMMAND_NV,24);
        COMMAND_HEADER[NVCommandList.GL_ELEMENT_ADDRESS_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_ELEMENT_ADDRESS_COMMAND_NV,16);
        COMMAND_HEADER[NVCommandList.GL_ATTRIBUTE_ADDRESS_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_ATTRIBUTE_ADDRESS_COMMAND_NV,16);
        COMMAND_HEADER[NVCommandList.GL_UNIFORM_ADDRESS_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_UNIFORM_ADDRESS_COMMAND_NV,16);
        COMMAND_HEADER[NVCommandList.GL_BLEND_COLOR_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_BLEND_COLOR_COMMAND_NV,20);
        COMMAND_HEADER[NVCommandList.GL_STENCIL_REF_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_STENCIL_REF_COMMAND_NV,12);
        COMMAND_HEADER[NVCommandList.GL_LINE_WIDTH_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_LINE_WIDTH_COMMAND_NV,8);
        COMMAND_HEADER[NVCommandList.GL_POLYGON_OFFSET_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_POLYGON_OFFSET_COMMAND_NV,12);
        COMMAND_HEADER[NVCommandList.GL_ALPHA_REF_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_ALPHA_REF_COMMAND_NV,8);
        COMMAND_HEADER[NVCommandList.GL_VIEWPORT_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_VIEWPORT_COMMAND_NV,20);
        COMMAND_HEADER[NVCommandList.GL_SCISSOR_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_SCISSOR_COMMAND_NV,20);
        COMMAND_HEADER[NVCommandList.GL_FRONT_FACE_COMMAND_NV] = NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_FRONT_FACE_COMMAND_NV,8);
    }
    
    public void drawArrays(int count, int first){
        var pointer = this.buffer.reserve(12);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_DRAW_ARRAYS_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, count);
        MemoryUtil.memPutInt(pointer + 8L, first);
        this.indirects.add(12+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(12);
    }
    
    public void drawElements(int count, int firstIndex, int baseVertex){
        var pointer = this.buffer.reserve(16);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_DRAW_ELEMENTS_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, count);
        MemoryUtil.memPutInt(pointer + 8L, firstIndex);
        MemoryUtil.memPutInt(pointer + 12L, baseVertex);
        this.indirects.add(16+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(16);
    }
    
    public void drawArraysInstanced(int mode, int count, int instanceCount, int first, int baseInstance){
        var pointer = this.buffer.reserve(24);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_DRAW_ARRAYS_INSTANCED_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, mode);
        MemoryUtil.memPutInt(pointer + 8L, count);
        MemoryUtil.memPutInt(pointer + 12L, instanceCount);
        MemoryUtil.memPutInt(pointer + 16L, first);
        MemoryUtil.memPutInt(pointer + 20L, baseInstance);
        this.indirects.add(24+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(24);
    }
    
    public void drawElementsInstanced(int mode, int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance){
        var pointer = this.buffer.reserve(28);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_DRAW_ELEMENTS_INSTANCED_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, mode);
        MemoryUtil.memPutInt(pointer + 8L, count);
        MemoryUtil.memPutInt(pointer + 12L, instanceCount);
        MemoryUtil.memPutInt(pointer + 16L, firstIndex);
        MemoryUtil.memPutInt(pointer + 20L, baseVertex);
        MemoryUtil.memPutInt(pointer + 24L, baseInstance);
        this.indirects.add(28+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(28);
    }
    
    public void bindVBO(int buffer, int binding){
        var addr = ClientUtils.getNamedBufferAddrNV(buffer);
        var pointer = this.buffer.reserve(16);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_ATTRIBUTE_ADDRESS_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, binding);
        MemoryUtil.memPutInt(pointer + 8L, VanillaUtils.getAddrHi(addr));
        MemoryUtil.memPutInt(pointer + 12L, VanillaUtils.getAddrLo(addr));
        this.indirects.add(16+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(16);
    }
    
    public void bindEBO(int buffer, VertexFormat.IndexType indexType){
        var addr = ClientUtils.getNamedBufferAddrNV(buffer);
        var pointer = this.buffer.reserve(16);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_ELEMENT_ADDRESS_COMMAND_NV]);
        MemoryUtil.memPutInt(pointer + 4L, VanillaUtils.getAddrHi(addr));
        MemoryUtil.memPutInt(pointer + 8L, VanillaUtils.getAddrLo(addr));
        MemoryUtil.memPutInt(pointer + 12L, indexType.bytes);
        this.indirects.add(16+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(16);
    }
    
    public void bindUBO(int buffer, int binding, int offset){
        this.bindUBO(buffer, binding, offset, NVTokenShaderStage.NVTOKEN_STAGE_VERTEX);
        this.bindUBO(buffer, binding, offset, NVTokenShaderStage.NVTOKEN_STAGE_GEOMETRY);
        this.bindUBO(buffer, binding, offset, NVTokenShaderStage.NVTOKEN_STAGE_FRAGMENT);
    }
    
    public void bindUBO(int buffer, int binding, int offset, NVTokenShaderStage stage){
        assert offset % 256 == 0;
        var addr = ClientUtils.getNamedBufferAddrNV(buffer);
        addr += offset;
        var pointer = this.buffer.reserve(16);
        MemoryUtil.memPutInt(pointer, COMMAND_HEADER[NVCommandList.GL_UNIFORM_ADDRESS_COMMAND_NV]);
        MemoryUtil.memPutShort(pointer + 4L, (short) binding);
        MemoryUtil.memPutShort(pointer + 8L, (short) stage.ordinal());
        MemoryUtil.memPutInt(pointer + 8L, VanillaUtils.getAddrHi(addr));
        MemoryUtil.memPutInt(pointer + 12L, VanillaUtils.getAddrLo(addr));
        this.indirects.add(16+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(16);
    }
    
    public ByteBufferBuilder.Result build(){
        return this.buffer.build();
    }
    
    public LongArrayList getIndirects() {
        return indirects;
    }
    
    public IntArrayList getSizes() {
        return sizes;
    }
    
    public ByteBufferBuilder getBuffer() {
        return buffer;
    }
    
}
