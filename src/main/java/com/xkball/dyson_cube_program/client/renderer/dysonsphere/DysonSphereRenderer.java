package com.xkball.dyson_cube_program.client.renderer.dysonsphere;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.xkball.dyson_cube_program.api.IDGetter;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.ISTD140Writer;
import com.xkball.dyson_cube_program.client.DCPStandaloneModels;
import com.xkball.dyson_cube_program.client.b3d.pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.client.b3d.mesh.InstanceInfo;
import com.xkball.dyson_cube_program.client.b3d.mesh.MeshBundle;
import com.xkball.dyson_cube_program.client.b3d.mesh.MeshBundleWithRenderPipeline;
import com.xkball.dyson_cube_program.client.b3d.uniform.TransMatColor;
import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonElementType;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonNodeData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonOrbitData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSpareBlueprintData;
import com.xkball.dyson_cube_program.common.dysonsphere.data.DysonSphereLayerData;
import com.xkball.dyson_cube_program.graph.BFSHandler;
import com.xkball.dyson_cube_program.utils.client.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import com.xkball.dyson_cube_program.utils.math.HexGrid;
import com.xkball.dyson_cube_program.utils.math.LatAndLon;
import com.xkball.dyson_cube_program.utils.math.MathConstants;
import com.xkball.dyson_cube_program.utils.math.Quad;
import com.xkball.dyson_cube_program.utils.math.SphereGeometryUtils;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@NonNullByDefault
public class DysonSphereRenderer {
    
    private static final Logger LOGGER = LogUtils.getLogger();
    public DysonSpareBlueprintData data;
    private final Map<RenderPipeline, MeshBundle<RenderPipeline>> meshes = new LinkedHashMap<>();
    private MeshBundle<RenderPipeline> inBloom = new MeshBundleWithRenderPipeline("dyson_sail",DCPRenderPipelines.DYSON_SAIL);
    private static final int defaultColor = ColorUtils.getColor(161,227,255,255);
    public DysonSphereRenderer(DysonSpareBlueprintData data) {
        this.data = data;
    }
    
    public long buildMeshes(){
        var time = System.currentTimeMillis();
        for(var entry : meshes.entrySet()){
            entry.getValue().close();
        }
        inBloom.close();
        inBloom = new MeshBundleWithRenderPipeline("dyson_sail",DCPRenderPipelines.DYSON_SAIL);
        meshes.clear();
        meshes.put(DCPRenderPipelines.POSITION_COLOR_INSTANCED, new MeshBundleWithRenderPipeline("dyson_sphere_position_color_instanced", DCPRenderPipelines.POSITION_COLOR_INSTANCED));
        meshes.put(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED, new MeshBundleWithRenderPipeline("dyson_sphere_position_tex_color_instanced", DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED));
        meshes.put(DCPRenderPipelines.POSITION_DUAL_TEX_COLOR,new MeshBundleWithRenderPipeline("dyson_shell",DCPRenderPipelines.POSITION_DUAL_TEX_COLOR));
        var poseStack = new PoseStack();
        var elements = data.type().elementTypes;
        if(elements.contains(DysonElementType.LAYER)){
            assert data.singleLayer() != null;
            renderSingleLayer(null, data.singleLayer(), poseStack);
        }
        if (elements.contains(DysonElementType.LAYERS)){
            var id = 1;
            var orbits = IDGetter.toMap(data.layerOrbits());
            for (int i = 0; i < data.layers().size(); i++) {
                var layer = data.layers().get(i);
                if(layer == null) continue;
                renderSingleLayer(orbits.get(id), layer, poseStack);
                id += 1;
            }
        }
        if (elements.contains(DysonElementType.SWARMS)){
            var rand = RandomSource.create(time);
            for(int i = 0; i < data.swarmOrbits().size(); i++){
                var orbit = data.swarmOrbits().get(i);
                if(!orbit.isValidOrbit()) continue;
                var count = rand.nextInt(1000,5000);
                renderSingleSwarm(orbit,count,data.sailOrbitColorHSVA().get(i),rand,rotateOrbit(orbit),poseStack);
            }
        }
        time = System.currentTimeMillis() - time;
        LOGGER.info("DysonSphereRenderer buildMeshes time: {}",time);
        return time;
    }
    
