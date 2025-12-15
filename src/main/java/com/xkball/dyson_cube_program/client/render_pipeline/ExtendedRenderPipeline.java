package com.xkball.dyson_cube_program.client.render_pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.LogicOp;
import com.mojang.blaze3d.platform.PolygonMode;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.UpdatableUBO;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationGpuTextureView;
import net.neoforged.neoforge.client.stencil.StencilTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@NonNullByDefault
public class ExtendedRenderPipeline extends RenderPipeline {
    
    public final Map<String, UpdatableUBO> UBOBindings;
    public final Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings;
    public final List<String> SSBOs;
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ExtendedRenderPipeline(Identifier location, Identifier vertexShader,
                                  Identifier fragmentShader, ShaderDefines shaderDefines,
                                  List<String> samplers, List<UniformDescription> uniforms,
                                  Optional<BlendFunction> blendFunction, DepthTestFunction depthTestFunction,
                                  PolygonMode polygonMode, boolean cull,
                                  boolean writeColor, boolean writeAlpha, boolean writeDepth,
                                  LogicOp colorLogic, VertexFormat vertexFormat,
                                  VertexFormat.Mode vertexFormatMode,
                                  float depthBiasScaleFactor, float depthBiasConstant,
                                  int sortKey, Optional<StencilTest> stencilTest,
                                  Map<String, UpdatableUBO> UBOBindings, Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings,
                                  List<String> SSBOs) {
        super(location, vertexShader, fragmentShader, shaderDefines, samplers, uniforms, blendFunction, depthTestFunction, polygonMode, cull, writeColor, writeAlpha, writeDepth, colorLogic, vertexFormat, vertexFormatMode, depthBiasScaleFactor, depthBiasConstant, sortKey, stencilTest);
        this.UBOBindings = UBOBindings;
        this.samplerBindings = samplerBindings;
        this.SSBOs = SSBOs;
    }
    
    public void apply(RenderPass renderPass) {
        for(var entry : UBOBindings.entrySet()) {
            renderPass.setUniform(entry.getKey(),entry.getValue().getBuffer());
        }
        for(var entry : samplerBindings.entrySet()) {
            var texture = entry.getValue().get().getFirst();
            if(texture instanceof ValidationGpuTextureView vgtv) texture = vgtv.getRealTextureView();
            renderPass.bindTexture(entry.getKey(), texture, entry.getValue().get().getSecond());
        }
    }
    
    public static Builder extendedbuilder(RenderPipeline.Snippet... snippets) {
        var  builder = new Builder();
        
        for (RenderPipeline.Snippet renderpipeline$snippet : snippets) {
            builder.withSnippet(renderpipeline$snippet);
        }
        
        return builder;
    }
    
    public static class Builder extends RenderPipeline.Builder {
        
        private final Map<String, UpdatableUBO> UBOBindings = new HashMap<>();
        public final Map<String, Supplier<Pair<GpuTextureView, GpuSampler>>> samplerBindings = new HashMap<>();
        private final List<String> SSBOs = new ArrayList<>();
        
        public Builder(){
            super();
        }
        
        public Builder bindSampler(String sampler, Supplier<Pair<GpuTextureView, GpuSampler>> texture){
            this.samplerBindings.put(sampler, texture);
            return this;
        }
        
        public Builder bindUniform(String uniform, UpdatableUBO ubo){
            this.UBOBindings.put(uniform, ubo);
            return this;
        }
        
        public Builder withSSBO(String name){
            this.SSBOs.add(name);
            return this;
        }
        
        @Override
        public Builder withLocation(String location) {
            this.location = Optional.of(Identifier.withDefaultNamespace(location));
            return this;
        }
        
        @Override
        public Builder withLocation(Identifier location) {
            this.location = Optional.of(location);
            return this;
        }
        
        @Override
        public Builder withFragmentShader(String fragmentShader) {
            this.fragmentShader = Optional.of(Identifier.withDefaultNamespace(fragmentShader));
            return this;
        }
        
        @Override
        public Builder withFragmentShader(Identifier fragmentShader) {
            this.fragmentShader = Optional.of(fragmentShader);
            return this;
        }
        
        @Override
        public Builder withVertexShader(String vertexShader) {
            this.vertexShader = Optional.of(Identifier.withDefaultNamespace(vertexShader));
            return this;
        }
        
        @Override
        public Builder withVertexShader(Identifier vertexShader) {
            this.vertexShader = Optional.of(vertexShader);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String flag) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(flag);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String key, int value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(key, value);
            return this;
        }
        
        @Override
        public Builder withShaderDefine(String key, float value) {
            if (this.definesBuilder.isEmpty()) {
                this.definesBuilder = Optional.of(ShaderDefines.builder());
            }
            
            this.definesBuilder.get().define(key, value);
            return this;
        }
        
