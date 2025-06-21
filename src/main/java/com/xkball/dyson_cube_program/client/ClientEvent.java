package com.xkball.dyson_cube_program.client;

import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.joml.Vector3f;

import java.text.NumberFormat;

public class ClientEvent {
    
    public static long tickCount = 0;
    
    @SubscribeEventEnhanced
    public static void onClientTick(ClientTickEvent.Pre event) {
        tickCount+=1;
    }
    
    @SubscribeEventEnhanced
    public static void onRegGuiLayerDef(RegisterGuiLayersEvent event){
        if(VanillaUtils.DEBUG){
            event.registerAboveAll(VanillaUtils.modRL("debug"),(guiGraphics, partialTicks) -> {
                var font = Minecraft.getInstance().font;
                var numFormat = NumberFormat.getInstance();
                numFormat.setMaximumFractionDigits(4);
                numFormat.setMinimumFractionDigits(4);
                
            });
        }

    }
}