    public void renderBloom(PoseStack poseStack){
        var target = DCPPostProcesses.BLOOM.startPass(Minecraft.getInstance().getMainRenderTarget(), true);
        inBloom.render(poseStack, null, target.getColorTextureView(), target.getDepthTextureView());
        DCPPostProcesses.BLOOM.endPass(true);
    }
    
    public void render(PoseStack poseStack){
        poseStack.pushPose();
        //var cmdList = new GLCommandList();
        for(var entry : meshes.entrySet()){
            entry.getValue().render(poseStack);
        }
        //cmdList.close();
        poseStack.popPose();
    }
    
    private void renderSingleLayer(@Nullable DysonOrbitData orbit, DysonSphereLayerData layer, PoseStack poseStack){
        var nodes = IDGetter.toMap(layer.nodePool());
        var setup = rotateOrbit(orbit);
        
        this.renderDysonNodes(poseStack, nodes, orbit, setup);
        this.renderDysonFrame(poseStack, nodes, layer, setup);
        this.renderDysonShell(poseStack, nodes, layer, setup, orbit == null ? new Quaternionf() : orbit.rotation());
    }
    
    private void renderDysonNodes(PoseStack poseStack, IntObjectMap<DysonNodeData> nodes,@Nullable DysonOrbitData orbit, Consumer<PoseStack> setup){
        var modelManager = Minecraft.getInstance().getModelManager();
        var nodeModel = modelManager.getStandaloneModel(DCPStandaloneModels.DYSON_NODE_KEY);
        if(nodeModel == null) return;
        var transformList = new ArrayList<TransMatColor>();
        var nodeBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.POSITION_COLOR_INSTANCED);
        ClientUtils.putModelToBuffer(poseStack,nodeBuilder,nodeModel.getAll(),-1);
        for(var node : nodes.entries()){
            var color = ColorUtils.abgrToArgb(node.value().color());
            if(color == 0) color = defaultColor;
            var pos = node.value().pos();
            var spPos = LatAndLon.fromSperePos(pos);
            var nextPos = LatAndLon.ofDegree(spPos.getLatDegree()+10,spPos.getLonDegree()+10).toSperePos();
            poseStack.pushPose();
            poseStack.translate(pos.x,pos.y,pos.z);
            poseStack.translate(new Vec3(pos.mul(0.004f,new Vector3f())));
            poseStack.scale(400,400,400);
            poseStack.mulPose(facingYOriginFacingXTo(pos,nextPos));
            transformList.add(new TransMatColor(new Matrix4f(poseStack.last().pose()), ColorUtils.Vectorization.argbColor(color)));
            poseStack.popPose();
        }
        if(!nodes.isEmpty()) {
            var mesh = nodeBuilder.buildOrThrow();
            var buffer = ISTD140Writer.batchBuildStd140Block(transformList);
            var instanceInfo = new InstanceInfo(nodes.size(),"InstanceTransformColor",buffer.slice());
            meshes.computeIfPresent(DCPRenderPipelines.POSITION_COLOR_INSTANCED,
                    (k,v)-> v.appendImmediately(mesh,setup,instanceInfo));
        }
    }
    
    private void renderDysonFrame(PoseStack poseStack, IntObjectMap<DysonNodeData> nodes, DysonSphereLayerData layer, Consumer<PoseStack> setup){
        var modelManager = Minecraft.getInstance().getModelManager();
        var frameModel = modelManager.getStandaloneModel(DCPStandaloneModels.DYSON_FRAME_KEY);
        if(frameModel == null) return;
        var lineBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.POSITION_TEX_COLOR_INSTANCED);
        var transformList = new ArrayList<TransMatColor>();
        ClientUtils.putModelToBuffer(poseStack,lineBuilder,frameModel.getAll(),-1);
        var frames = layer.framePool().stream().filter(Objects::nonNull).toList();
        int count = 0;
        for(var frame : frames){
            var color = ColorUtils.abgrToArgb(frame.color());
            if(color == 0) color = defaultColor;
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
                poseStack.pushPose();
                poseStack.translate(pos.x()*0.9995f,pos.y()*0.9995f,pos.z()*0.9995f);
                poseStack.mulPose(facingYOriginFacingXTo(pos,nextPos));
                poseStack.scale(l*d,500,300);
                //面反向了 原因未知 但是无所谓再反一次就行
                poseStack.scale(-1,-1f,-1);
                transformList.add(new TransMatColor(new Matrix4f(poseStack.last().pose()),ColorUtils.Vectorization.argbColor(color)));
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
    
    private void renderDysonShell(PoseStack poseStack, IntObjectMap<DysonNodeData> nodes, DysonSphereLayerData layer, Consumer<PoseStack> setup, Quaternionf orbit){
        var shellBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.POSITION_DUAL_TEX_COLOR);
        var shells = layer.shellPool().stream().filter(Objects::nonNull).toList();
        for(var shell : shells){
            assert shell.nodes().size() >= 3;
            var color = ColorUtils.abgrToArgb(shell.color());
            if(color == 0) color = defaultColor;
            var quads = SphereGeometryUtils.earClipping(shell.nodes().stream().map(i -> nodes.get(i).pos()).toList());
            var center = shell.nodes().stream()
                    .map(i -> nodes.get(i).pos())
                    .reduce((a,b) -> a.add(b,new Vector3f()))
                    .orElseThrow().mul(1.0f/shell.nodes().size(),new Vector3f()).normalize();
            var target = MathConstants.ICOSAHEDRON_FACE1_CENTER;
            var centerLL=  LatAndLon.fromSperePos(center);
            var targetLL = LatAndLon.fromSperePos(target);
            var mat = centerLL.rotationTo(targetLL);
            poseStack.pushPose();
            var s = quads.getFirst().a().length();
            poseStack.scale(s,s,s);
            var filteredNodes = filterNodesInShell(new HexGrid((int) (s/384)), mat, quads);
            var sides = shell.getSides(nodes).stream().map(p -> Pair.of(p.getFirst().normalize(new Vector3f()),p.getSecond().normalize(new Vector3f()))).toList();
            for(var node : filteredNodes){
                node.transform(mat);
                node.makeQuads(poseStack.last(),shellBuilder, color, quads, sides, !node.contextInsideQuad());
            }
            poseStack.popPose();
        }

        if(!shells.isEmpty()) {
            var mesh = shellBuilder.buildOrThrow();
            meshes.computeIfPresent(DCPRenderPipelines.POSITION_DUAL_TEX_COLOR,(k,v)-> v.appendImmediately(mesh,setup));
        }
    }
    
    private void renderSingleSwarm(DysonOrbitData orbit, int sailCount, Vector4f color,RandomSource rand, Consumer<PoseStack> setup, PoseStack poseStack){
        var sailBuilder = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.DYSON_SAIL);
        var c = color.equals(MathConstants.ZERO_VEC4) ? ColorUtils.getColor(255,255,255,255) : ColorUtils.hsvaToRgba(color);
        sailBuilder.addVertex( 0,0,0).setColor(0);
        sailBuilder.addVertex( 1,0,0).setColor(0);
        sailBuilder.addVertex( 0.5f, 0, (float) (Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex(-0.5f, 0, (float) (Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex( 0,0,0).setColor(0);
        sailBuilder.addVertex(-0.5f, 0, (float) (Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex(-1,0,0).setColor(0);
        sailBuilder.addVertex(-0.5f, 0, (float) (-Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex( 0,0,0).setColor(0);
        sailBuilder.addVertex(-0.5f, 0, (float) (-Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex( 0.5f, 0, (float) (-Math.sqrt(3)/2f)).setColor(0);
        sailBuilder.addVertex( 1,0,0).setColor(0);
        sailBuilder.addVertex( 0,0,0).setColor(ColorUtils.getColor(0,0,0,255));
        sailBuilder.addVertex( 0,0,0).setColor(ColorUtils.getColor(255,0,0,255));
        sailBuilder.addVertex( 0,0,0).setColor(ColorUtils.getColor(255,255,0,255));
        sailBuilder.addVertex( 0,0,0).setColor(ColorUtils.getColor(0,255,0,255));
        var transformList = new ArrayList<TransMatColor>();
        for (int i = 0; i < sailCount; i++) {
            var r = Mth.frac(rand.nextFloat())*Math.PI*2;
            var pos = new Vector3f((float) (Math.cos(r)),0,(float) (Math.sin(r))).normalize(orbit.radius());
            var spread = (float)(orbit.radius()*0.03f*rand.nextGaussian());
            pos.add(Mth.frac(rand.nextFloat())*spread-0.5f*spread,Mth.frac(rand.nextFloat())*spread-0.5f*spread,Mth.frac(rand.nextFloat())*spread-0.5f*spread);
            poseStack.pushPose();
            poseStack.translate(pos.x,pos.y,pos.z);
            poseStack.mulPose(facingYOrigin(pos));
            poseStack.scale(20,20,20);
            transformList.add(new TransMatColor(new Matrix4f(poseStack.last().pose()),ColorUtils.Vectorization.argbColor(c)));
            poseStack.popPose();
        }
        var mesh = sailBuilder.buildOrThrow();
        var buffer = ISTD140Writer.batchBuildStd140Block(transformList);
        var instanceInfo = new InstanceInfo(sailCount,"InstanceTransformColor",buffer.slice());
        inBloom.appendImmediately(mesh,setup,instanceInfo);
    }
    
    private static Quaternionf facingYOrigin(Vector3f pos){
        var u = MathConstants.Y_POSITIVE;
        var d = pos.negate(new Vector3f()).normalize();
        var axis = u.cross(d,new Vector3f()).normalize();
        var theta = Math.acos(u.dot(d));
        return new Quaternionf().rotationAxis((float) theta,axis);
    }
    
    private static Quaternionf facingYOriginFacingXTo(Vector3f pos, Vector3f to){
        pos = pos.normalize(new Vector3f());
        to = to.normalize(new Vector3f());
        var mulY = facingYOrigin(pos);
        var pb = to.sub(pos,new Vector3f()).normalize();
        var rotationDir = MathConstants.X_POSITIVE.rotate(mulY,new Vector3f()).normalize();
        var dotDir = pb.cross(rotationDir,new Vector3f()).normalize();
        var sameFacing = dotDir.dot(pos) > 0;
        var theta = -Math.acos(rotationDir.dot(pb));
        if(!sameFacing) theta = -theta;
        var mulX = new Quaternionf().rotateAxis((float)theta,pos);
        return mulX.mul(mulY);
    }
    
    private static Consumer<PoseStack> rotateOrbit(@Nullable DysonOrbitData orbit){
        if(orbit == null) return p -> {};
        return p -> {
            p.mulPose(orbit.rotation());
            p.mulPose(Axis.YP.rotationDegrees(ClientUtils.clientTickWithPartialTick()/16));
        };
    }
    
    public List<HexGrid.Node> filterNodesInShell(HexGrid grid, Matrix4f mat, List<Quad> quads){
        var center = grid.getNode(grid.layers*2/3,grid.layers/3);
        if(center != null) {
            var list = new ArrayList<HexGrid.Node>();
            var bsf = new BFSHandler<HexGrid.Node>((n,i) -> {
                list.add(n);
                return SphereGeometryUtils.insideQuads(mat.transformPosition(n.spherePos,n.contextPos), quads);
            });
            bsf.traverse(center.nearCenters().getFirst(), HexGrid.Node::nearCenters,-1);
            if(list.size() > 10) return list;
        }
        var list = grid.map.stream().flatMap(List::stream).filter(node -> node.type == HexGrid.NodeType.CENTER).toList();
        List<HexGrid.Node> result = new ArrayList<>();
        for (HexGrid.Node node : list) {
            var pos0 = mat.transformPosition(node.spherePos, node.contextPos);
            var flag = SphereGeometryUtils.insideQuads(pos0, quads);
            if (!flag) {
                boolean b = false;
                for (HexGrid.Node nn : node.neighbors()) {
                    if (SphereGeometryUtils.insideQuads(mat.transformPosition(nn.spherePos, nn.contextPos), quads)) {
                        b = true;
                        break;
                    }
                }
                flag = b;
            }
            if (flag) {
                result.add(node);
            }
        }
        return result;
    }
    

}
