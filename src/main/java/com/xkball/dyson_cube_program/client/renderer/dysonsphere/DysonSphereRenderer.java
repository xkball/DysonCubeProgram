package com.xkball.dyson_cube_program.client.renderer.dysonsphere;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xkball.dyson_cube_program.api.IDGetter;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.ISTD140Writer;
import com.xkball.dyson_cube_program.client.DCPStandaloneModels;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.render_pipeline.TransformMat;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.InstanceInfo;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.MeshBundle;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.MeshBundleWithRenderPipeline;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonElementType;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonOrbitData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSphereLayerData;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@NonNullByDefault
public class DysonSphereRenderer {
    
    private final DysonSpareBlueprintData data;
    private final Map<RenderPipeline, MeshBundle<RenderPipeline>> meshes = new HashMap<>();
    
    public DysonSphereRenderer(DysonSpareBlueprintData data) {
        this.data = data;
    }
    
    public void buildMeshes(){
        for(var entry : meshes.entrySet()){
            entry.getValue().close();
        }
        meshes.clear();
        meshes.put(DCPRenderPipelines.POSITION_COLOR_INSTANCED, new MeshBundleWithRenderPipeline("dyson_sphere_position_color_instanced", DCPRenderPipelines.POSITION_COLOR_INSTANCED));
        meshes.put(RenderPipelines.DEBUG_QUADS, new MeshBundleWithRenderPipeline("dyson_sphere_debug_quad",RenderPipelines.DEBUG_QUADS));
        meshes.put(DCPRenderPipelines.DEBUG_LINE, new MeshBundleWithRenderPipeline("dyson_sphere_debug_lines",DCPRenderPipelines.DEBUG_LINE));
        var elements = data.type().elementTypes;
        if(elements.contains(DysonElementType.LAYER)){
            assert data.singleLayer() != null;
            renderSingleLayer(null, data.singleLayer());
        }
        if (elements.contains(DysonElementType.LAYERS)){
            var id = 1;
            var orbits = IDGetter.toMap(data.layerOrbits());
            for (int i = 0; i < data.layers().size(); i++) {
                var layer = data.layers().get(i);
                if(layer == null) continue;
                renderSingleLayer(orbits.get(id), layer);
                id += 1;
            }
        }
        if (elements.contains(DysonElementType.SWARMS)){
            for(int i = 0; i < data.swarmOrbits().size(); i++){
                if(!data.swarmOrbits().get(i).isValidOrbit()) continue;
                renderSingleSwarm(data.swarmOrbits().get(i),1000,data.sailOrbitColorHSVA().get(i));
            }
        }
    }
    
    public void render(PoseStack poseStack){
        poseStack.pushPose();
        
        for(var entry : meshes.entrySet()){
            entry.getValue().render(poseStack);
        }
        poseStack.popPose();
    }
    
    private void renderSingleLayer(@Nullable DysonOrbitData orbit, DysonSphereLayerData layer){
        var defaultColor = ColorUtils.getColor(8,19,23,255);
        var nodes = IDGetter.toMap(layer.nodePool());
        var setup = rotateOrbit(orbit);
        
        var modelManager = Minecraft.getInstance().getModelManager();
        var transformList = new ArrayList<TransformMat>();
        var nodeModel = modelManager.getStandaloneModel(DCPStandaloneModels.DYSON_NODE_KEY);
        if(nodeModel != null){
            var nodeBuilder = ClientUtils.beginWithRenderPipeline(RenderPipelines.DEBUG_QUADS);
            var _quads = nodeModel.getAll();
            for(var quad : _quads){
                var aint = quad.vertices();
                for (int i = 0; i < 4; i++) {
                    var x = Float.intBitsToFloat(aint[i*8]);
                    var y = Float.intBitsToFloat(aint[i*8+1]);
                    var z = Float.intBitsToFloat(aint[i*8+2]);
                    nodeBuilder.addVertex(x,y,z).setColor(-1);
                }
                
            }
            var poseStack = new PoseStack();
            for(var node : nodes.entries()){
                poseStack.pushPose();
                var u = new Vector3f(0,1,0);
                var d = node.value().pos().negate(new Vector3f()).normalize();
                var axis = u.cross(d,new Vector3f()).normalize();
                var theta = Math.acos(u.dot(d));
                poseStack.translate(node.value().pos().x,node.value().pos().y,node.value().pos().z);
                poseStack.scale(400,400,400);
                poseStack.mulPose(Axis.of(axis).rotation((float) theta));
                transformList.add(new TransformMat(new Matrix4f(poseStack.last().pose())));
                poseStack.popPose();
            }
            if(!nodes.isEmpty()) {
                var mesh = nodeBuilder.buildOrThrow();
                var buffer = ISTD140Writer.batchBuildStd140Block(transformList);
                var instanceInfo = new InstanceInfo(nodes.size(),"InstanceTransform",buffer.slice());
                meshes.computeIfPresent(DCPRenderPipelines.POSITION_COLOR_INSTANCED,
                        (k,v)-> v.appendImmediately(mesh,setup,instanceInfo));
            }
        }
       
        var lineBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.DEBUG_LINE);
        var frames = layer.framePool().stream().filter(Objects::nonNull).toList();
        for(var frame : frames){
            var nodeA = nodes.get(frame.nodeAID());
            var nodeB = nodes.get(frame.nodeBID());
            var color = ColorUtils.abgrToArgb(frame.color());
            if(color == 0) color = defaultColor;
            lineBuilder.addVertex(nodeA.pos()).setColor(color);
            lineBuilder.addVertex(nodeB.pos()).setColor(color);
        }
        if(!frames.isEmpty()) {
            var mesh = lineBuilder.buildOrThrow();
            meshes.computeIfPresent(DCPRenderPipelines.DEBUG_LINE,(k,v)-> v.appendImmediately(mesh,setup));
        }

        var shellBuilder = ClientUtils.beginWithRenderPipeline(RenderPipelines.DEBUG_QUADS);
        var shells = layer.shellPool().stream().filter(Objects::nonNull).toList();
        for(var shell : shells){
            assert shell.nodes().size() >= 3;
            var color = ColorUtils.abgrToArgb(shell.color());
            if(color == 0) color = defaultColor;
            var quads = ClientUtils.earClipping(shell.nodes().stream().map(i -> nodes.get(i).pos()).toList());
            for(var quad : quads){
                shellBuilder.addVertex(quad.a()).setColor(color);
                shellBuilder.addVertex(quad.b()).setColor(color);
                shellBuilder.addVertex(quad.c()).setColor(color);
                shellBuilder.addVertex(quad.d()).setColor(color);
            }
        }
        if(!shells.isEmpty()) {
            var mesh = shellBuilder.buildOrThrow();
            meshes.computeIfPresent(RenderPipelines.DEBUG_QUADS,(k,v)-> v.appendImmediately(mesh,setup));
        }
        
    }
    
    private void renderSingleSwarm(DysonOrbitData orbit, int sailCount, Vector4f color){
    
    }
    
    private static Consumer<PoseStack> rotateOrbit(@Nullable DysonOrbitData orbit){
        if(orbit == null) return p -> {};
        return p -> {
            p.mulPose(orbit.rotation());
            p.mulPose(Axis.YP.rotationDegrees(ClientUtils.clientTickWithPartialTick()/10));
        };
    }
}
