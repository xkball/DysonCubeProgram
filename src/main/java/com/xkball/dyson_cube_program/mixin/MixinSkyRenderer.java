package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xkball.dyson_cube_program.client.renderer.block_entity.DebugEntityBlockRenderer;
import com.xkball.dyson_cube_program.common.DCPTempReg;
import com.xkball.dyson_cube_program.utils.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.MoonPhase;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

//Todo: 临时写法, 发正式版前删掉overwrite
@Mixin(SkyRenderer.class)
public abstract class MixinSkyRenderer {
    
//    @Inject(method = "buildStars",at = @At("HEAD"))
//    public void onBuildStar(CallbackInfoReturnable<GpuBuffer> cir){
//        ClientUtils.SkyHelper.loadStars();
//    }
    
    @Shadow private int starIndexCount;
    
    @Shadow protected abstract void renderMoon(MoonPhase moonPhase, float rainBrightness, PoseStack poseStack);
    
    @Shadow protected abstract void renderStars(float starBrightness, PoseStack poseStack);
    
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
        var renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get());
        if(renderer != null){
            ((DebugEntityBlockRenderer)renderer).render(poseStack.last().pose().transformPosition(new Vector3f(0,100,0)),poseStack);
        }
        ClientUtils.getCommandEncoder().clearDepthTexture(Minecraft.getInstance().getMainRenderTarget().getDepthTextureView().texture(),1.0);
    }
    
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderSunMoonAndStars(
            PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness
    ) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(sunAngle));
        poseStack.translate(0,100,0);
        poseStack.mulPose(Axis.XN.rotation(sunAngle));
        poseStack.mulPose(Axis.YP.rotationDegrees(ClientUtils.clientTickWithPartialTick()/160));
        poseStack.scale(8,8,8);
        this.renderSun(rainBrightness, poseStack);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotation(moonAngle));
        this.renderMoon(moonPhase, rainBrightness, poseStack);
        poseStack.popPose();
        if (starBrightness > 0.0F) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotation(starAngle));
            this.renderStars(starBrightness, poseStack);
            poseStack.popPose();
        }
        
        poseStack.popPose();
    }
}
