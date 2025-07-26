package com.xkball.dyson_cube_program.client.postprocess;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.CachedMesh;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.renderer.CachedOrthoProjectionMatrixBuffer;

import java.util.OptionalDouble;
import java.util.OptionalInt;

//https://github.com/ZhuRuoLing/AppliedWebTerminal/blob/main/src/main/kotlin/icu/takeneko/appwebterminal/client/rendering/foundation/PostProcess.kt
@NonNullByDefault
public abstract class AbstractPostProcess {
    
    protected int xSize;
    protected int ySize;

    protected final CachedOrthoProjectionMatrixBuffer projMatCache = new CachedOrthoProjectionMatrixBuffer("post process",0.1f,1000f,false);
    protected CachedMesh cachedMesh;

    public AbstractPostProcess(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.cachedMesh = createScreenQuad(xSize, ySize);
    }
    
    public abstract String getName();
    
    public abstract void apply(RenderTarget inputTexture);
    
    public CachedMesh createScreenQuad(int xSize, int ySize){
        return new CachedMesh("screen_blit", VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION, b -> {
            b.addVertex(0.0f, 0.0f, 500.0f);
            b.addVertex(xSize, 0.0f, 500.0f);
            b.addVertex(xSize,  ySize, 500.0f);
            b.addVertex(0.0f, ySize, 500.0f);
        });
    }
    
    public void resize(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.cachedMesh.close();
        this.cachedMesh = createScreenQuad(xSize, ySize);
    }
    
    protected void processOnce(
            RenderPipeline renderPipeline,
            RenderTarget output,
            SamplerSetter samplerSetter
    ) {
        var colorOutput = output.getColorTextureView();
        var depthOutput = output.getDepthTextureView();
        if(colorOutput == null) return;
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projMatCache.getBuffer(xSize,ySize), ProjectionType.ORTHOGRAPHIC);
        try(var renderpass = ClientUtils.getCommandEncoder()
                .createRenderPass(() -> getName() + " processing",
                        colorOutput, OptionalInt.of(0),
                        depthOutput, OptionalDouble.empty())){
            RenderSystem.bindDefaultUniforms(renderpass);
            samplerSetter.setSampler(renderpass);
            renderpass.setPipeline(renderPipeline);
            renderpass.setVertexBuffer(0, cachedMesh.getVertexBuffer());
            renderpass.setIndexBuffer(cachedMesh.getIndexBuffer(),cachedMesh.getIndexType());
            renderpass.drawIndexed(0,0, cachedMesh.getIndexCount(), 1);
        }
        RenderSystem.restoreProjectionMatrix();
    }
    
    public int getXSize() {
        return xSize;
    }
    
    public int getYSize() {
        return ySize;
    }
}