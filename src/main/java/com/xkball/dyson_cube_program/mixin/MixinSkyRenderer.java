package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.client.renderer.block_entity.DebugEntityBlockRenderer;
import com.xkball.dyson_cube_program.common.DCPTempReg;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import org.joml.Vector3f;
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
    
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void renderSun(float rainBrightness, PoseStack poseStack){
        poseStack.pushPose();
        poseStack.translate(0,100,0);
        poseStack.scale(8,8,8);
        var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get());
        if(renderer != null){
            ((DebugEntityBlockRenderer)renderer).render(poseStack.last().pose().transformPosition(new Vector3f(0,100,0)),poseStack);
        }
        poseStack.popPose();
        ClientUtils.getCommandEncoder().clearDepthTexture(Minecraft.getInstance().getMainRenderTarget().getDepthTextureView().texture(),1.0);
        
    }
}
