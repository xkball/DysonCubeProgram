package com.xkball.dyson_cube_program.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;

public class SamplerCacheCache {
    public static final GpuSampler NEAREST_REPEAT = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
    public static final GpuSampler NEAREST_CLAMP = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
}
