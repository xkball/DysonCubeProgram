package com.xkball.dyson_cube_program.client;

import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

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
    public static void onRegRenderPipeline(RegisterRenderPipelinesEvent event){
        event.registerPipeline(DCPRenderPipelines.DEBUG_LINE);
        event.registerPipeline(DCPRenderPipelines.SUN_0);
        event.registerPipeline(DCPRenderPipelines.SUN_1);
        event.registerPipeline(DCPRenderPipelines.SUN_2);
        event.registerPipeline(DCPRenderPipelines.BLOOM_DOWN_SAMPLER);
        event.registerPipeline(DCPRenderPipelines.BLOOM_COMPOSITE);
        event.registerPipeline(DCPRenderPipelines.POSITION_COLOR_INSTANCED);
        event.registerPipeline(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED);
    }
    
}
