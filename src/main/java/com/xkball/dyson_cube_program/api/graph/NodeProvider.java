package com.xkball.dyson_cube_program.api.graph;

import java.util.List;

public interface NodeProvider<T extends NodeProvider<T>> {
    
    List<T> getNeighbors();
    
    default void visitCallback(boolean result){}
}
