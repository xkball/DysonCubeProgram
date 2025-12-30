package com.xkball.dyson_cube_program.api.graph;

@FunctionalInterface
public interface NodeVisitor<T extends NodeProvider<T>> {
    
    boolean visit(T node, int depth);
}
