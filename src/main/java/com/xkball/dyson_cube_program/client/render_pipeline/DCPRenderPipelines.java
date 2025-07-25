package com.xkball.dyson_cube_program.client.render_pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import net.minecraft.client.renderer.RenderPipelines;

public class DCPRenderPipelines {
    
    public static final RenderPipeline DEBUG_LINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(VanillaUtils.modRL("pipeline/debug_line"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
            .build();
    
    public static final ExtendedRenderPipeline SUN_0 = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("pipeline/sun_0"))
            .withVertexShader(VanillaUtils.modRL("core/sun_0"))
            .withFragmentShader(VanillaUtils.modRL("core/sun_0"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("SunUniform", UniformType.UNIFORM_BUFFER)
            .bindUniform("SunUniform", DCPUniforms.THE_SUN_UNIFORM)
            .withBlend(BlendFunction.TRANSLUCENT)
            .buildExtended();
    
    public static final ExtendedRenderPipeline SUN_1 = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("pipeline/sun_1"))
            .withVertexShader(VanillaUtils.modRL("core/sun_1"))
            .withFragmentShader(VanillaUtils.modRL("core/sun_1"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("SunUniform", UniformType.UNIFORM_BUFFER)
            .bindUniform("SunUniform", DCPUniforms.THE_SUN_UNIFORM)
            .withBlend(BlendFunction.TRANSLUCENT)
            .buildExtended();
    
    public static final ExtendedRenderPipeline SUN_2 = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("pipeline/sun_2"))
            .withVertexShader(VanillaUtils.modRL("core/sun_2"))
            .withFragmentShader(VanillaUtils.modRL("core/sun_2"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("SunUniform", UniformType.UNIFORM_BUFFER)
            .bindUniform("SunUniform", DCPUniforms.THE_SUN_UNIFORM)
            .withBlend(BlendFunction.TRANSLUCENT)
            .buildExtended();

}
