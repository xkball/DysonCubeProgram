package com.xkball.dyson_cube_program.utils;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.logging.LogUtils;
import com.xkball.dyson_cube_program.client.ClientEvent;
import com.xkball.dyson_cube_program.utils.math.Quad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ClientUtils {
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
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
    
    public static BufferBuilder beginWithRenderType(RenderType renderType){
        return Tesselator.getInstance().begin(renderType.mode,renderType.format);
    }
    
    public static VertexBuffer fromMesh(MeshData meshData){
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
    
    public static float clientTickWithPartialTick(){
        return ClientEvent.tickCount + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
    }
    
    public static List<Quad> earClipping(List<Vector3f> points){
        if(points.size() == 3){
            return List.of(new Quad(points.get(0), points.get(1), points.get(2), points.get(2)));
        }
        if(points.size() == 4){
            return List.of(new Quad(points.get(0), points.get(1), points.get(2), points.get(3)));
        }
        var result = new ArrayList<Quad>();
        var plist = new LinkedList<>(points);
        if(isCounterclockwisePoints(points)) {
            plist = plist.reversed();
        }
        var count = 0;
        while (plist.size() > 3){
            count++;
            var iter = plist.iterator();
            var a= iter.next();
            var b = iter.next();
            var c = plist.getLast();
            if(isEar(a,c,b,plist)){
                result.add(new Quad(a,b,c,c));
                plist.removeFirst();
            }
            else {
                plist.addLast(plist.removeFirst());
            }
            if(count > points.size() * 100){
                LOGGER.error("Failed to triangulated polygons.");
                LOGGER.error("points: {} ", points);
                break;
            }
        }
        result.add(new Quad(plist.getFirst(),plist.get(1),plist.getLast(),plist.getLast()));
        return result;
    }
    
    public static boolean isCounterclockwisePoints(List<Vector3f> points){
        var n = new Vector3f();
        for(var i = 0; i < points.size() - 1; i++){
            var a = points.get(i);
            var b = points.get(i + 1);
            n.add(a.cross(b,new Vector3f()));
        }
        return n.dot(points.getFirst()) < 0;
    }
    
    public static boolean isEar(Vector3f c, Vector3f l, Vector3f r, Collection<Vector3f> points){
        var nab = l.cross(c,new Vector3f()).normalize();
        var nbc = c.cross(r,new Vector3f()).normalize();
        var nac = r.cross(l,new Vector3f()).normalize();
        
        var d = l.dot(c.cross(r,new Vector3f()));
        if(d < 0) return false;
        
        for(var p : points){
            //此处用 == 是有意义的 因此也要求输入三个点必须要在points中
            if(p == c || p == l || p == r) continue;
            var dab = Math.signum(p.dot(nab));
            var dbc = Math.signum(p.dot(nbc));
            var dac = Math.signum(p.dot(nac));
            if(dab > 0 && dbc > 0 && dac > 0){
                return false;
            }
        }
        return true;
    }
}
