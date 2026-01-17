package com.xkball.dyson_cube_program.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;

public class SamplerCacheCache {
    public static final GpuSampler NEAREST_REPEAT = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
    public static final GpuSampler NEAREST_CLAMP = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
    public static final GpuSampler LINEAR_CLAMP = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
    public static final GpuSampler NEAREST_REPEAT_MIPMAP = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST, true);
    public static final GpuSampler NEAREST_CLAMP_MIPMAP = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);
}
