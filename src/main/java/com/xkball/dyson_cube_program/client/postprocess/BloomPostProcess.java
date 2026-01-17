package com.xkball.dyson_cube_program.client.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.logging.LogUtils;
import com.xkball.dyson_cube_program.DysonCubeProgram;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.SamplerCacheCache;
import com.xkball.dyson_cube_program.client.b3d.pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.b3d.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;

@NonNullByDefault
public class BloomPostProcess extends AbstractPostProcess {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    private final int samplerDepth;
    private final RenderTarget swap;
    private final RenderTarget composite;
    private final RenderTarget[] downSamplersH;
    private final RenderTarget[] downSamplersV;
    private RenderTarget background;
    private int used = 0;
    private boolean inPass = false;
    
    public BloomPostProcess(int xSize, int ySize) {
        this(xSize,ySize,4);
    }
    
    @Override
    public String getName() {
        return "bloom";
    }
    
    private BloomPostProcess(int xSize, int ySize, int samplerDepth) {
        super(xSize, ySize);
        assert samplerDepth > 0;
        this.samplerDepth = samplerDepth;
        this.composite = new TextureTarget("bloom_composite", xSize, ySize, false);
        this.swap = new TextureTarget("bloom_swap", xSize, ySize, true);
        this.downSamplersH = new RenderTarget[samplerDepth];
        this.downSamplersV = new RenderTarget[samplerDepth];
        for(var i = 0; i < samplerDepth; i++) {
            var factor = 2 << i;
            downSamplersH[i] = new TextureTarget("bloom_down_h_"+i, xSize/factor, ySize/factor, false);
            downSamplersV[i] = new TextureTarget("bloom_down_v_"+i, xSize/factor, ySize/factor, false);
        }
    }
    
    @Override
    public void resize(int xSize, int ySize) {
        super.resize(xSize, ySize);
        composite.resize(xSize,ySize);
        swap.resize(xSize,ySize);
        for(var i = 0; i < samplerDepth; i++) {
            var factor = 2 << i;
            downSamplersH[i].resize(xSize/factor,ySize/factor);
            downSamplersV[i].resize(xSize/factor,ySize/factor);
        }
    }
    
    @Override
    public void apply(RenderTarget input) {
        if(this.background == null){
            LOGGER.error("background render target not set.");
            if(DysonCubeProgram.IS_DEBUG) throw new NullPointerException("background render target not set.");
            return;
        }
        var src = input;
        
        for(var i = 0; i < samplerDepth; i++) {
            var h = downSamplersH[i];
            var v = downSamplersV[i];
            RenderTarget finalSrc = src;
            this.updateDownSamplerUniform(i,true);
            this.processOnce(DCPRenderPipelines.BLOOM_DOWN_SAMPLER,h, (pass) -> pass.bindTexture("DiffuseSampler", finalSrc.getColorTextureView(), SamplerCacheCache.NEAREST_CLAMP));
            this.updateDownSamplerUniform(i,false);
            this.processOnce(DCPRenderPipelines.BLOOM_DOWN_SAMPLER,v,(pass) -> pass.bindTexture("DiffuseSampler", h.getColorTextureView(), SamplerCacheCache.NEAREST_CLAMP));
            src = h;
        }
        
        this.processOnce(DCPRenderPipelines.BLOOM_COMPOSITE,composite,(pass) -> {
            pass.bindTexture("DiffuseSampler",background.getColorTextureView(), SamplerCacheCache.NEAREST_CLAMP);
            pass.bindTexture("HighLight",input.getColorTextureView(), SamplerCacheCache.NEAREST_CLAMP);
            for(var i = 0; i < samplerDepth; i++) {
                pass.bindTexture("BlurTexture" + (i+1),downSamplersV[i].getColorTextureView(), SamplerCacheCache.LINEAR_CLAMP);
            }
        });
        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
        ClientUtils.copyFrameBufferColorTo(composite,mainBuffer);
    }
    
    private void updateDownSamplerUniform(int i, boolean horizontal) {
        var factor = 2 << i;
        DCPUniforms.BLOOM_DOWN_SAMPLER_UNIFORM.updateUnsafe(b ->
                b.putFloat(factor)
                .putInt((i + 1) * 6 + 1)
                .putVec2(horizontal ? 1f : 0f, horizontal ? 0f : 1f)
                .putVec2((float) xSize/factor, (float) ySize/factor));
    }
    
//    public void bindAndClear(boolean copyDepth) {
//        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
//        ClientUtils.copyFrameBufferColorTo(mainBuffer,this.swap);
//        if(copyDepth) {
//            ClientUtils.copyFrameBufferDepthTo(mainBuffer,this.swap);
//        }
//        ClientUtils.clear(mainBuffer,false);
//    }
//
//    public void applyAndUnbind(boolean copyDepth) {
//        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
//        this.apply(mainBuffer);
//        if(copyDepth) {
//            ClientUtils.copyFrameBufferDepthTo(this.swap, mainBuffer);
//        }
//    }
    
    public RenderTarget startPass(RenderTarget scr, boolean copyDepth){
        if(this.inPass){
            throw new IllegalStateException("Already in a bloom pass.");
        }
        if(copyDepth){
            ClientUtils.copyFrameBufferDepthTo(scr,this.swap);
        }
        this.background = scr;
        this.inPass = true;
        return swap;
    }
    
    public void endPass(boolean copyDepth){
        this.used+=1;
        this.inPass = false;
        if(copyDepth){
            ClientUtils.copyFrameBufferDepthTo(this.swap, this.background);
        }
    }
    
    public void applyAndFlush(){
        if(this.used == 0) return;
        this.apply(swap);
        ClientUtils.clear(swap,true);
        this.background = null;
        this.used = 0;
    }
    
    @SubscribeEventEnhanced
    public static void onLevelRender(RenderLevelStageEvent.AfterLevel event){
        DCPPostProcesses.BLOOM.applyAndFlush();
    }
}
