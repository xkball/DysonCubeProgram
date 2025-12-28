package com.xkball.dyson_cube_program;

import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = DysonCubeProgram.MODID)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    public static boolean USE_NV_COMMAND_LIST = true;
    
    static final ModConfigSpec.ConfigValue<Boolean> USE_NV_COMMAND_LIST_CONFIG = BUILDER.comment("Enable using Command List for rendering.(NV only)").define("use_nv_command_list", true);
    static final ModConfigSpec SPEC = BUILDER.build();
    
    public static void update() {
        USE_NV_COMMAND_LIST = USE_NV_COMMAND_LIST_CONFIG.get();
    }
    
    public static boolean useNvCommandList() {
        return USE_NV_COMMAND_LIST && ClientRenderObjects.SUPPORT_NV_COMMAND_LIST && ClientRenderObjects.SUPPORT_NV_SHADER_BUFFER_LOAD;
    }
    
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        update();
    }
    
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        update();
    }
}
