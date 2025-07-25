package com.xkball.dyson_cube_program.client;

import com.xkball.dyson_cube_program.api.client.IEndFrameListener;
import com.xkball.dyson_cube_program.api.client.IUpdatable;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppedEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientRenderObjects {
    
    public static final List<AutoCloseable> closeOnExit = new ArrayList<>();
    public static final List<IEndFrameListener> endFrame = new ArrayList<>();
    public static final List<IUpdatable> everyFrame = new ArrayList<>();
    
    public static void addCloseOnExit(AutoCloseable obj) {
        synchronized (closeOnExit) {
            closeOnExit.add(obj);
        }
    }
    
    public static void addEndFrameListener(IEndFrameListener listener) {
        synchronized (endFrame) {
            endFrame.add(listener);
        }
    }
    
    public static void addEveryFrameListener(IUpdatable obj) {
        synchronized (everyFrame) {
            everyFrame.add(obj);
        }
    }
    
    @SubscribeEventEnhanced
    public static void onGameExit(ClientStoppedEvent event) {
        try {
            for(var closeable : closeOnExit) {
                closeable.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        closeOnExit.clear();
    }
}
