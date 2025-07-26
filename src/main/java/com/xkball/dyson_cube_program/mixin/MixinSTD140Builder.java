package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.buffers.Std140Builder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Std140Builder.class)
public class MixinSTD140Builder {
    
    @ModifyConstant(method = "putVec3(Lorg/joml/Vector3fc;)Lcom/mojang/blaze3d/buffers/Std140Builder;", constant = @Constant(intValue = 16, ordinal = 1))
    public int fixVec3Align1(int i){
        return 12;
    }
    
    @ModifyConstant(method = "putVec3(FFF)Lcom/mojang/blaze3d/buffers/Std140Builder;", constant = @Constant(intValue = 4))
    public int fixVec3Align2(int i){
        return 0;
    }
    
}
