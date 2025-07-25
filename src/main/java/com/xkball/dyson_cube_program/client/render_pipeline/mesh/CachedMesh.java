package com.xkball.dyson_cube_program.client.render_pipeline.mesh;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.ICloseOnExit;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Supplier;

@NonNullByDefault
public class CachedMesh implements ICloseOnExit<CachedMesh> {
    
    public final String name;
    public final VertexFormat.Mode mode;
    public final VertexFormat format;
    public final Supplier<MeshData> initFunc;
    
    private volatile int indexCount = -1;
    @Nullable
    private VertexFormat.IndexType indexType;
    
    @Nullable
    private GpuBuffer vertexBuffer;
    
    @Nullable
    private GpuBuffer indexBuffer;
    
    public CachedMesh(String name, VertexFormat.Mode mode, VertexFormat format, Consumer<BufferBuilder> initFunc) {
        this.name = name;
        this.mode = mode;
        this.format = format;
        this.initFunc = () -> {
            var bufferBuilder = Tesselator.getInstance().begin(mode, format);
            initFunc.accept(bufferBuilder);
            return bufferBuilder.buildOrThrow();
        };
    }
    
    public CachedMesh(String name, VertexFormat.Mode mode, VertexFormat format, Supplier<MeshData> initFunc) {
        this.name = name;
        this.mode = mode;
        this.format = format;
        this.initFunc = initFunc;
    }
    
    private void checkInit(){
        if(indexCount == -1){
            synchronized (this){
                if(indexCount == -1){
                    init();
                }
            }
        }
    }
    
    private void init(){
        try(var mesh = initFunc.get()) {
            this.vertexBuffer = ClientUtils.getGpuDevice().createBuffer(() -> name + "mesh vertex buffer",GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,mesh.vertexBuffer());
            var state = mesh.drawState();
            this.indexCount = state.indexCount();
            if(mesh.indexBuffer() == null){
                var indices = RenderSystem.getSequentialBuffer(mode);
                this.indexBuffer = indices.getBuffer(indexCount);
                this.indexType = indices.type();
            }
            else {
                this.indexBuffer = ClientUtils.getGpuDevice().createBuffer(() -> name + "mesh index buffer", GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, Objects.requireNonNull(mesh.indexBuffer()));
                this.indexType = state.indexType();
            }
        }
    }
    
    public void render(RenderPipeline pipeline, PoseStack poseStack) {
        var colorTarget = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        var depthTarget = Minecraft.getInstance().getMainRenderTarget().getDepthTextureView();
        if(colorTarget == null || depthTarget == null) return;
        
        var modelView = RenderSystem.getModelViewStack().mul(poseStack.last().pose(), new Matrix4f());
        var transformUBO = RenderSystem.getDynamicUniforms().writeTransform(modelView, new Vector4f(1,1,1,1), new Vector3f(), new Matrix4f(), 0f);
        try (var renderpass = ClientUtils.getCommandEncoder()
                .createRenderPass(() -> name + " mesh rendering",colorTarget, OptionalInt.empty(), depthTarget, OptionalDouble.empty())){
            RenderSystem.bindDefaultUniforms(renderpass);
            renderpass.setPipeline(pipeline);
            renderpass.setUniform("DynamicTransforms", transformUBO);
            renderpass.setVertexBuffer(0, this.getVertexBuffer());
            renderpass.setIndexBuffer(this.getIndexBuffer(),this.getIndexType());
            renderpass.drawIndexed(0,0, indexCount, 1);
        }
    }
    
    public MeshBundle<RenderPipeline> toMeshBundle(RenderPipeline pipeline){
        return new MeshBundleWithRenderPipeline(name, pipeline, List.of(Pair.of(p -> {},this)));
    }
    
    public MeshBundle<RenderType> toMeshBundle(RenderType renderType){
        return new MeshBundleWithRenderType(name, renderType, List.of(Pair.of(p -> {},this)));
    }
    
    public GpuBuffer getVertexBuffer(){
        checkInit();
        //noinspection DataFlowIssue
        return vertexBuffer;
    }
    
    public GpuBuffer getIndexBuffer(){
        checkInit();
        //noinspection DataFlowIssue
        return indexBuffer;
    }
    
    public VertexFormat.IndexType getIndexType(){
        checkInit();
        //noinspection DataFlowIssue
        return indexType;
    }
    
    public int getIndexCount(){
        checkInit();
        return indexCount;
    }
    
    @Override
    public void close() {
        if(vertexBuffer != null){
            vertexBuffer.close();
        }
        if(indexBuffer != null){
            indexBuffer.close();
        }
    }
}
