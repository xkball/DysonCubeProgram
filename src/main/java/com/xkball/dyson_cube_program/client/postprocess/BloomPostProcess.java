package com.xkball.dyson_cube_program.client.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.DCPUniforms;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.Minecraft;

import java.util.Objects;

@NonNullByDefault
public class BloomPostProcess extends AbstractPostProcess {
    
    private final int samplerDepth;
    private final RenderTarget swap;
    private final RenderTarget composite;
    private final RenderTarget[] downSamplersH;
    private final RenderTarget[] downSamplersV;
    
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
    public void apply(GpuTextureView input) {
        ClientUtils.clear(composite);
        for(var i = 0; i < samplerDepth; i++) {
            ClientUtils.clear(downSamplersH[i]);
            ClientUtils.clear(downSamplersV[i]);
        }
        
        var src = input;
        
        for(var i = 0; i < samplerDepth; i++) {
            var h = downSamplersH[i];
            var v = downSamplersV[i];
            GpuTextureView finalSrc = src;
            this.updateDownSamplerUniform(i,true);
            this.processOnce(DCPRenderPipelines.BLOOM_DOWN_SAMPLER,h, (pass) -> pass.bindSampler("DiffuseSampler", finalSrc));
            this.updateDownSamplerUniform(i,false);
            this.processOnce(DCPRenderPipelines.BLOOM_DOWN_SAMPLER,v,(pass) -> pass.bindSampler("DiffuseSampler", h.getColorTextureView()));
            src = h.getColorTextureView();
        }
        
        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
        this.processOnce(DCPRenderPipelines.BLOOM_COMPOSITE,composite,(pass) -> {
            pass.bindSampler("DiffuseSampler",swap.getColorTextureView());
            pass.bindSampler("HighLight",input);
            for(var i = 0; i < samplerDepth; i++) {
                pass.bindSampler("BlurTexture" + (i+1),downSamplersV[i].getColorTextureView());
            }
        });
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
    
    public void bindAndClear(boolean copyDepth) {
        ClientUtils.clear(this.swap);
        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
        ClientUtils.copyFrameBufferColorTo(mainBuffer,this.swap);
        if(copyDepth) {
            ClientUtils.copyFrameBufferDepthTo(mainBuffer,this.swap);
        }
        ClientUtils.clear(mainBuffer);
    }
    
    public void applyAndUnbind(boolean copyDepth) {
        var mainBuffer = Minecraft.getInstance().getMainRenderTarget();
        this.apply(Objects.requireNonNull(mainBuffer.getColorTextureView()));
        if(copyDepth) {
            ClientUtils.copyFrameBufferDepthTo(this.swap, Minecraft.getInstance().getMainRenderTarget());
        }
    }
    
}
