package com.xkball.dyson_cube_program.client.rendertype;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class DCPRenderTypes {
    
    public static final RenderType THE_SUN_0 = RenderType.create(
            "the_sun",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            RenderType.SMALL_BUFFER_SIZE,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(DCPRenderStateShards.THE_SUN_SHADER_0)
                    .setOutputState(DCPRenderStateShards.SETUP_SUN_SHADER_0)
                    .createCompositeState(false)
    );
    
    public static final RenderType THE_SUN_1 = RenderType.create(
            "the_sun",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            RenderType.SMALL_BUFFER_SIZE,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setShaderState(DCPRenderStateShards.THE_SUN_SHADER_1)
                    .setOutputState(DCPRenderStateShards.SETUP_SUN_SHADER_1)
                    .createCompositeState(false)
    );
    
    public static final RenderType THE_SUN_2 = RenderType.create(
            "the_sun",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            RenderType.SMALL_BUFFER_SIZE,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setShaderState(DCPRenderStateShards.THE_SUN_SHADER_2)
                    .setOutputState(DCPRenderStateShards.SETUP_SUN_SHADER_2)
                    .createCompositeState(false)
    );
    
}
