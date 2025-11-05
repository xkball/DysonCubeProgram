package com.xkball.dyson_cube_program.client.renderer.dysonsphere;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.api.IDGetter;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.ISTD140Writer;
import com.xkball.dyson_cube_program.client.DCPStandaloneModels;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.TransMat;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.InstanceInfo;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.MeshBundle;
import com.xkball.dyson_cube_program.client.render_pipeline.mesh.MeshBundleWithRenderPipeline;
import com.xkball.dyson_cube_program.client.render_pipeline.uniform.TransMatColor;
import com.xkball.dyson_cube_program.client.renderer.SphereHexGridMesh;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonElementType;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonOrbitData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSphereLayerData;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import com.xkball.dyson_cube_program.utils.math.MathConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
    
    public DysonSpareBlueprintData data;
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
        meshes.put(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED, new MeshBundleWithRenderPipeline("dyson_sphere_position_tex_color_instanced", DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED));
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
        var defaultColor = ColorUtils.getColor(73,140,163,255);
        var nodes = IDGetter.toMap(layer.nodePool());
        var setup = rotateOrbit(orbit);
        var modelManager = Minecraft.getInstance().getModelManager();
        var poseStack = new PoseStack();
        
        var nodeModel = modelManager.getStandaloneModel(DCPStandaloneModels.DYSON_NODE_KEY);
        if(nodeModel != null){
            var transformList = new ArrayList<TransMat>();
            var nodeBuilder = ClientUtils.beginWithRenderPipeline(RenderPipelines.DEBUG_QUADS);
            
            ClientUtils.putModelToBuffer(poseStack,nodeBuilder,nodeModel.getAll(),defaultColor);
            for(var node : nodes.entries()){
                poseStack.pushPose();
                poseStack.translate(node.value().pos().x,node.value().pos().y,node.value().pos().z);
                poseStack.scale(400,400,400);
                poseStack.mulPose(facingOrigin(node.value().pos()));
                transformList.add(new TransMat(new Matrix4f(poseStack.last().pose())));
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
       
        var frameModel = modelManager.getStandaloneModel(DCPStandaloneModels.DYSON_FRAME_KEY);
        if(frameModel != null){
            var lineBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED);
            var debugLineBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.DEBUG_LINE);
            var transformList = new ArrayList<TransMatColor>();
            ClientUtils.putModelToBuffer(poseStack,lineBuilder,frameModel.getAll(),-1);
            var frames = layer.framePool().stream().filter(Objects::nonNull).toList();
            int count = 0;
            for(var frame : frames){
                var color = ColorUtils.abgrToArgb(frame.color());
                if(color == 0) color = defaultColor;
                color = -1;
                var nodeA = nodes.get(frame.nodeAID());
                var nodeB = nodes.get(frame.nodeBID());
                var posA = nodeA.pos();
                var posB = nodeB.pos();
                var dirA = posA.normalize(new Vector3f());
                var dirB = posB.normalize(new Vector3f());
                var r = (posA.length() + posB.length()) / 2;
                var cosThetaAB = dirA.dot(dirB);
                var thetaAB = Math.toDegrees(Math.acos(cosThetaAB));
                var n = thetaAB/1.25;
                var fracN = Mth.frac(n);
                n = Mth.lfloor(n);
                float firstAndLastL;
                if(fracN < 0.5){
                    firstAndLastL = (float) ((1-fracN) /2);
                    n = n+2;
                }
                else{
                    firstAndLastL = (float) ((1+fracN) /2);
                    n = n+1;
                }
                count += (int) n;
                float l = (float) (Math.toRadians(1.25) * r);
                var normal = posA.cross(posB,new Vector3f()).normalize();
                float currentL = 0;
                var posList = new ArrayList<Vector3f>();
                for (int i = 0; i < n; i++) {
                    var d = ( i == 0 || i == n - 1) ? firstAndLastL : 1;
                    currentL += d/2;
                    var q = new Quaternionf().rotateAxis((float) Math.toRadians(1.25 * currentL),normal);
                    var p = posA.rotate(q,new Vector3f());
                    posList.add(p);
                   
                    currentL += d/2;
                }
                posList.add(posB);
                for(int i = 0; i < posList.size() - 1; i++){
                    var d = ( i == 0 || i == n - 1) ? firstAndLastL : 1;
                    var pos = posList.get(i);
                    var nextPos = posList.get(i+1);
                    var foq = facingOrigin(pos);
                    var pb = nextPos.sub(pos,new Vector3f()).normalize();
                    var rotationDir = MathConstants.X_POSITIVE.rotate(foq,new Vector3f());
                   
                    var theta = -Math.acos(rotationDir.dot(pb));
                    poseStack.pushPose();
                    poseStack.translate(pos.x(),pos.y(),pos.z());
                    poseStack.mulPose(new Quaternionf().rotateAxis((float)theta,pos));
                    poseStack.mulPose(foq);
                    poseStack.scale(l*d,200,200);
                    //面反向了 原因未知 但是无所谓再反一次就行
                    poseStack.scale(-1,-1,-1);
                    transformList.add(new TransMatColor(new Matrix4f(poseStack.last().pose()),ColorUtils.Vectorization.rgbaColor(color)));
                    poseStack.popPose();
                }
            }
            if(!frames.isEmpty()) {
                var mesh = lineBuilder.buildOrThrow();
                var buffer = ISTD140Writer.batchBuildStd140Block(transformList);
                var instanceInfo = new InstanceInfo(count,"InstanceTransformColor",buffer.slice());
                meshes.computeIfPresent(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED,(k,v)-> v.appendImmediately(mesh,setup,instanceInfo));
            }
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
        poseStack.pushPose();
        poseStack.scale(60000,60000,60000);
        var shgm = new SphereHexGridMesh(7);
        meshes.computeIfPresent(DCPRenderPipelines.DEBUG_LINE,(k,v) -> v.appendImmediately(shgm.buildMesh_(poseStack)));
        poseStack.popPose();
    }
    
    private void renderSingleSwarm(DysonOrbitData orbit, int sailCount, Vector4f color){
    
    }
    
    private static Quaternionf facingOrigin(Vector3f pos){
        var u = MathConstants.Y_POSITIVE;
        var d = pos.negate(new Vector3f()).normalize();
        var axis = u.cross(d,new Vector3f()).normalize();
        var theta = Math.acos(u.dot(d));
        return new Quaternionf().rotationAxis((float) theta,axis);
    }
    
    private static Consumer<PoseStack> rotateOrbit(@Nullable DysonOrbitData orbit){
        if(orbit == null) return p -> {};
        return p -> {
            p.mulPose(orbit.rotation());
            //p.mulPose(Axis.YP.rotationDegrees(ClientUtils.clientTickWithPartialTick()/10));
        };
    }
}
