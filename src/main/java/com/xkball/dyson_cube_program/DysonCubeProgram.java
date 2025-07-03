package com.xkball.dyson_cube_program;

import com.xkball.dyson_cube_program.client.renderer.block_entity.DebugEntityBlockRenderer;
import com.xkball.dyson_cube_program.common.DCPTempReg;
import com.xkball.xorlib.api.annotation.ModMeta;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@Mod(DysonCubeProgram.MODID)
@ModMeta(useLanguages = {"en_us","zh_cn"})
public class DysonCubeProgram {

    public static final String MODID = "dyson_cube_program";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DysonCubeProgram(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        DCPTempReg.init(modEventBus);
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        
        }
        
        @SubscribeEvent
        public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get(), DebugEntityBlockRenderer::new);
        }
    }
}
