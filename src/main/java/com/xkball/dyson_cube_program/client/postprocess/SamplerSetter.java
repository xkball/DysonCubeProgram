package com.xkball.dyson_cube_program.client.postprocess;

import com.mojang.blaze3d.systems.RenderPass;

@FunctionalInterface
public interface SamplerSetter {
    void setSampler(RenderPass shader);
}
