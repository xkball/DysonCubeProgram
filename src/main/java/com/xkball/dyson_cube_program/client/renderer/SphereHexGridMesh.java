package com.xkball.dyson_cube_program.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import org.joml.Vector3f;

import java.util.List;

public class SphereHexGridMesh {
    private static final float phi = (1.0f + (float)Math.sqrt(5.0)) / 2.0f;
    
    public static final List<Vector3f> icosahedronVertices = List.of(
            new Vector3f(0,  1, phi).normalize(),  new Vector3f(-1, phi, 0).normalize(),  new Vector3f(1, phi, 0).normalize(),
            new Vector3f(0,  1, phi).normalize(),   new Vector3f(1,  phi,  0).normalize(),   new Vector3f(phi, 0, 1).normalize(),
            new Vector3f(0,  1, phi).normalize(),   new Vector3f(phi,  0,  1).normalize(),   new Vector3f(0, -1, phi).normalize(),
            new Vector3f(0,  1, phi).normalize(),    new Vector3f(0,  -1,phi).normalize(),   new Vector3f(-phi, 0, 1).normalize(),
            new Vector3f(0,  1, phi).normalize(),    new Vector3f(-phi, 0, 1).normalize(),   new Vector3f(-1, phi, 0).normalize(),
            new Vector3f(0, -1, phi).normalize(),    new Vector3f(phi, 0,  1).normalize(),   new Vector3f(1, -phi, 0).normalize(),
            new Vector3f(0, -1, phi).normalize(),    new Vector3f(1, -phi, 0).normalize(),   new Vector3f(-1, -phi, 0).normalize(),
            new Vector3f(0, -1, phi).normalize(),    new Vector3f(-1, -phi,0).normalize(),   new Vector3f(-phi, 0, 1).normalize(),
            new Vector3f(phi, 0,  1).normalize(),    new Vector3f(1, phi,  0).normalize(),   new Vector3f(phi, 0, -1).normalize(),
            new Vector3f(phi, 0,  1).normalize(),    new Vector3f(phi, 0, -1).normalize(),   new Vector3f(1, -phi, 0).normalize(),
            new Vector3f(-phi, 0,  1).normalize(),   new Vector3f(-1,-phi,   0).normalize(),   new Vector3f(-phi, 0, -1).normalize(),
            new Vector3f(-phi, 0,  1).normalize(),   new Vector3f(-phi, 0,  -1).normalize(),   new Vector3f(-1, phi, 0).normalize(),
            new Vector3f( phi, 0, -1).normalize(),   new Vector3f(1,  phi,   0).normalize(),   new Vector3f(0,    1, -phi).normalize(),
            new Vector3f(0,   1,-phi).normalize(),   new Vector3f(-1, phi,   0).normalize(),   new Vector3f(-phi, 0, -1).normalize(),
            new Vector3f(-phi, 0, -1).normalize(),   new Vector3f(0,  -1, -phi).normalize(),   new Vector3f(-1,-phi, 0).normalize(),
            new Vector3f(-phi, 0, -1).normalize(),   new Vector3f(0,   1, -phi).normalize(),   new Vector3f(0, -1, -phi).normalize(),
            new Vector3f(0,  1, -phi).normalize(),   new Vector3f(0,  -1, -phi).normalize(),   new Vector3f(phi,0, -1).normalize(),
            new Vector3f(phi,0,   -1).normalize(),   new Vector3f(0,  -1, -phi).normalize(),   new Vector3f(1, -phi, 0).normalize(),
            new Vector3f(-1, phi,   0).normalize(),   new Vector3f(1, phi, 0).normalize(),   new Vector3f(0, 1, -phi).normalize(),
            new Vector3f(-1, -phi,   0).normalize(),   new Vector3f(1, -phi, 0).normalize(),   new Vector3f(0, -1, -phi).normalize()
            //new Vector3f(0, -1, -phi).normalize(),   new Vector3f(1,-phi,    0).normalize(),   new Vector3f(0, -1, phi).normalize()
    );
    public final int depth;
    
    public SphereHexGridMesh(int depth) {
        this.depth = depth;
    }
    
    public MeshData buildMesh_(PoseStack poseStack) {
        var buffer = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.DEBUG_LINE);
        var pose = poseStack.last();
        buffer.addVertex(pose, 0, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(pose, 10, 0, 0).setColor(0xFFFF0000);
        buffer.addVertex(pose, 0, 0, 0).setColor(0xFF00FF00);
        buffer.addVertex(pose, 0, 10, 0).setColor(0xFF00FF00);
        buffer.addVertex(pose, 0, 0, 0).setColor(0xFF0000FF);
        buffer.addVertex(pose, 0, 0, 10).setColor(0xFF0000FF);
        for(int i = 0; i < icosahedronVertices.size(); i+=3) {
            buffer.addVertex(pose, icosahedronVertices.get(i  )).setColor(0xFF000000);
            buffer.addVertex(pose, icosahedronVertices.get(i+1)).setColor(0xFF000000);

            buffer.addVertex(pose, icosahedronVertices.get(i+1)).setColor(0xFF000000);
            buffer.addVertex(pose, icosahedronVertices.get(i+2)).setColor(0xFF000000);

            buffer.addVertex(pose, icosahedronVertices.get(i+2)).setColor(0xFF000000);
            buffer.addVertex(pose, icosahedronVertices.get(i  )).setColor(0xFF000000);
            appendTriangleRecursion(buffer,pose,icosahedronVertices.get(i),icosahedronVertices.get(i+1),icosahedronVertices.get(i+2),depth);
        }
        return buffer.buildOrThrow();
    }
    
    private void appendTriangleRecursion(BufferBuilder buffer, PoseStack.Pose pose, Vector3f a, Vector3f b, Vector3f c, int depth) {
        if(depth > 0) {
            var abHalf = a.add(b.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            var bcHalf = b.add(c.sub(b,new Vector3f()).mul(0.5f),new Vector3f());
            var acHalf = a.add(c.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            appendTriangleRecursion(buffer, pose, a, abHalf, acHalf, depth-1);
            appendTriangleRecursion(buffer, pose, abHalf, b, bcHalf, depth-1);
            appendTriangleRecursion(buffer, pose, acHalf, bcHalf, c, depth-1);
            appendTriangleRecursion(buffer, pose, abHalf, bcHalf, acHalf, depth-1);
        }
        else {
            var an = a.normalize(new Vector3f());
            var bn = b.normalize(new Vector3f());
            var cn = c.normalize(new Vector3f());
            
            buffer.addVertex(pose, an).setColor(-1);
            buffer.addVertex(pose, bn).setColor(-1);
            
            buffer.addVertex(pose, bn).setColor(-1);
            buffer.addVertex(pose, cn).setColor(-1);
            
            buffer.addVertex(pose, cn).setColor(-1);
            buffer.addVertex(pose, an).setColor(-1);
        }
    }
}
