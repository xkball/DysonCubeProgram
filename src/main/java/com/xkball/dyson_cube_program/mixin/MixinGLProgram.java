package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.GlProgram;
import com.xkball.dyson_cube_program.api.client.mixin.IExtendedGLProgram;
import com.xkball.dyson_cube_program.client.b3d.uniform.SSBOIndexStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(GlProgram.class)
public class MixinGLProgram implements IExtendedGLProgram {
    
    @Unique
    public final Map<String, SSBOIndexStorage> dysonCubeProgram$SSBOByName = new LinkedHashMap<>();
    
    @Override
    public Map<String, SSBOIndexStorage> dysonCubeProgram$getSSBOByName() {
        return dysonCubeProgram$SSBOByName;
    }
    
}
