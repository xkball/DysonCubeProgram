package com.xkball.dyson_cube_program.utils.math;

import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

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
                var node = new Node(x,y);
                node.type = typeY;
                if(x == 0){
                    node.spherePos = MathConstants.ICOSAHEDRON0;
                }
                else {
                    node.spherePos = SphereGeometryUtils.slerp(xr,yr,(float) y/x);
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
    
    public static class Node{
        public int x;
        public int y;
        public Vector3f spherePos;
        public @Nullable Node n000;
        public @Nullable Node n060;
        public @Nullable Node n120;
        public @Nullable Node n180;
        public @Nullable Node n240;
        public @Nullable Node n300;
        public NodeType type;
        
        public Node(int x, int y){
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
        
        public List<Node> neighbors(NodeType type){
            return neighbors().stream().filter(n -> n.type == type).toList();
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
