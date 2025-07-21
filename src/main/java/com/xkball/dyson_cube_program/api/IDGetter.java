package com.xkball.dyson_cube_program.api;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import java.util.Collection;

public interface IDGetter {
    
    int getID();
    
    static <T extends IDGetter> IntObjectMap<T> toMap(Collection<T> idGetters) {
        var result = new IntObjectHashMap<T>();
        for(var idGetter : idGetters) {
            if(idGetter == null) continue;
            result.put(idGetter.getID(), idGetter);
        }
        return result;
    }
}
