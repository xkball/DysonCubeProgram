package com.xkball.dyson_cube_program.client;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import com.xkball.dyson_cube_program.client.b3d.pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.renderer.block_entity.DebugEntityBlockRenderer;
import com.xkball.dyson_cube_program.common.DCPTempReg;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import org.slf4j.Logger;

import java.text.NumberFormat;

public class ClientEvent {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    
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
    
    @SubscribeEventEnhanced
    public static void onRegClientCommand(RegisterClientCommandsEvent event){
        event.getDispatcher().register(
                Commands.literal("dyson_cube_program")
                        .then(Commands.literal("client")
                                .then(Commands.literal("rebuild_mesh").executes(ClientEvent::rebuildDysonSphereRendererMesh))
                                .then(Commands.literal("rebuild_mesh_from_clipboard").executes(ClientEvent::rebuildDysonSphereRendererMeshFromClipboard))
                                .then(Commands.literal("mesh_build_benchmark").executes(ClientEvent::meshBuildBenchmark))
        ));
    }
    
    public static int rebuildDysonSphereRendererMesh(CommandContext<CommandSourceStack> source){
        var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get());
        if(renderer instanceof DebugEntityBlockRenderer renderer_){
            var time = renderer_.sphereRenderer.buildMeshes();
            source.getSource().sendSystemMessage(Component.literal("DysonSphereRenderer buildMeshes time: "+time+"ms"));
        }
        return 0;
    }
    
    public static int rebuildDysonSphereRendererMeshFromClipboard(CommandContext<CommandSourceStack> source){
        var str = Minecraft.getInstance().keyboardHandler.getClipboard();
        try {
            var data = DysonSpareBlueprintData.parse(str);
            var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get());
            if(renderer instanceof DebugEntityBlockRenderer renderer_){
                renderer_.sphereRenderer.data = data;
                var time = renderer_.sphereRenderer.buildMeshes();
                source.getSource().sendSystemMessage(Component.literal("DysonSphereRenderer buildMeshes time: "+time+"ms"));
            }
        }catch (Exception e){
            source.getSource().sendSystemMessage(Component.literal("Failed parse dyson sphere data. Please check the error in log."));
            LOGGER.error("Failed parse dyson sphere data", e);
        }
        return 0;
    }
    
    public static int meshBuildBenchmark(CommandContext<CommandSourceStack> source){
        var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get());
        if(renderer instanceof DebugEntityBlockRenderer renderer_){
            for (int i = 0; i < 10; i++) {
                renderer_.sphereRenderer.buildMeshes();
            }
            var time = 0L;
            for (int i = 0; i < 10; i++) {
                time += renderer_.sphereRenderer.buildMeshes();
            }
            source.getSource().sendSystemMessage(Component.literal("DysonSphereRenderer buildMeshes benchmark avg time: "+time/10+"ms"));
        }
        return 0;
    }
}
