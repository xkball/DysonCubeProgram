package com.xkball.dyson_cube_program.client;

import com.xkball.dyson_cube_program.utils.VanillaUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

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
    
    @SubscribeEventEnhanced
    public static void onRegAdditional(ModelEvent.RegisterAdditional event){
        event.register(Models.DYSON_NODE);
    }
    
    public static class Models{
        public static final ModelResourceLocation DYSON_NODE = ModelResourceLocation.standalone(VanillaUtils.modRL("additional/dyson_node"));
    }
}
