package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.NVCommandList;
import org.lwjgl.system.MemoryStack;

public class GLCommandList implements AutoCloseable {
    
    private final IntArrayList stateObjects = new IntArrayList();
    private final CommandBufferBuilder bufferBuilder = new CommandBufferBuilder();
    private final IntArrayList fbos = new IntArrayList();
    private int tokenBuffer = -1;
    
    public void collectDrawArrays(int mode, int firstIndex, int indexCount) {
        this.captureState(mode);
        this.bufferBuilder.drawArrays(indexCount, firstIndex);
    }
    
    public void collectDrawArraysInstanced(int mode, int index, int count, int primCount) {
        this.captureState(mode);
        this.bufferBuilder.drawArraysInstanced(mode, count, primCount, index, 0);
    }
    
    public void collectDrawElements(int mode, int count, int firstIndex, int baseVertex) {
        this.captureState(mode);
        this.bufferBuilder.drawElements(count, firstIndex, baseVertex);
    }
    
    public void collectDrawElementsInstanced(int mode, int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance) {
        this.captureState(mode);
        this.bufferBuilder.drawElementsInstanced(mode, count, instanceCount, firstIndex, baseVertex, baseInstance);
    }
    
    public void collectBindVBO(int buffer, int binding) {
        this.bufferBuilder.bindVBO(buffer, binding);
    }
    
    public void collectBindEBO(int buffer, VertexFormat.IndexType indexType) {
        this.bufferBuilder.bindEBO(buffer, indexType);
    }
    
    public void collectBindUBO(int buffer, int binding, int offset) {
        this.bufferBuilder.bindUBO(buffer, binding, offset);
    }
    
    private void captureState(int mode) {
        var so = StateObjectCache.INSTANCE.getOrCreateStateObject();
        stateObjects.add(so);
        fbos.add(GlStateManager.getFrameBuffer(GL40.GL_DRAW_FRAMEBUFFER));
        NVCommandList.glStateCaptureNV(so, mode);
    }
    
    public void createCommandList() {
        this.tokenBuffer = ARBDirectStateAccess.glCreateBuffers();
        try (var buffer = this.bufferBuilder.build()) {
            ARBDirectStateAccess.glNamedBufferStorage(this.tokenBuffer, buffer.byteBuffer(), 0);
        }
    }
    
    public void draw() {
        if (bufferBuilder.getSizes().isEmpty()) return;
        if (this.tokenBuffer == -1) this.createCommandList();
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            var states = stack.mallocInt(stateObjects.size());
            states.put(stateObjects.elements(), 0, stateObjects.size());
            states.flip();
            var indirects = stack.mallocPointer(bufferBuilder.getIndirects().size());
            indirects.put(bufferBuilder.getIndirects().elements(), 0, bufferBuilder.getIndirects().size());
            indirects.flip();
            var sizes = stack.mallocInt(bufferBuilder.getSizes().size());
            sizes.put(bufferBuilder.getSizes().elements(), 0, bufferBuilder.getSizes().size());
            sizes.flip();
            var fbos = stack.mallocInt(this.fbos.size());
            fbos.put(this.fbos.elements(), 0, this.fbos.size());
            fbos.flip();
            NVCommandList.glDrawCommandsStatesNV(this.tokenBuffer, indirects, sizes, states, fbos);
            GL40.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 15,tokenBuffer);
        } finally {
            stack.setPointer(stackPointer);
        }
    }
    
    @Override
    public void close() {
        for (var obj : stateObjects) {
            StateObjectCache.INSTANCE.freeStateObject(obj);
        }
        this.bufferBuilder.getBuffer().close();
        if (this.tokenBuffer != -1) {
            GL40.glDeleteBuffers(this.tokenBuffer);
        }
    }
}
