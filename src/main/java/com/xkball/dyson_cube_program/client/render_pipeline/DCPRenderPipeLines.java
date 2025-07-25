package com.xkball.dyson_cube_program.client.render_pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.xkball.dyson_cube_program.utils.VanillaUtils;

public class DCPRenderPipeLines {
    
    public static final RenderPipeline SUN_0 = RenderPipeline.builder()
            .withLocation(VanillaUtils.modRL("sun_0"))
            .withVertexShader(VanillaUtils.modRL("core/sun_0"))
            .withFragmentShader(VanillaUtils.modRL("core/sun_0"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("SunUniform0", UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build();

}
