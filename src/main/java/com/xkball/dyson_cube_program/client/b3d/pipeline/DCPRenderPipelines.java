package com.xkball.dyson_cube_program.client.b3d.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.client.SamplerCacheCache;
import com.xkball.dyson_cube_program.client.b3d.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.client.b3d.vertex.DCPVertexFormats;
import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import com.xkball.dyson_cube_program.utils.client.ClientUtils;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class DCPRenderPipelines {
    
    public static final ExtendedRenderPipeline POSITION_COLOR_INSTANCED = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("position_color_instanced"))
            .withVertexShader(VanillaUtils.modRL("core/position_color_instanced"))
            .withFragmentShader("core/position_color")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSSBO("InstanceTransformColor")
            .buildExtended();
    
    public static final ExtendedRenderPipeline POSITION_COLOR_INSTANCED_NO_CULL = ExtendedRenderPipeline.extendedbuilder(POSITION_COLOR_INSTANCED)
            .withLocation(VanillaUtils.modRL("position_color_instanced_no_cull"))
            .withCull(false)
            .buildExtended();
    
    public static final ExtendedRenderPipeline POSITION_TEX_COLOR_INSTANCED = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("position_tex_color_instanced"))
            .withVertexShader(VanillaUtils.modRL("core/position_tex_color_instanced"))
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .bindSampler("Sampler0",() -> {
                var texture = Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                return Pair.of(texture.getTextureView(),texture.getSampler());
            })
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withSSBO("InstanceTransformColor")
            .buildExtended();
    
    
    public static final ExtendedRenderPipeline POSITION_DUAL_TEX_COLOR = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DCPVertexFormats.POSITION_DUAL_TEX_COLOR, VertexFormat.Mode.TRIANGLES)
            .withLocation(VanillaUtils.modRL("position_dual_tex_color"))
            .withVertexShader(VanillaUtils.modRL("core/position_dual_tex_color"))
            .withFragmentShader(VanillaUtils.modRL("core/position_dual_tex_color"))
            .withBlend(BlendFunction.TRANSLUCENT)
            .withSampler("TexFront")
            .bindSampler("TexFront",() -> {
                var texture = ClientUtils.getTexture(VanillaUtils.modRL("textures/dyson_shell/dyson-shell-e14.png"));
                return Pair.of(texture.getTextureView(), SamplerCacheCache.NEAREST_CLAMP_MIPMAP);
            })
            .withSampler("TexBack")
            .bindSampler("TexBack",() -> {
                var texture = ClientUtils.getTexture(VanillaUtils.modRL("textures/dyson_shell/dyson-shell-a.png"));
                return Pair.of(texture.getTextureView(), SamplerCacheCache.NEAREST_CLAMP_MIPMAP);
            })
            .withSampler("TexNoise")
            .bindSampler("TexNoise",() -> {
                var texture = ClientUtils.getTexture(VanillaUtils.modRL("textures/blue_noise.png"));
                return Pair.of(texture.getTextureView(), SamplerCacheCache.NEAREST_REPEAT);
            })
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("CustomColorModulator", UniformType.UNIFORM_BUFFER)
            .bindUniform("CustomColorModulator", DCPUniforms.CUSTOM_COLOR_MODULATOR)
            .bindMultiTarget(1,() -> DCPPostProcesses.BLOOM.getSwapRenderTarget().getColorTextureView())
            .withCull(false)
            .buildExtended();
    
    public static final ExtendedRenderPipeline DYSON_SAIL = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("dyson_sail"))
            .withVertexShader(VanillaUtils.modRL("core/dyson_sail"))
            .withFragmentShader(VanillaUtils.modRL("core/dyson_sail"))
            .withBlend(BlendFunction.TRANSLUCENT)
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("ScreenSize", UniformType.UNIFORM_BUFFER)
            .bindUniform("ScreenSize", DCPUniforms.SCREEN_SIZE)
            .withSSBO("InstanceTransformColor")
            .withCull(false)
            .buildExtended();
    
    public static final RenderPipeline DEBUG_LINE = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(VanillaUtils.modRL("pipeline/debug_line"))
            .withDepthWrite(true)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES)
            .build();
    
    public static final RenderPipeline DEBUG_QUADS = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(VanillaUtils.modRL("pipeline/debug_quads"))
            .withDepthWrite(true)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();
    
    public static final RenderPipeline DEBUG_TRIANGLES = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(VanillaUtils.modRL("pipeline/debug_triangles"))
            .withDepthWrite(true)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .build();
    
    public static final ExtendedRenderPipeline BLOOM_DOWN_SAMPLER = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("pipeline/bloom_down_sampler"))
            .withVertexShader(VanillaUtils.modRL("core/down_sampler_blur"))
            .withFragmentShader(VanillaUtils.modRL("core/down_sampler_blur"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withSampler("DiffuseSampler")
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("DownSampler", UniformType.UNIFORM_BUFFER)
            .bindUniform("DownSampler", DCPUniforms.BLOOM_DOWN_SAMPLER_UNIFORM)
            .buildExtended();
    
    public static final ExtendedRenderPipeline BLOOM_COMPOSITE = ExtendedRenderPipeline.extendedbuilder()
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withLocation(VanillaUtils.modRL("pipeline/bloom_composite"))
            .withVertexShader(VanillaUtils.modRL("core/blit"))
            .withFragmentShader(VanillaUtils.modRL("core/bloom_composite"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withSampler("DiffuseSampler")
            .withSampler("HighLight")
            .withSampler("BlurTexture1")
            .withSampler("BlurTexture2")
            .withSampler("BlurTexture3")
            .withSampler("BlurTexture4")
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Composite", UniformType.UNIFORM_BUFFER)
            .bindUniform("Composite", DCPUniforms.BLOOM_COMPOSITE_UNIFORM)
            .buildExtended();
    
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
