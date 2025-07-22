package com.xkball.dyson_cube_program.client.renderer.dysonsphere;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.xkball.dyson_cube_program.api.IDGetter;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.client.ClientEvent;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonElementType;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonOrbitData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSphereLayerData;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Vector3f;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@NonNullByDefault
public class DysonSphereRenderer {
    
    private final DysonSpareBlueprintData data;
    private final Multimap<RenderType, Pair<VertexBuffer, Consumer<PoseStack>>> meshes = LinkedHashMultimap.create();
    
    public DysonSphereRenderer(DysonSpareBlueprintData data) {
        this.data = data;
    }
    
    public void buildMeshes(){
        meshes.clear();
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
        for(var entry : meshes.asMap().entrySet()){
            for(var meshAndSetup : entry.getValue()){
                poseStack.pushPose();
                meshAndSetup.getSecond().accept(poseStack);
                poseStack.mulPose(Axis.YP.rotationDegrees(ClientUtils.clientTickWithPartialTick()/10));
                ClientUtils.drawWithRenderType(entry.getKey(), meshAndSetup.getFirst(), poseStack);
                poseStack.popPose();
            }
        }
    }
    
    private void renderSingleLayer(@Nullable DysonOrbitData orbit, DysonSphereLayerData layer){
        var modelManager = Minecraft.getInstance().getModelManager();
        var nodeModel = modelManager.getModel(ClientEvent.Models.DYSON_NODE);
        var defaultColor = ColorUtils.getColor(8,19,23,255);
        var nodes = IDGetter.toMap(layer.nodePool());
        var setup = rotateOrbit(orbit);
        
        var nodeBuilder = ClientUtils.beginWithRenderType(RenderType.DEBUG_QUADS);
        var _quads = nodeModel.getQuads(null, null, RandomSource.create(), ModelData.EMPTY, null);
        var poseStack = new PoseStack();
        for(var node : nodes.entries()){
            poseStack.pushPose();
            var u = new Vector3f(0,1,0);
            var d = node.value().pos().negate(new Vector3f()).normalize();
            var axis = u.cross(d,new Vector3f()).normalize();
            var theta = Math.acos(u.dot(d));
            //poseStack.translate(-0.5,0,-0.5);
            poseStack.scale(1000,1000,1000);
            //poseStack.mulPose(Axis.of(axis).rotation((float) theta));
            poseStack.translate(node.value().pos().x,node.value().pos().y,node.value().pos().z);
            for(var quad : _quads){
                var aint = quad.getVertices();
                for (int i = 0; i < 4; i++) {
                    var x = Float.intBitsToFloat(aint[i*8]);
                    var y = Float.intBitsToFloat(aint[i*8+1]);
                    var z = Float.intBitsToFloat(aint[i*8+2]);
                    nodeBuilder.addVertex(poseStack.last(),x,y,z).setColor(-1);
                }
                
            }
            poseStack.popPose();
        }
        if(!nodes.isEmpty()) meshes.put(RenderType.DEBUG_QUADS,Pair.of(ClientUtils.fromMesh(nodeBuilder.buildOrThrow()),setup));
        
//        var lineBuilder = ClientUtils.beginWithRenderType(DCPRenderTypes.DEBUG_LINE);
//        var frames = layer.framePool().stream().filter(Objects::nonNull).toList();
//        for(var frame : frames){
//            var nodeA = nodes.get(frame.nodeAID());
//            var nodeB = nodes.get(frame.nodeBID());
//            var color = ColorUtils.abgrToArgb(frame.color());
//            if(color == 0) color = defaultColor;
//            lineBuilder.addVertex(nodeA.pos()).setColor(color);
//            lineBuilder.addVertex(nodeB.pos()).setColor(color);
//        }
//        if(!frames.isEmpty()) meshes.put(RenderType.lines(),Pair.of(ClientUtils.fromMesh(lineBuilder.buildOrThrow()),setup));
//
//        var shellBuilder = ClientUtils.beginWithRenderType(RenderType.DEBUG_QUADS);
//        var shells = layer.shellPool().stream().filter(Objects::nonNull).toList();
//        for(var shell : shells){
//            assert shell.nodes().size() >= 3;
//            var color = ColorUtils.abgrToArgb(shell.color());
//            if(color == 0) color = defaultColor;
//            var quads = ClientUtils.earClipping(shell.nodes().stream().map(i -> nodes.get(i).pos()).toList());
//            for(var quad : quads){
//                shellBuilder.addVertex(quad.a()).setColor(color);
//                shellBuilder.addVertex(quad.b()).setColor(color);
//                shellBuilder.addVertex(quad.c()).setColor(color);
//                shellBuilder.addVertex(quad.d()).setColor(color);
//            }
//        }
//        if(!shells.isEmpty()) meshes.put(RenderType.DEBUG_QUADS,Pair.of(ClientUtils.fromMesh(shellBuilder.buildOrThrow()),setup));
        
    }
    
    private void renderSingleSwarm(DysonOrbitData orbit, int sailCount, Vector4f color){
    
    }
    
    private static Consumer<PoseStack> rotateOrbit(@Nullable DysonOrbitData orbit){
        if(orbit == null) return p -> {};
        return p -> {
            p.mulPose(orbit.rotation());
        };
    }
}
