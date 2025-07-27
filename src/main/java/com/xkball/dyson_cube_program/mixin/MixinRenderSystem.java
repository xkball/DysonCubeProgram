package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiFunction;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "initRenderer",at = @At("RETURN"))
    private static void afterInitRender(long window, int glDebugVerbosity, boolean synchronous, BiFunction<ResourceLocation, ShaderType, String> defaultShaderSource, boolean renderDebugLabels, CallbackInfo ci) {
        ClientRenderObjects.INSTANCE = new ClientRenderObjects();
    }
}
