package com.xkball.dyson_cube_program.client.b3d.extension;

import com.mojang.blaze3d.opengl.GlStateManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.NVCommandList;
import org.lwjgl.system.MemoryStack;

public class GLCommandList implements AutoCloseable {
    
    private final IntArrayList stateObjects = new IntArrayList();
    private final CommandBufferBuilder buffer = new CommandBufferBuilder();
    private final IntArrayList fbos = new IntArrayList();
    private int cmdList = -1;
    
    public void collectDrawArrays(int mode, int firstIndex, int indexCount) {
        this.captureState(mode);
        this.buffer.drawArrays(indexCount, firstIndex);
    }
    
    public void collectDrawArraysInstanced(int mode, int index, int count, int primCount) {
        this.captureState(mode);
        this.buffer.drawArraysInstanced(mode, count, primCount, index, 0);
    }
    
    public void collectDrawElements(int mode, int count, int firstIndex, int baseVertex) {
        this.captureState(mode);
        this.buffer.drawElements(count, firstIndex, baseVertex);
    }
    
    public void collectDrawElementsInstanced(int mode, int count, int instanceCount, int firstIndex, int baseVertex, int baseInstance) {
        this.captureState(mode);
        this.buffer.drawElementsInstanced(mode, count, instanceCount, firstIndex, baseVertex, baseInstance);
    }
    
    private void captureState(int mode) {
        var so = StateObjectCache.INSTANCE.getOrCreateStateObject();
        stateObjects.add(so);
        fbos.add(GlStateManager.getFrameBuffer(GL40.GL_DRAW_FRAMEBUFFER));
        NVCommandList.glStateCaptureNV(so, mode);
    }
    
    public void createCommandList() {
        this.cmdList = GL40.glGenBuffers();
        try (var buffer = this.buffer.build()) {
            ARBDirectStateAccess.glNamedBufferStorage(this.cmdList, buffer.byteBuffer(), 0);
        }
    }
    
    public void draw() {
        if (buffer.getSizes().isEmpty()) return;
        if (this.cmdList == -1) this.createCommandList();
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            var states = stack.mallocInt(stateObjects.size());
            states.put(stateObjects.elements(), 0, stateObjects.size());
            states.flip();
            var indirects = stack.mallocPointer(buffer.getIndirects().size());
            indirects.put(buffer.getIndirects().elements(), 0, buffer.getIndirects().size());
            indirects.flip();
            var sizes = stack.mallocInt(buffer.getSizes().size());
            sizes.put(buffer.getSizes().elements(), 0, buffer.getSizes().size());
            sizes.flip();
            var fbos = stack.mallocInt(this.fbos.size());
            fbos.put(this.fbos.elements(), 0, this.fbos.size());
            fbos.flip();
            NVCommandList.glDrawCommandsStatesNV(this.cmdList, indirects, sizes, states, fbos);
        } finally {
            stack.setPointer(stackPointer);
        }
    }
    
    @Override
    public void close() {
        for (var obj : stateObjects) {
            StateObjectCache.INSTANCE.freeStateObject(obj);
        }
        this.buffer.getBuffer().close();
        if (this.cmdList != -1) {
            GL40.glDeleteBuffers(this.cmdList);
        }
    }
}
