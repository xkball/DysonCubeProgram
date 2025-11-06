package com.xkball.dyson_cube_program.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xkball.dyson_cube_program.api.client.IEndFrameListener;
import com.xkball.dyson_cube_program.api.client.IUpdatable;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppedEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientRenderObjects {
    
    public final List<AutoCloseable> closeOnExit = new ArrayList<>();
    public final List<IEndFrameListener> endFrame = new ArrayList<>();
    public final List<IUpdatable> everyFrame = new ArrayList<>();
    
    public static ClientRenderObjects INSTANCE;
    
    public ClientRenderObjects() {
    
    }
    
    public void addCloseOnExit(AutoCloseable obj) {
        RenderSystem.assertOnRenderThread();
        closeOnExit.add(obj);
        
    }
    
    public void addEndFrameListener(IEndFrameListener listener) {
        RenderSystem.assertOnRenderThread();
        endFrame.add(listener);
    }
    
    public void addEveryFrameListener(IUpdatable obj) {
        RenderSystem.assertOnRenderThread();
        everyFrame.add(obj);
    }
    
    @SubscribeEventEnhanced
    public static void onGameExit(ClientStoppedEvent event) {
        try {
            for(var closeable : INSTANCE.closeOnExit) {
                closeable.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        INSTANCE.closeOnExit.clear();
    }
}
