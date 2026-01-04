package com.xkball.dyson_cube_program.utils;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.xkball.dyson_cube_program.client.ClientEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationGpuDevice;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.opengl.NVShaderBufferLoad;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Scanner;

public class ClientUtils {
    
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static GpuDevice getGpuDevice(){
        return RenderSystem.getDevice();
    }
    
    public static GlDevice getGLDevice(){
        var device = getGpuDevice();
        if(device instanceof ValidationGpuDevice vgd) {
            //noinspection UnstableApiUsage
            return (GlDevice) vgd.getRealDevice();
        }
        return (GlDevice) device;
    }
    
    public static CommandEncoder getCommandEncoder(){
        return RenderSystem.getDevice().createCommandEncoder();
    }
    
    public static RenderPass createRenderPass(String name){
        var colorTarget = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        var depthTarget = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        //noinspection DataFlowIssue
        return getCommandEncoder().createRenderPass(() -> name, colorTarget, OptionalInt.empty(), depthTarget, OptionalDouble.empty());
    }
    
    public static void clear(RenderTarget target){
        clear(target, true);
    }
    
    public static void clear(RenderTarget target, boolean clearDepth){
        if(target.useDepth && clearDepth){
            getCommandEncoder().clearColorAndDepthTextures(Objects.requireNonNull(target.getColorTexture()),0,Objects.requireNonNull(target.getDepthTexture()),1d);
        }
        else {
            getCommandEncoder().clearColorTexture(Objects.requireNonNull(target.getColorTexture()),0);
        }
    }
    
    public static PoseStack fromPose(PoseStack.Pose pose){
        var result = new PoseStack();
        result.last().set(pose);
        return result;
    }
    
    public static void renderAxis(MultiBufferSource bufferSource, PoseStack poseStack) {
        var buffer = bufferSource.getBuffer(RenderTypes.lines());
        var matrix = poseStack.last();
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, -1, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(matrix, 100, 0, 0).setNormal(matrix, 1, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, 0, -1, 0).setColor(0xFF00FF00);
        buffer.addVertex(matrix, 0, 100, 0).setNormal(matrix, 0, 1, 0).setColor(0xFF00FF00);
        buffer.addVertex(matrix, 0, 0, 0).setNormal(matrix, 0, 0, -1).setColor(0xFF0000FF);
        buffer.addVertex(matrix, 0, 0, 100).setNormal(matrix, 0, 0, 1).setColor(0xFF0000FF);
    }
    
    public static void copyFrameBufferColorTo(RenderTarget from, RenderTarget to) {
            getCommandEncoder().copyTextureToTexture(Objects.requireNonNull(from.getColorTexture()), Objects.requireNonNull(to.getColorTexture()),0, 0, 0, 0, 0, from.width, from.height);
    }
    
    public static void copyFrameBufferDepthTo(RenderTarget from, RenderTarget to) {
        to.copyDepthFrom(from);
    }
    
    public static BufferBuilder beginWithRenderPipeline(RenderPipeline pipeline){
        return Tesselator.getInstance().begin(pipeline.getVertexFormatMode(),pipeline.getVertexFormat());
    }
    
    public static float clientTickWithPartialTick(){
        return ClientEvent.tickCount + Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }
    
    public static void putModelToBuffer(PoseStack poseStack, BufferBuilder builder, Collection<BakedQuad> quads, int color){
        var color_ = ColorUtils.Vectorization.argbColor(color);
        for(var quad : quads){
            builder.putBulkData(poseStack.last(),quad,color_.x,color_.y,color_.z,color_.w, LightTexture.pack(15,15), OverlayTexture.NO_OVERLAY);
        }
    }
    
    public static long getNamedBufferAddrNV(int buffer){
        var result = NVShaderBufferLoad.glGetNamedBufferParameterui64NV(buffer, NVShaderBufferLoad.GL_BUFFER_GPU_ADDRESS_NV);
        NVShaderBufferLoad.glMakeNamedBufferResidentNV(buffer, GlConst.GL_READ_ONLY);
        return result;
    }
    
