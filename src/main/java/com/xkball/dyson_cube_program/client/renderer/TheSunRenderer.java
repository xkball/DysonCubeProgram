package com.xkball.dyson_cube_program.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.xkball.dyson_cube_program.client.postprocess.DCPPostProcesses;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderTypes;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.util.Lazy;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TheSunRenderer {
    
    public static final int SUN_COLOR = ColorUtils.getColor(255,77,57,255);
    //public static final int SUN_COLOR = VanillaUtils.getColor(159,248,229,255);

    private static final Lazy<VertexBuffer> NEAR_SUN_MESH = Lazy.of(() -> ClientUtils.fromMesh(createCubeSphereMesh(10,false)));
    private static final Lazy<VertexBuffer> FAR_SUN_MESH = Lazy.of(() -> ClientUtils.fromMesh(createCubeSphereMesh(3,false)));
    private static final Lazy<VertexBuffer> NEGATIVE_NEAR_SUN_MESH = Lazy.of(() -> ClientUtils.fromMesh(createCubeSphereMesh(10,true)));
    
    private static final Lazy<VertexBuffer> RING_MESH = Lazy.of(() -> ClientUtils.fromMesh(createRingMesh()));
    
    public static Vector3f renderingCenter = new Vector3f();
    public static int contextColor = -1;
    
    private static final Vector3f[][] BASE_POINTS = new Vector3f[][]{
            {new Vector3f(1,1,1),  new Vector3f(1,1,-1),  new Vector3f(1,-1,-1),  new Vector3f(1,-1,1)},
            {new Vector3f(-1,1,1), new Vector3f(-1,-1,1), new Vector3f(-1,-1,-1), new Vector3f(-1,1,-1)},
            {new Vector3f(1,1,1),  new Vector3f(1,1,-1),  new Vector3f(-1,1,-1),  new Vector3f(-1,1,1)},
            {new Vector3f(1,-1,1), new Vector3f(-1,-1,1), new Vector3f(-1,-1,-1), new Vector3f(1,-1,-1)},
            {new Vector3f(1,1,1),  new Vector3f(1,-1,1),  new Vector3f(-1,-1,1),  new Vector3f(-1,1,1)},
            {new Vector3f(1,1,-1), new Vector3f(-1,1,-1), new Vector3f(-1,-1,-1), new Vector3f(1,-1,-1)},
    };
    
    public static void setRenderingCenter(Vector3f center) {
        renderingCenter = center;
    }
    
    public static MeshData createCubeSphereMesh(int edgePoints, boolean negative){
        var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for(var d = 0; d < BASE_POINTS.length; d++){
            var points = BASE_POINTS[d];
            var di = points[1].sub(points[0],new Vector3f()).mul(1f/(edgePoints+1));
            var dj = points[3].sub(points[0],new Vector3f()).mul(1f/(edgePoints+1));
            for (int i = 0; i < edgePoints + 1; i++) {
                for (int j = 0; j < edgePoints + 1; j++) {
                    var p0 = di.mul(i,new Vector3f()).add(dj.mul(j,new Vector3f())).add(points[0]);
                    var p1 = p0.add(di,new Vector3f());
                    var p2 = p1.add(dj,new Vector3f());
                    var p3 = p0.add(dj,new Vector3f());
                    if((d != 2 && d != 3) != negative){
                        var temp = p3;
                        p3 = p1;
                        p1 = temp;
                    }
                    builder.addVertex(p0.normalize());
                    builder.addVertex(p1.normalize());
                    builder.addVertex(p2.normalize());
                    builder.addVertex(p3.normalize());
                }
            }
        }
        return builder.buildOrThrow();
    }
    
    public static MeshData createRingMesh(){
        var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        for(var i = 0; i < 36; i++){
            var x1 = (float) Math.cos(Math.toRadians(i*10));
            var y1 = (float) Math.sin(Math.toRadians(i*10));
            var x2 = (float) Math.cos(Math.toRadians((i+1)*10));
            var y2 = (float) Math.sin(Math.toRadians((i+1)*10));
            builder.addVertex(new Vector3f(x1,y1,0).mul(1.8f));
            builder.addVertex(new Vector3f(x1,y1,0).mul(0.8f));
            builder.addVertex(new Vector3f(x2,y2,0).mul(0.8f));
            builder.addVertex(new Vector3f(x2,y2,0).mul(1.8f));
        }
        return builder.buildOrThrow();
    }
    
    public static Vector3f getRenderDirection(){
        return TheSunRenderer.renderingCenter.sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f(),new Vector3f());
    }
    
    public static void drawSunAt(PoseStack poseStack, Vector3f center, int color){
        DCPPostProcesses.BLOOM.bindAndClear(true);
        TheSunRenderer.contextColor = color;
        TheSunRenderer.setRenderingCenter(center);
        
        ClientUtils.drawWithRenderType(DCPRenderTypes.THE_SUN_0, TheSunRenderer.NEAR_SUN_MESH.get(),poseStack);

        poseStack.pushPose();
        poseStack.scale(1.05f, 1.05f, 1.05f);
        ClientUtils.drawWithRenderType(DCPRenderTypes.THE_SUN_1, TheSunRenderer.NEAR_SUN_MESH.get(),poseStack);
        poseStack.popPose();
        
        poseStack.pushPose();
        var dir = getRenderDirection().normalize();
        var forward = new Vector3f(0, 0, 1);
        var dot = forward.dot(dir);
        //todo 存在突变
        var flag = dot < -0.5f;
        if(flag){
            forward = new Vector3f(0, 0, -1);
            dot = forward.dot(dir);
        }
        var angle = (float)Math.acos(dot);
        var axis = forward.cross(dir, new Vector3f()).normalize();
        poseStack.mulPose(new Quaternionf().rotationAxis(angle, axis));
        if(flag) poseStack.mulPose(Axis.YP.rotationDegrees(180));
        ClientUtils.drawWithRenderType(DCPRenderTypes.THE_SUN_2, TheSunRenderer.RING_MESH.get(),poseStack);
        poseStack.popPose();

        DCPPostProcesses.BLOOM.applyAndUnbind(true);
    }
}
