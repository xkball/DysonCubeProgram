package com.xkball.dyson_cube_program.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.xkball.dyson_cube_program.DysonCubeProgram;
import com.xkball.dyson_cube_program.utils.ThrowableSupplier;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = DysonCubeProgram.MODID,bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class DCPShaders {
    
    public static ShaderInstance BLOOM_COMPOSITE_SHADER;
    public static ShaderInstance DOWN_SAMPLER_BLUR_SHADER;
    public static ShaderInstance THE_SUN_SHADER_0;
    public static ShaderInstance THE_SUN_SHADER_1;
    public static ShaderInstance THE_SUN_SHADER_2;
    
    public static ShaderInstance getDownSamplerBlurShader() {
        return DOWN_SAMPLER_BLUR_SHADER;
    }
    
    public static ShaderInstance getBloomCompositeShader() {
        return BLOOM_COMPOSITE_SHADER;
    }
    
    public static ShaderInstance getTheSunShader0() {
        return THE_SUN_SHADER_0;
    }
    
    public static ShaderInstance getTheSunShader1() {
        return THE_SUN_SHADER_1;
    }
    
    public static ShaderInstance getTheSunShader2() {
        return THE_SUN_SHADER_2;
    }
    
    @SubscribeEvent
    public static void onRegShader(RegisterShadersEvent event) {
        var res = event.getResourceProvider();
        
        var bloomCompositeShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("bloom_composite"),DefaultVertexFormat.POSITION));
        event.registerShader(bloomCompositeShader,s -> BLOOM_COMPOSITE_SHADER = s);
        
        var downSamplerBlurShader = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("down_sampler_blur"),DefaultVertexFormat.POSITION));
        event.registerShader(downSamplerBlurShader, s -> DOWN_SAMPLER_BLUR_SHADER = s);
        
        var theSunShader0 = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("sun_0"),DefaultVertexFormat.POSITION));
        event.registerShader(theSunShader0, s -> THE_SUN_SHADER_0 = s);
        
        var theSunShader1 = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("sun_1"),DefaultVertexFormat.POSITION));
        event.registerShader(theSunShader1, s -> THE_SUN_SHADER_1 = s);
        
        var theSunShader2 = ThrowableSupplier.getOrThrow(() -> new ShaderInstance(res,VanillaUtils.modRL("sun_2"),DefaultVertexFormat.POSITION));
        event.registerShader(theSunShader2, s -> THE_SUN_SHADER_2 = s);
    }
    
}
