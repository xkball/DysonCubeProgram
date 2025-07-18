package com.xkball.dyson_cube_program.mixin;

import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    @Inject(method = "reloadShaders", at = @At("RETURN"))
    public void afterReloadShaders(ResourceProvider resourceProvider, CallbackInfo ci){
        DCPPostProcesses.createPostProcess();
    }
}
