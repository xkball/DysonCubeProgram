package com.xkball.dyson_cube_program.utils.math;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.graph.NodeProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.Mth.lerp;

@NonNullByDefault
public class HexGrid {
    
    public static final HexGrid LEVEL7 = withRecursionDepth(7);
    public static final HexGrid LEVEL8 = withRecursionDepth(8);
    public final ArrayList<ArrayList<Node>> map;
    public final int layers;
    
    public static HexGrid withRecursionDepth(int depth){
        return new HexGrid((int) (Math.pow(2,depth)+1));
    }
    
    public HexGrid(int layers) {
        this.layers = layers;
        this.map = new ArrayList<>(layers);
        this.rebuildMap();
    }
    
    public @Nullable Node getNode(Vector2i pos){
        return getNode(pos.x(), pos.y());
    }
    
    public @Nullable Node getNode(int x, int y){
        if(x > layers || y < 0 || y > x) return null;
        return map.get(x).get(y);
    }
    
    public List<Node> getNodes(){
        return map.stream().flatMap(List::stream).toList();
    }
    
    public void rebuildMap(){
        this.map.clear();
        for(int i = 0; i <= layers; i++){
            this.map.add(new ArrayList<>());
        }
        var typeX = NodeType.CENTER;
        for(int x = 0; x <= layers; x++){
            var xr = SphereGeometryUtils.slerp(MathConstants.ICOSAHEDRON0,MathConstants.ICOSAHEDRON1,(float) x/layers);
            var yr = SphereGeometryUtils.slerp(MathConstants.ICOSAHEDRON0,MathConstants.ICOSAHEDRON2,(float) x/layers);
            @SuppressWarnings("SuspiciousNameCombination")
            var typeY = typeX;
            for(int y = 0; y <= x; y++){
                var node = new Node(this, x, y);
                node.type = typeY;
                if(x == 0){
                    node.spherePos = MathConstants.ICOSAHEDRON0;
                }
                else {
                    node.spherePos = SphereGeometryUtils.slerp(xr,yr,(float) y/x).normalize();
                }
                map.get(x).add(node);
                typeY = typeY.getNext();
            }
            typeX = typeX.getNext();
        }
        for(int x = 0; x <= layers; x++){
            for(int y = 0; y <= x; y++){
                var node = getNode(x,y);
                assert node != null;
                var neighbors = node.neighborsPos().stream().map(this::getNode).toList();
                node.n000 = neighbors.get(0);
                node.n060 = neighbors.get(1);
                node.n120 = neighbors.get(2);
                node.n180 = neighbors.get(3);
                node.n240 = neighbors.get(4);
                node.n300 = neighbors.get(5);
            }
            
        }
    }
    
    public static class Node implements NodeProvider<Node> {
        public final HexGrid grid;
        public int x;
        public int y;
        public Vector3f spherePos;
        public Vector3f contextPos = new Vector3f();
        public boolean contextState = false;
        public @Nullable Node n000;
        public @Nullable Node n060;
        public @Nullable Node n120;
        public @Nullable Node n180;
        public @Nullable Node n240;
        public @Nullable Node n300;
        public NodeType type;
        
        public Node(HexGrid grid, int x, int y){
            this.grid = grid;
            this.x = x;
            this.y = y;
        }
        
        public Vector2f pos(){
            return new Vector2f(x,y);
        }
        
        public List<Vector2i> neighborsPos(){
            return List.of(
                    new Vector2i(x+1,y),
                    new Vector2i(x+1,y+1),
                    new Vector2i(x,y+1),
                    new Vector2i(x-1,y),
                    new Vector2i(x-1,y-1),
                    new Vector2i(x,y-1)
            );
        }
        
        public List<Node> neighbors(){
            var result = new ArrayList<Node>();
            if(n000 != null) result.add(n000);
            if(n060 != null) result.add(n060);
            if(n120 != null) result.add(n120);
            if(n180 != null) result.add(n180);
            if(n240 != null) result.add(n240);
            if(n300 != null) result.add(n300);
            return result;
        }
        
