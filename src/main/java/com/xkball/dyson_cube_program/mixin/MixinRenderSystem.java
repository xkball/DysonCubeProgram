package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.RenderSystem;
import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import org.lwjgl.opengl.GL;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Inject(method = "initRenderer",at = @At("RETURN"))
    private static void afterInitRender(long p_409720_, int p_69581_, boolean p_69582_, ShaderSource p_460676_, boolean p_410401_, CallbackInfo ci) {
        ClientRenderObjects.init(GL.getCapabilities());
    }
}
