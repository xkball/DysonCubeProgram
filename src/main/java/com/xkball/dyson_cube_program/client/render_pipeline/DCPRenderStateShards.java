package com.xkball.dyson_cube_program.client.render_pipeline;

import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.client.shader.DCPShaders;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import net.minecraft.client.renderer.RenderStateShard;

public class DCPRenderStateShards {
    
    public static final RenderStateShard.ShaderStateShard THE_SUN_SHADER_0 = new RenderStateShard.ShaderStateShard(DCPShaders::getTheSunShader0);
    public static final RenderStateShard.ShaderStateShard THE_SUN_SHADER_1 = new RenderStateShard.ShaderStateShard(DCPShaders::getTheSunShader1);
    public static final RenderStateShard.ShaderStateShard THE_SUN_SHADER_2 = new RenderStateShard.ShaderStateShard(DCPShaders::getTheSunShader2);
    public static final RenderStateShard.OutputStateShard SETUP_SUN_SHADER_0 = new RenderStateShard.OutputStateShard("set_client_time",
            () -> {
                DCPShaders.getTheSunShader0().safeGetUniform("ClientTime").set(ClientUtils.clientTickWithPartialTick());
                DCPShaders.getTheSunShader0().safeGetUniform("RenderDir").set(TheSunRenderer.getRenderDirection());
                DCPShaders.getTheSunShader0().safeGetUniform("Color").set(ColorUtils.color(TheSunRenderer.contextColor));
            },
            () -> {});
    
    public static final RenderStateShard.OutputStateShard SETUP_SUN_SHADER_1 = new RenderStateShard.OutputStateShard("set_client_time",
            () -> {
                DCPShaders.getTheSunShader1().safeGetUniform("ClientTime").set(ClientUtils.clientTickWithPartialTick());
                DCPShaders.getTheSunShader1().safeGetUniform("Color").set(ColorUtils.color(TheSunRenderer.contextColor));
            },
            () -> {});
    
    public static final RenderStateShard.OutputStateShard SETUP_SUN_SHADER_2 = new RenderStateShard.OutputStateShard("set_client_time",
            () -> {
                DCPShaders.getTheSunShader2().safeGetUniform("ClientTime").set(ClientUtils.clientTickWithPartialTick());
                DCPShaders.getTheSunShader2().safeGetUniform("Color").set(ColorUtils.color(TheSunRenderer.contextColor));
            },
            () -> {});
    
}
