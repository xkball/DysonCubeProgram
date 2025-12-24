package com.xkball.dyson_cube_program.client.b3d.extension;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.lwjgl.opengl.NVCommandList;
import org.lwjgl.system.MemoryStack;

public class StateObjectCache implements AutoCloseable{
    
    public static StateObjectCache INSTANCE;
    private final IntArrayList stateObjects = new IntArrayList();
    private final IntArrayFIFOQueue freeStateObjects = new IntArrayFIFOQueue();
    
    public int getOrCreateStateObject(){
        if(!freeStateObjects.isEmpty()){
            return freeStateObjects.dequeueInt();
        }
        else {
            var state = NVCommandList.glCreateStatesNV();
            stateObjects.add(state);
            return state;
        }
    }
    
    public void freeStateObject(int state){
        freeStateObjects.enqueue(state);
    }
    
    @Override
    public void close() {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            var buffer = stack.mallocInt(stateObjects.size());
            buffer.put(stateObjects.elements());
            buffer.flip();
            NVCommandList.glDeleteStatesNV(buffer);
        } finally {
            stack.setPointer(stackPointer);
        }
    }
    
}
