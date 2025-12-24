package com.xkball.dyson_cube_program.api.client.mixin;

import com.xkball.dyson_cube_program.client.b3d.uniform.SSBOIndexStorage;

import java.util.Map;

public interface IExtendedGLProgram {
    
    Map<String, SSBOIndexStorage> dysonCubeProgram$getSSBOByName();
    
    static IExtendedGLProgram cast(Object obj){
        return (IExtendedGLProgram)obj;
    }
}
