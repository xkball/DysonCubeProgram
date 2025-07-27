package com.xkball.dyson_cube_program.client.render_pipeline.mesh;

import com.mojang.blaze3d.buffers.GpuBufferSlice;

import javax.annotation.Nullable;

public record InstanceInfo(int instanceCount,@Nullable String ssboName, @Nullable GpuBufferSlice ssboBuffer) {
    
    public static final InstanceInfo EMPTY = new InstanceInfo(1, null, null);
    
}
