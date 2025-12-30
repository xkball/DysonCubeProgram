package com.xkball.dyson_cube_program.graph;

import com.mojang.datafixers.util.Pair;
import com.xkball.dyson_cube_program.api.graph.NodeProvider;
import com.xkball.dyson_cube_program.api.graph.NodeVisitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

public class BFSHandler<T extends NodeProvider<T>> {
    
    private final NodeVisitor<T> visitor;
    private final Set<T> visited = new HashSet<>();
    private final Queue<Pair<T,Integer>> queue = new LinkedList<>();
    
    public BFSHandler(NodeVisitor<T> visitor) {
        this.visitor = visitor;
    }
    
    public void traverse(T head, int maxDepth) {
        traverse(head, NodeProvider::getNeighbors, maxDepth);
    }
    
    public void traverse(T head, Function<T,List<T>> neighborSupplier, int maxDepth){
        queue.add(Pair.of(head,0));
        visited.add(head);
        while (!queue.isEmpty()){
            var node = queue.poll();
            if(maxDepth > 0 && node.getSecond() >= maxDepth) continue;
            var result = visitor.visit(node.getFirst(),node.getSecond());
            node.getFirst().visitCallback(result);
            if(!result) continue;
            for(var neighbor : neighborSupplier.apply(node.getFirst())){
                if(!visited.contains(neighbor)){
                    visited.add(neighbor);
                    queue.add(Pair.of(neighbor,node.getSecond()+1));
                }
            }
        }
    }
}
