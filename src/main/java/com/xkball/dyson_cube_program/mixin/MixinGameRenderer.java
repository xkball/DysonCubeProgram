package com.xkball.dyson_cube_program.mixin;

import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void beforeRenderLevel(DeltaTracker deltaTracker, CallbackInfo ci){
        for(var up : ClientRenderObjects.INSTANCE.everyFrame){
            up.update();
        }
    }
}