        public void makeQuads(PoseStack.Pose pose, BufferBuilder bufferBuilder, int color, TextureAtlasSprite front, List<Quad> quads, List<Pair<Vector3f,Vector3f>> sides, boolean fullCheck){
            if(type != NodeType.CENTER) return;
            if(!fullCheck){
                if(n000 != null && n060 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n000.contextPos,n060.contextPos,color,1f/3f,1f/3f,2f/3f,1f/3f,2f/3f,0,front);
                if(n060 != null && n120 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n060.contextPos,n120.contextPos,color,1f/3f,1f/3f,2f/3f,0,1f/3f,0,front);
                if(n120 != null && n180 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n120.contextPos,n180.contextPos,color,1f/3f,1f/3f,1f/3f,0,0,1f/3f,front);
                if(n180 != null && n240 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n180.contextPos,n240.contextPos,color,1f/3f,1f/3f,0,1f/3f,0,2f/3f,front);
                if(n240 != null && n300 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n240.contextPos,n300.contextPos,color,1f/3f,1f/3f,0,2f/3f,1f/3f,2f/3f,front);
                if(n300 != null && n000 != null) this.buildTriangle(pose,bufferBuilder,this.contextPos,n300.contextPos,n000.contextPos,color,1f/3f,1f/3f,1f/3f,2f/3f,2f/3f,1f/3f,front);
            }
            else{
                for(var n : neighbors()){
                    n.contextState = SphereGeometryUtils.insideQuads(n.contextPos, quads);
                }
                if(n000 != null && n060 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n000,n060,color,1f/3f,1f/3f,2f/3f,1f/3f,2f/3f,0,front);
                if(n060 != null && n120 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n060,n120,color,1f/3f,1f/3f,2f/3f,0,1f/3f,0,front);
                if(n120 != null && n180 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n120,n180,color,1f/3f,1f/3f,1f/3f,0,0,1f/3f,front);
                if(n180 != null && n240 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n180,n240,color,1f/3f,1f/3f,0,1f/3f,0,2f/3f,front);
                if(n240 != null && n300 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n240,n300,color,1f/3f,1f/3f,0,2f/3f,1f/3f,2f/3f,front);
                if(n300 != null && n000 != null) makeEdgeQuads(pose,bufferBuilder,sides,this,n300,n000,color,1f/3f,1f/3f,1f/3f,2f/3f,2f/3f,1f/3f,front);
            }
        }
        
