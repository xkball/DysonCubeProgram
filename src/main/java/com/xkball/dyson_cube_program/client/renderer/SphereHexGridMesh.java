package com.xkball.dyson_cube_program.client.renderer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xkball.dyson_cube_program.client.render_pipeline.DCPRenderPipelines;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import com.xkball.dyson_cube_program.utils.math.Quad;
import com.xkball.dyson_cube_program.utils.math.SphereGeometryUtils;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

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
    public int color = -1;
    
    public SphereHexGridMesh(int depth) {
        this.depth = depth;
    }
    
    public MeshData buildMesh_(PoseStack poseStack) {
        var buffer = ClientUtils.beginWithRenderPipeline(DCPRenderPipelines.DEBUG_LINE);
        var pose = poseStack.last();
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
    
    public void buildMesh(PoseStack poseStack, BufferBuilder buffer, Collection<Quad> quads) {
        var pose = poseStack.last();
        for(int i = 0; i < icosahedronVertices.size(); i+=3) {
//            buffer.addVertex(pose, icosahedronVertices.get(i  )).setColor(-1);
//            buffer.addVertex(pose, icosahedronVertices.get(i+1)).setColor(-1);
//
//            buffer.addVertex(pose, icosahedronVertices.get(i+1)).setColor(-1);
//            buffer.addVertex(pose, icosahedronVertices.get(i+2)).setColor(-1);
//
//            buffer.addVertex(pose, icosahedronVertices.get(i+2)).setColor(-1);
//            buffer.addVertex(pose, icosahedronVertices.get(i  )).setColor(-1);
            appendTriangleWithFilterRecursion(buffer,pose,icosahedronVertices.get(i),icosahedronVertices.get(i+1),icosahedronVertices.get(i+2),depth,
                    (v) -> quads.stream().anyMatch(q -> SphereGeometryUtils.isInside(v,q)));
        }
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
            addTriangle(buffer, pose, a, b, c);
        }
    }
    
    private void addTriangle(BufferBuilder buffer, PoseStack.Pose pose, Vector3f a, Vector3f b, Vector3f c) {
        var an = a.normalize(new Vector3f());
        var bn = b.normalize(new Vector3f());
        var cn = c.normalize(new Vector3f());
        
        buffer.addVertex(pose, an).setColor(color);
        buffer.addVertex(pose, bn).setColor(color);
        
        buffer.addVertex(pose, bn).setColor(color);
        buffer.addVertex(pose, cn).setColor(color);
        
        buffer.addVertex(pose, cn).setColor(color);
        buffer.addVertex(pose, an).setColor(color);
    }
    
    private void appendTriangleWithFilterRecursion(BufferBuilder buffer, PoseStack.Pose pose, Vector3f a, Vector3f b, Vector3f c, int depth, Predicate<Vector3f> func) {
        if(depth > 0) {
            var abHalf = a.add(b.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            var bcHalf = b.add(c.sub(b,new Vector3f()).mul(0.5f),new Vector3f());
            var acHalf = a.add(c.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            var aIn = func.test(a);
            var bIn = func.test(b);
            var cIn = func.test(c);
            var abIn = func.test(abHalf);
            var bcIn = func.test(bcHalf);
            var acIn = func.test(acHalf);
            //if(aIn || abIn || acIn)
                appendTriangleWithFilterRecursion(buffer, pose, a, aIn, abHalf, abIn, acHalf, acIn, depth-1, func);
            //if(abIn || bIn || bcIn)
                appendTriangleWithFilterRecursion(buffer, pose, abHalf, abIn, b, bIn, bcHalf, bcIn, depth-1, func);
            //if(acIn || bcIn || cIn)
                appendTriangleWithFilterRecursion(buffer, pose, acHalf, acIn, bcHalf, bcIn, c, cIn, depth-1, func);
            //if(abIn || bcIn || acIn)
                appendTriangleWithFilterRecursion(buffer, pose, abHalf, abIn, bcHalf, bcIn, acHalf, acIn, depth-1, func);
        }
        else {
            addTriangle(buffer, pose, a, b, c);
        }
    }
    
    private void appendTriangleWithFilterRecursion(BufferBuilder buffer, PoseStack.Pose pose, Vector3f a, boolean aIn, Vector3f b, boolean bIn, Vector3f c, boolean cIn, int depth, Predicate<Vector3f> func) {
        if(depth > 0) {
            var abHalf = a.add(b.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            var bcHalf = b.add(c.sub(b,new Vector3f()).mul(0.5f),new Vector3f());
            var acHalf = a.add(c.sub(a,new Vector3f()).mul(0.5f),new Vector3f());
            var abIn = false;
            var bcIn = false;
            var acIn = false;
            if(aIn && bIn && cIn){
                abIn = true;
                bcIn = true;
                acIn = true;
            }
            else{
                abIn = func.test(abHalf);
                bcIn = func.test(bcHalf);
                acIn = func.test(acHalf);
            }
            //if(aIn || abIn || acIn)
                appendTriangleWithFilterRecursion(buffer, pose, a, aIn, abHalf, abIn, acHalf, acIn, depth-1, func);
            //if(abIn || bIn || bcIn)
                appendTriangleWithFilterRecursion(buffer, pose, abHalf, abIn, b, bIn, bcHalf, bcIn, depth-1, func);
            //if(acIn || bcIn || cIn)
                appendTriangleWithFilterRecursion(buffer, pose, acHalf, acIn, bcHalf, bcIn, c, cIn, depth-1, func);
            //if(abIn || bcIn || acIn)
                appendTriangleWithFilterRecursion(buffer, pose, abHalf, abIn, bcHalf, bcIn, acHalf, acIn, depth-1, func);
        }
        else {
            if(aIn || bIn || cIn) addTriangle(buffer, pose, a, b, c);
        }
    }
}
