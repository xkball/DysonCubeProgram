package com.xkball.dyson_cube_program.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xkball.dyson_cube_program.api.client.IEndFrameListener;
import com.xkball.dyson_cube_program.api.client.IUpdatable;
import com.xkball.dyson_cube_program.client.b3d.extension.StateObjectCache;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStoppedEvent;
import org.lwjgl.opengl.GLCapabilities;

import java.util.ArrayList;
import java.util.List;

public class ClientRenderObjects {
    
    public static boolean SUPPORT_NV_COMMAND_LIST = false;
    public static boolean SUPPORT_NV_SHADER_BUFFER_LOAD = false;

    public final List<AutoCloseable> closeOnExit = new ArrayList<>();
    public final List<IEndFrameListener> endFrame = new ArrayList<>();
    public final List<IUpdatable> everyFrame = new ArrayList<>();
    public final List<IUpdatable> reload = new ArrayList<>();
    
    public static ClientRenderObjects INSTANCE;
    
    public ClientRenderObjects() {
    
    }
    
    public static void init(GLCapabilities capabilities){
        INSTANCE = new ClientRenderObjects();
        SUPPORT_NV_COMMAND_LIST = capabilities.GL_NV_command_list;
        SUPPORT_NV_SHADER_BUFFER_LOAD = capabilities.GL_NV_shader_buffer_load;
        StateObjectCache.INSTANCE = new StateObjectCache();
        ClientRenderObjects.INSTANCE.addCloseOnExit(StateObjectCache.INSTANCE);
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
    
    public void addReloadListener(IUpdatable obj) {
        RenderSystem.assertOnRenderThread();
        reload.add(obj);
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
    
    @SubscribeEventEnhanced
    public static void afterReloadFinish(ClientResourceLoadFinishedEvent event){
        for(var updatable : INSTANCE.reload) {
            updatable.update();
        }
    }
}
