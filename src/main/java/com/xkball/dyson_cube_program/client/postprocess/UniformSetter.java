package com.xkball.dyson_cube_program.client.postprocess;

import net.minecraft.client.renderer.ShaderInstance;

@FunctionalInterface
public interface UniformSetter {
    void setUniforms(ShaderInstance shader);
}