        public void makeEdgeQuads(PoseStack.Pose pose, BufferBuilder bufferBuilder, List<Pair<Vector3f,Vector3f>> sides, Node a, Node b, Node c,
                                  int color, float u0, float v0, float u1, float v1, float u2, float v2, TextureAtlasSprite front){
            var outsideCount = 0;
            if(!a.contextState) outsideCount++;
            if(!b.contextState) outsideCount++;
            if(!c.contextState) outsideCount++;
            if(outsideCount == 0) buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
            if(outsideCount == 1){
                if(!a.contextState){
                    var abn = findIntersection(sides,a,b);
                    var acn = findIntersection(sides,a,c);
                    if(abn != null && acn != null) {
                        var dabn = calculateDelta(abn,a,b);
                        var dacn = calculateDelta(acn,a,c);
                        buildTriangle(pose,bufferBuilder,abn,b.contextPos,c.contextPos,color, lerp(dabn,u0,u1), lerp(dabn,v0,v1),u1,v1,u2,v2,front);
                        buildTriangle(pose,bufferBuilder,abn,c.contextPos,acn,color, lerp(dabn,u0,u1), lerp(dabn,v0,v1),u2,v2, lerp(dacn,u0,u2), lerp(dacn,v0,v2),front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
                else if(!b.contextState){
                    var bcn = findIntersection(sides,b,c);
                    var ban = findIntersection(sides,b,a);
                    if(bcn != null && ban != null) {
                        var dban = calculateDelta(ban,b,a);
                        var dbcn = calculateDelta(bcn,b,c);
                        buildTriangle(pose,bufferBuilder,a.contextPos,ban,c.contextPos,color,u0,v0, lerp(dban,u1,u0), lerp(dban,v1,v0),u2,v2,front);
                        buildTriangle(pose,bufferBuilder,ban,bcn,c.contextPos,color, lerp(dban,u1,u0), lerp(dban,v1,v0), lerp(dbcn,u1,u2), lerp(dbcn,v1,v2),u2,v2,front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
                else {
                    var can = findIntersection(sides,c,a);
                    var cbn = findIntersection(sides,c,b);
                    if(can != null && cbn != null) {
                        var dcan = calculateDelta(can,c,a);
                        var dcbn = calculateDelta(cbn,c,b);
                        buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,cbn,color,u0,v0,u1,v1,lerp(dcan,u2,u1),lerp(dcan,v2,v1),front);
                        buildTriangle(pose,bufferBuilder,a.contextPos,cbn,can,color,u0,v0,lerp(dcbn,u2,u1),lerp(dcbn,v2,v1),lerp(dcan,u2,u0),lerp(dcan,v2,v0),front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
            }
            if(outsideCount == 2){
                if(a.contextState){
                    var bn = findIntersection(sides,a,b);
                    var cn = findIntersection(sides,a,c);
                    if(bn != null && cn != null){
                        var dbn = calculateDelta(bn,a,b);
                        var dcn = calculateDelta(cn,a,c);
                        buildTriangle(pose,bufferBuilder,a.contextPos,bn,cn,color,u0,v0, lerp(dbn,u0,u1), lerp(dbn,v0,v1), lerp(dcn,u0,u2), lerp(dcn,v0,v2),front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
                else if(b.contextState){
                    var an = findIntersection(sides,b,a);
                    var cn = findIntersection(sides,b,c);
                    if(an != null && cn != null) {
                        var dan = calculateDelta(an,b,a);
                        var dcn = calculateDelta(cn,b,c);
                        buildTriangle(pose,bufferBuilder,an,b.contextPos,cn,color, lerp(dan,u1,u0), lerp(dan,v1,v0),u1,u1, lerp(dcn,u1,u2), lerp(dcn,v1,v2),front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
                else {
                    var an = findIntersection(sides,c,a);
                    var bn = findIntersection(sides,c,b);
                    if(an != null && bn != null) {
                        var dan = calculateDelta(an,c,a);
                        var dbn = calculateDelta(bn,c,b);
                        buildTriangle(pose,bufferBuilder,an,bn,c.contextPos,color, lerp(dan,u2,u0), lerp(dan,v2,v0), lerp(dbn,u2,u1), lerp(dbn,v2,v1),u2,v2,front);
                    }
                    else buildTriangle(pose,bufferBuilder,a.contextPos,b.contextPos,c.contextPos,color,u0,v0,u1,v1,u2,v2,front);
                }
            }
        }
        
        private void buildTriangle(PoseStack.Pose pose, BufferBuilder bufferBuilder, Vector3f a, Vector3f b, Vector3f c,
                                   int color, float u0, float v0, float u1, float v1, float u2, float v2, TextureAtlasSprite front){
            bufferBuilder.addVertex(pose,a).setUv(front.getU(u0),front.getV(v0)).setColor(color);
            bufferBuilder.addVertex(pose,b).setUv(front.getU(u1),front.getV(v1)).setColor(color);
            bufferBuilder.addVertex(pose,c).setUv(front.getU(u2),front.getV(v2)).setColor(color);
        }
        
        private @Nullable Vector3f findIntersection(List<Pair<Vector3f,Vector3f>> sides, Node l1a, Node l1b){
            for(var side : sides){
                var intersection = SphereGeometryUtils.intersection(l1a.contextPos,l1b.contextPos,side.getFirst(),side.getSecond());
                if(intersection != null) return intersection;
            }
            return null;
        }
        
        private float calculateDelta(Vector3f d, Node from, Node to){
            return d.sub(from.contextPos,new Vector3f()).length() / to.contextPos.sub(from.contextPos,new Vector3f()).length();
        }
        
        public void transform(Matrix4f mat){
            if(type != NodeType.CENTER) return;
            mat.transformPosition(this.spherePos,this.contextPos);
            if(n000 != null) {mat.transformPosition(n000.spherePos,n000.contextPos); this.contextPos.add(n000.contextPos.sub(this.contextPos,n000.contextPos).mul(0.98f),n000.contextPos);}
            if(n060 != null) {mat.transformPosition(n060.spherePos,n060.contextPos); this.contextPos.add(n060.contextPos.sub(this.contextPos,n060.contextPos).mul(0.98f),n060.contextPos);}
            if(n120 != null) {mat.transformPosition(n120.spherePos,n120.contextPos); this.contextPos.add(n120.contextPos.sub(this.contextPos,n120.contextPos).mul(0.98f),n120.contextPos);}
            if(n180 != null) {mat.transformPosition(n180.spherePos,n180.contextPos); this.contextPos.add(n180.contextPos.sub(this.contextPos,n180.contextPos).mul(0.98f),n180.contextPos);}
            if(n240 != null) {mat.transformPosition(n240.spherePos,n240.contextPos); this.contextPos.add(n240.contextPos.sub(this.contextPos,n240.contextPos).mul(0.98f),n240.contextPos);}
            if(n300 != null) {mat.transformPosition(n300.spherePos,n300.contextPos); this.contextPos.add(n300.contextPos.sub(this.contextPos,n300.contextPos).mul(0.98f),n300.contextPos);}
        }
        
        public List<Node> neighbors(NodeType type){
            return neighbors().stream().filter(n -> n.type == type).toList();
        }
        
        public List<Node> nearCenters(){
            if(type != NodeType.CENTER) return neighbors(NodeType.CENTER);
            var result = new ArrayList<Node>();
            var n1 = grid.getNode(x+1,y-1);
            var n2 = grid.getNode(x+2,y+1);
            var n3 = grid.getNode(x+1,y+2);
            var n4 = grid.getNode(x-1,y+1);
            var n5 = grid.getNode(x-2,y-1);
            var n6 = grid.getNode(x-1,y-2);
            if(n1 != null) result.add(n1);
            if(n2 != null) result.add(n2);
            if(n3 != null) result.add(n3);
            if(n4 != null) result.add(n4);
            if(n5 != null) result.add(n5);
            if(n6 != null) result.add(n6);
            return result;
        }
        
        public boolean contextInsideQuad(){
            if(type != NodeType.CENTER) return false;
            var result = this.contextState;
            for(var n : nearCenters()){
                result &= n.contextState;
            }
            return result;
        }
        
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node node)) return false;
            return x == node.x && y == node.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
        
        @Override
        public List<Node> getNeighbors() {
            return neighbors();
        }
        
        @Override
        public void visitCallback(boolean result) {
            this.contextState = result;
        }
        
        @Override
        public String toString() {
            return "Node{" +
                    "x=" + x +
                    ", y=" + y +
                    ", spherePos=" + spherePos +
                    '}';
        }
    }
    
    public enum NodeType{
        CENTER(1),
        R00(2),
        R60(0);
        
        public static final NodeType[] VALUES = values();
        private final int nextIndex;
        
        NodeType(int nextIndex){
            this.nextIndex = nextIndex;
        }
        
        public NodeType getNext(){
            return VALUES[nextIndex];
        }
        
    }
}
