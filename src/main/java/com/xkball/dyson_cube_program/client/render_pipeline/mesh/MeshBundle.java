package com.xkball.dyson_cube_program.client.render_pipeline.mesh;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.client.ICloseOnExit;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class MeshBundle<T> implements ICloseOnExit<MeshBundle<T>> {
    
    protected final String name;
    protected final T renderSettings;
    protected final List<Pair<Consumer<PoseStack>,CachedMesh>> meshes = new ArrayList<>();
    
    public MeshBundle(String name, T renderSettings) {
        this.name = name;
        this.renderSettings = renderSettings;
    }
    
    public MeshBundle(String name, T renderSettings, List<Pair<Consumer<PoseStack>,CachedMesh>> meshes) {
        this.name = name;
        this.renderSettings = renderSettings;
        this.meshes.addAll(meshes);
    }
    
    @SuppressWarnings("resource")
    public static MeshBundle<RenderPipeline> of(String name, RenderPipeline pipeline, Consumer<BufferBuilder> initFunc){
        return new CachedMesh(name, pipeline.getVertexFormatMode(), pipeline.getVertexFormat(), initFunc).toMeshBundle(pipeline);
    }
    
    public abstract void setupRenderPass(RenderPass renderPass);
    public abstract void endRenderPass(RenderPass renderPass);
    public abstract @Nullable GpuTextureView getColorTarget();
    public abstract @Nullable GpuTextureView getDepthTarget();
    public abstract VertexFormat.Mode getVertexFormatMode();
    public abstract VertexFormat getVertexFormat();
    
    public MeshBundle<T> append(Supplier<MeshData> mesh){
        this.meshes.add(Pair.of(p ->{},new CachedMesh(this.name + "_" + this.meshes.size(), this.getVertexFormatMode(), this.getVertexFormat(), mesh)));
        return this;
    }
    
    public MeshBundle<T> append(Consumer<BufferBuilder> mesh){
        this.meshes.add(Pair.of(p ->{},new CachedMesh(this.name + "_" + this.meshes.size(), this.getVertexFormatMode(), this.getVertexFormat(), mesh)));
        return this;
    }
    
    public MeshBundle<T> append(Supplier<MeshData> mesh, Consumer<PoseStack> poseStackSetup){
        this.meshes.add(Pair.of(poseStackSetup,new CachedMesh(this.name + "_" + this.meshes.size(), this.getVertexFormatMode(), this.getVertexFormat(), mesh)));
        return this;
    }
    
    public MeshBundle<T> append(Consumer<BufferBuilder> mesh, Consumer<PoseStack> poseStackSetup){
        this.meshes.add(Pair.of(poseStackSetup,new CachedMesh(this.name + "_" + this.meshes.size(), this.getVertexFormatMode(), this.getVertexFormat(), mesh)));
        return this;
    }
    
    public void render(PoseStack poseStack) {
        var colorTarget = this.getColorTarget();
        var depthTarget = this.getDepthTarget();
        if(colorTarget == null || depthTarget == null) return;
        
        try (var renderpass = ClientUtils.getCommandEncoder()
                .createRenderPass(() -> name + " mesh bundle rendering",colorTarget, OptionalInt.empty(), depthTarget, OptionalDouble.empty())){
            RenderSystem.bindDefaultUniforms(renderpass);
            this.setupRenderPass(renderpass);
            for(var entry : meshes) {
                var setup = entry.getFirst();
                var mesh = entry.getSecond();
                poseStack.pushPose();
                setup.accept(poseStack);
                var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
                var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f(), 0f);
                renderpass.setUniform("DynamicTransforms", transformUBO);
                renderpass.setVertexBuffer(0, mesh.getVertexBuffer());
                renderpass.setIndexBuffer(mesh.getIndexBuffer(),mesh.getIndexType());
                renderpass.drawIndexed(0,0, mesh.getIndexCount(), 1);
                poseStack.popPose();
            }
            this.endRenderPass(renderpass);
        }
    }
    
    @Override
    public void close() {
        for(var mesh : meshes){
            mesh.getSecond().close();
        }
    }
}
