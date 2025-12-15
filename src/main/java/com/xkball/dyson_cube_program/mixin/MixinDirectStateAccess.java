package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.ByteBuffer;

@Mixin(DirectStateAccess.class)
public interface MixinDirectStateAccess {
    
    @Invoker
    ByteBuffer invokeMapBufferRange(int buffer, long offset, long length, int access, int usage);
}
