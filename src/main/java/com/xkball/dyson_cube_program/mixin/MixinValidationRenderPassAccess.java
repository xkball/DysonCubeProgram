package com.xkball.dyson_cube_program.mixin;

import com.mojang.blaze3d.systems.RenderPass;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ValidationRenderPass.class)
public interface MixinValidationRenderPassAccess {
    
    @Accessor
    RenderPass getRealRenderPass();
}