        @Override
        public Builder withSampler(String sampler) {
            if (this.samplers.isEmpty()) {
                this.samplers = Optional.of(new ArrayList<>());
            }
            
            this.samplers.get().add(sampler);
            return this;
        }
        
        @Override
        public Builder withUniform(String uniform, UniformType type) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList<>());
            }
            
            if (type == UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Cannot use texel buffer without specifying texture format");
            } else {
                this.uniforms.get().add(new RenderPipeline.UniformDescription(uniform, type));
                return this;
            }
        }
        
        @Override
        public Builder withUniform(String uniform, UniformType type, TextureFormat format) {
            if (this.uniforms.isEmpty()) {
                this.uniforms = Optional.of(new ArrayList<>());
            }
            
            if (type != UniformType.TEXEL_BUFFER) {
                throw new IllegalArgumentException("Only texel buffer can specify texture format");
            } else {
                this.uniforms.get().add(new RenderPipeline.UniformDescription(uniform, format));
                return this;
            }
        }
        
        @Override
        public Builder withDepthTestFunction(DepthTestFunction depthTestFunction) {
            this.depthTestFunction = Optional.of(depthTestFunction);
            return this;
        }
        
        @Override
        public Builder withPolygonMode(PolygonMode polygonMode) {
            this.polygonMode = Optional.of(polygonMode);
            return this;
        }
        
        @Override
        public Builder withCull(boolean cull) {
            this.cull = Optional.of(cull);
            return this;
        }
        
        @Override
        public Builder withBlend(BlendFunction blendFunction) {
            this.blendFunction = Optional.of(blendFunction);
            return this;
        }
        
        @Override
        public Builder withoutBlend() {
            this.blendFunction = Optional.empty();
            return this;
        }
        
        @Override
        public Builder withColorWrite(boolean writeColor) {
            this.writeColor = Optional.of(writeColor);
            this.writeAlpha = Optional.of(writeColor);
            return this;
        }
        
        @Override
        public Builder withColorWrite(boolean writeColor, boolean writeAlpha) {
            this.writeColor = Optional.of(writeColor);
            this.writeAlpha = Optional.of(writeAlpha);
            return this;
        }
        
        @Override
        public Builder withDepthWrite(boolean writeDepth) {
            this.writeDepth = Optional.of(writeDepth);
            return this;
        }
        
        @Override
        public Builder withVertexFormat(VertexFormat vertexFormat, VertexFormat.Mode vertexFormatMode) {
            this.vertexFormat = Optional.of(vertexFormat);
            this.vertexFormatMode = Optional.of(vertexFormatMode);
            return this;
        }
        
        @Override
        public Builder withDepthBias(float scaleFactor, float constant) {
            this.depthBiasScaleFactor = scaleFactor;
            this.depthBiasConstant = constant;
            return this;
        }
        
        @Override
        public Builder withStencilTest(net.neoforged.neoforge.client.stencil.StencilTest stencilTest) {
            this.stencilTest = Optional.of(stencilTest);
            return this;
        }
        
        @Override
        public Builder withoutStencilTest(){
            this.stencilTest = Optional.empty();
            return this;
        }
        
        @Override
        public RenderPipeline build() {
            return this.buildExtended();
        }
        
        public ExtendedRenderPipeline buildExtended(){
            if (this.location.isEmpty()) {
                throw new IllegalStateException("Missing location");
            } else if (this.vertexShader.isEmpty()) {
                throw new IllegalStateException("Missing vertex shader");
            } else if (this.fragmentShader.isEmpty()) {
                throw new IllegalStateException("Missing fragment shader");
            } else if (this.vertexFormat.isEmpty()) {
                throw new IllegalStateException("Missing vertex buffer format");
            } else if (this.vertexFormatMode.isEmpty()) {
                throw new IllegalStateException("Missing vertex mode");
            } else {
                return new ExtendedRenderPipeline(
                        this.location.get(),
                        this.vertexShader.get(),
                        this.fragmentShader.get(),
                        this.definesBuilder.orElse(ShaderDefines.builder()).build(),
                        List.copyOf(this.samplers.orElse(new ArrayList<>())),
                        this.uniforms.orElse(Collections.emptyList()),
                        this.blendFunction,
                        this.depthTestFunction.orElse(DepthTestFunction.LEQUAL_DEPTH_TEST),
                        this.polygonMode.orElse(PolygonMode.FILL),
                        this.cull.orElse(true),
                        this.writeColor.orElse(true),
                        this.writeAlpha.orElse(true),
                        this.writeDepth.orElse(true),
                        this.colorLogic.orElse(LogicOp.NONE),
                        this.vertexFormat.get(),
                        this.vertexFormatMode.get(),
                        this.depthBiasScaleFactor,
                        this.depthBiasConstant,
                        nextPipelineSortKey++,
                        this.stencilTest,
                        this.UBOBindings,
                        this.samplerBindings,
                        this.SSBOs);
            }
        }
        
    }
    
}
