package com.xkball.dyson_cube_program.mixin;

import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import com.xkball.dyson_cube_program.client.b3d.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class MixinLevelRenderer {
    
    @Inject(method = "resize",at = @At("HEAD"))
    public void onResize(int width, int height, CallbackInfo ci){
        DCPUniforms.SCREEN_SIZE.update();
        DCPPostProcesses.resize(width, height);
    }
    
    @Inject(method = "endFrame",at = @At("RETURN"))
    public void onEndFrame(CallbackInfo ci){
        for(var ef : ClientRenderObjects.INSTANCE.endFrame){
            ef.endFrame();
        }
    }
}
