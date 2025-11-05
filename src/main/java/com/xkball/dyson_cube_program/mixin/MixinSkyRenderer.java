package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SkyRenderer.class)
public class MixinSkyRenderer {
    
//    @Inject(method = "buildStars",at = @At("HEAD"))
//    public void onBuildStar(CallbackInfoReturnable<GpuBuffer> cir){
//        ClientUtils.SkyHelper.loadStars();
//    }
    
    @Shadow private int starIndexCount;
    
    /**
     * @author
     * @reason
     */
    @Overwrite
    private GpuBuffer buildStars(){
        ClientUtils.SkyHelper.loadStars();
        var pair = ClientUtils.SkyHelper.buildStars();
        this.starIndexCount = pair.getSecond();
        return pair.getFirst();
    }

}
