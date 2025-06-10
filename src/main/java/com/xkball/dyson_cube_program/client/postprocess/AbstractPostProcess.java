package com.xkball.dyson_cube_program.client.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

//https://github.com/ZhuRuoLing/AppliedWebTerminal/blob/main/src/main/kotlin/icu/takeneko/appwebterminal/client/rendering/foundation/PostProcess.kt
public abstract class AbstractPostProcess {
    
    protected int xSize;
    protected int ySize;

    protected final Matrix4f projectionMatrix;
    protected final Matrix4f mvMat = new Matrix4f();

    public AbstractPostProcess(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.projectionMatrix = new Matrix4f().setOrtho(
                0f,
                (float) xSize,
                0f,
                (float) ySize,
                0.1f,
                1000f
        );
    }
    
    public void apply(RenderTarget target) {
        this.apply(target.getColorTextureId());
    }

    public abstract void apply(int inputTexture);

    public void resize(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.projectionMatrix.setOrtho(
                0f,
                (float) xSize,
                0f,
                (float) ySize,
                0.1f,
                1000f
        );
    }
    
    protected void processOnce(ShaderInstance shader,
                               RenderTarget inputTexture,
                               RenderTarget writeFramebuffer,
                               UniformSetter uniformSetter){
        this.processOnce(shader,inputTexture.getColorTextureId(),writeFramebuffer,uniformSetter);
    }

    protected void processOnce(
            ShaderInstance shader,
            int inputTexture,
            RenderTarget writeFramebuffer,
            UniformSetter uniformSetter
    ) {
        RenderSystem.setShader(() -> shader);
        shader.setSampler("DiffuseSampler", inputTexture);
        shader.safeGetUniform("ProjMat").set(this.projectionMatrix);
        uniformSetter.setUniforms(shader);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.disableDepthTest();

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferBuilder.addVertex(0.0f, 0.0f, 500.0f);
        bufferBuilder.addVertex(xSize, 0.0f, 500.0f);
        bufferBuilder.addVertex(xSize,  ySize, 500.0f);
        bufferBuilder.addVertex(0.0f, ySize, 500.0f);

        shader.apply();
        writeFramebuffer.bindWrite(true);
        BufferUploader.draw(bufferBuilder.buildOrThrow());
        writeFramebuffer.unbindWrite();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(() -> null);
        shader.clear();
    }
    
}