    public static TextureAtlasSprite getTextureFromAtlas(Identifier atlas, String id){
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(atlas).getSprite(VanillaUtils.modRL(id));
    }
    
    public static TextureAtlasSprite getTextureFromAtlas(String id){
        return getTextureFromAtlas(AtlasIds.BLOCKS, id);
    }
    
    public static @Nullable NativeImage readImage(Identifier rl){
        NativeImage result;
        var resource = Minecraft.getInstance().getResourceManager().getResource(rl);
        if(resource.isEmpty()) return null;
        try(var stream = resource.get().open()) {
            result = NativeImage.read(stream);
        } catch (IOException e) {
            LOGGER.error("Failed to read image {}", rl, e);
            return null;
        }
        return result;
    }
    
    public static class SkyHelper{
        public static final List<StarData> stars = new ArrayList<>();
    
        public static void loadStars(){
            stars.clear();
            var temp = new ArrayList<StarData>();
            try(var input = ClientUtils.class.getClassLoader().getResourceAsStream("META-INF/output.csv");
                var scanner = new Scanner(input)) {
                while(scanner.hasNextLine()){
                    temp.add(StarData.parse(scanner.nextLine()));
                }
            }catch (Exception e){
                LOGGER.error("Failed to load stars.", e);
            }
            stars.addAll(temp.stream().limit(4000).toList());
        }
        
        public static Pair<GpuBuffer,Integer> buildStars(){
            RandomSource randomsource = RandomSource.create(10842L);
            GpuBuffer gpubuffer;
            int indexCount;
            try (ByteBufferBuilder bytebufferbuilder = ByteBufferBuilder.exactlySized(DefaultVertexFormat.POSITION.getVertexSize() * stars.size() * 4)) {
                BufferBuilder bufferbuilder = new BufferBuilder(bytebufferbuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                
                for(var star: stars){
                    star.putQuad(bufferbuilder,randomsource);
                }
                
                try (MeshData meshdata = bufferbuilder.buildOrThrow()) {
                    indexCount = meshdata.drawState().indexCount();
                    gpubuffer = RenderSystem.getDevice().createBuffer(() -> "DCP Stars vertex buffer", 40, meshdata.vertexBuffer());
                }
            }
            
            return Pair.of(gpubuffer,indexCount);
        }
    }
    
    public record StarData(String id, float ra, float dec, float k, float mag){
        
        public static StarData parse(String str){
            var lt = str.split(",");
            return new StarData(lt[0],Float.parseFloat(lt[1]),Float.parseFloat(lt[2]),Float.parseFloat(lt[3]),Float.parseFloat(lt[4]));
        }
        
        public Vector3f pos(){
            var r_ra = Math.toRadians(ra);
            var r_dec = Math.toRadians(dec);
            float cosDec = (float) Math.cos(r_dec);
            float x = cosDec * (float) Math.cos(r_ra);
            float z = cosDec * (float) Math.sin(r_ra);
            float y = (float) -Math.sin(r_dec);
            return new Vector3f(x, y, z).normalize();
        }
        
        public void putQuad(BufferBuilder builder, RandomSource random){
            var size = (float) ((8.2f-mag) * 0.025f + Math.log(8.2f-mag+1) * 0.01);
            var pos = this.pos().normalize(100f);
            var z_rotate = (float)(random.nextDouble() * Math.PI * 2);
            var mat3 = new Matrix3f().rotateTowards(pos.negate(new Vector3f()),new Vector3f(0,1,0)).rotateZ(z_rotate);
            builder.addVertex(new Vector3f(size, -size, 0.0F).mul(mat3).add(pos));
            builder.addVertex(new Vector3f(size, size, 0.0F).mul(mat3).add(pos));
            builder.addVertex(new Vector3f(-size, size, 0.0F).mul(mat3).add(pos));
            builder.addVertex(new Vector3f(-size, -size, 0.0F).mul(mat3).add(pos));
        }
    }
}
