package com.xkball.dyson_cube_program.api.client.mixin;

import com.xkball.dyson_cube_program.client.render_pipeline.uniform.SSBOIndexStorage;

import java.util.Map;

public interface IExtendedGLProgram {
    
    Map<String, SSBOIndexStorage> dysonCubeProgram$getSSBOByName();
    
    static IExtendedGLProgram cast(Object obj){
        return (IExtendedGLProgram)obj;
    }
}
