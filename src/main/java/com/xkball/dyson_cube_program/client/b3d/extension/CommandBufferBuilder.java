package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.lwjgl.opengl.NVCommandList;
import org.lwjgl.system.MemoryUtil;

public class CommandBufferBuilder {
    
    private final ByteBufferBuilder buffer = new ByteBufferBuilder(1024);
    private final LongArrayList indirects = new LongArrayList();
    private final IntArrayList sizes = new IntArrayList();
    
    public CommandBufferBuilder(){
    
    }
    
    public void drawArrays(int count, int first){
        var pointer = this.buffer.reserve(12);
        MemoryUtil.memPutInt(pointer, NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ARRAYS_COMMAND_NV,12));
        MemoryUtil.memPutInt(pointer + 4L, count);
        MemoryUtil.memPutInt(pointer + 8L, first);
        this.indirects.add(12+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(12);
    }
    
    public void drawElements(int count, int firstIndex, int baseVertex){
        var pointer = this.buffer.reserve(16);
        MemoryUtil.memPutInt(pointer, NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ELEMENTS_COMMAND_NV,16));
        MemoryUtil.memPutInt(pointer + 4L, count);
        MemoryUtil.memPutInt(pointer + 8L, firstIndex);
        MemoryUtil.memPutInt(pointer + 12L, baseVertex);
        this.indirects.add(16+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(16);
    }
    
    public void drawArraysInstanced(int mode, int count, int instanceCount, int first, int baseInstance){
        var pointer = this.buffer.reserve(24);
        MemoryUtil.memPutInt(pointer, NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ARRAYS_INSTANCED_COMMAND_NV,24));
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
        MemoryUtil.memPutInt(pointer, NVCommandList.glGetCommandHeaderNV(NVCommandList.GL_DRAW_ELEMENTS_INSTANCED_COMMAND_NV,28));
        MemoryUtil.memPutInt(pointer + 4L, mode);
        MemoryUtil.memPutInt(pointer + 8L, count);
        MemoryUtil.memPutInt(pointer + 12L, instanceCount);
        MemoryUtil.memPutInt(pointer + 16L, firstIndex);
        MemoryUtil.memPutInt(pointer + 20L, baseVertex);
        MemoryUtil.memPutInt(pointer + 24L, baseInstance);
        this.indirects.add(28+(indirects.isEmpty() ? 0 : indirects.getLast()));
        this.sizes.add(28);
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
