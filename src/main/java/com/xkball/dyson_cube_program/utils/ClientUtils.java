package com.xkball.dyson_cube_program.utils;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

public class ClientUtils {
    
    @OnlyIn(Dist.CLIENT)
    public static void renderAxis(MultiBufferSource bufferSource, PoseStack poseStack) {
        var buffer = bufferSource.getBuffer(RenderType.debugLineStrip(8));
        var matrix = poseStack.last();
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, -1, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(matrix, 100, 0, 0).setNormal(matrix, 1, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, 0, -1, 0).setColor(0xFF00FF00);
        buffer.addVertex(matrix, 0, 100, 0).setNormal(matrix, 0, 1, 0).setColor(0xFF00FF00);
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, 0, 0, -1).setColor(0xFF0000FF);
        buffer.addVertex(matrix, 0, 0, 100).setNormal(matrix, 0, 0, 1).setColor(0xFF0000FF);
    }
    
    public static void copyFrameBufferColorTo(RenderTarget from, RenderTarget to) {
        copyFrameBufferColorTo(from, to.frameBufferId);
    }
    
    public static void copyFrameBufferColorTo(RenderTarget from, int to) {
        var currentRead = GL30.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        var currentDraw = GL30.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, to);
        GL30.glBlitFramebuffer(0, 0, from.width, from.height,
                0, 0, from.width, from.height,
                GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, currentRead);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, currentDraw);
    }
    
    public static void copyFrameBufferDepthTo(RenderTarget from, RenderTarget to) {
        copyFrameBufferDepthTo(from, to.frameBufferId);
    }
    
    public static void copyFrameBufferDepthTo(RenderTarget from, int to) {
        var currentRead = GL30.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        var currentDraw = GL30.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, from.frameBufferId);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, to);
        GL30.glBlitFramebuffer(0, 0, from.width, from.height,
                0, 0, from.width, from.height,
                GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, currentRead);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, currentDraw);
    }
    
    public static VertexBuffer formMesh(MeshData meshData){
        var buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(meshData);
        VertexBuffer.unbind();
        return buffer;
    }
    
    public static void drawWithRenderType(RenderType renderType, VertexBuffer buffer){
        renderType.setupRenderState();
        var shader = RenderSystem.getShader();
        if(shader == null) return;
        var currentBuffer = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        buffer.bind();
        buffer.drawWithShader(RenderSystem.getModelViewMatrix(), RenderSystem.getProjectionMatrix(), shader);
        GlStateManager._glBindVertexArray(currentBuffer);
        renderType.clearRenderState();
    }
    
    public static void drawWithRenderType(RenderType renderType, VertexBuffer buffer, PoseStack poseStack){
        renderType.setupRenderState();
        var shader = RenderSystem.getShader();
        if(shader == null) return;
        var currentBuffer = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        buffer.bind();
        buffer.drawWithShader(RenderSystem.getModelViewMatrix().mul(poseStack.last().pose(),new Matrix4f()), RenderSystem.getProjectionMatrix(), shader);
        GlStateManager._glBindVertexArray(currentBuffer);
        renderType.clearRenderState();
    }
}
