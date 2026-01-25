package com.xkball.dyson_cube_program.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import com.xkball.dyson_cube_program.client.resources.metadata.TextureMetaDataRequireSize;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(TextureContents.class)
public class MixinTextureContents {
    
    @Inject(method = "load",at = @At("RETURN"))
    private static void onLoad(ResourceManager resourceManager, Identifier textureId, CallbackInfoReturnable<TextureContents> cir, @Local Resource resource, @Local NativeImage image){
        try {
            resource.metadata().getSection(TextureMetaDataRequireSize.TYPE).ifPresent(m -> {
               if(image.getWidth() != m.width() || image.getHeight() != m.height()){
                   throw new IllegalArgumentException("Texture size does not match the required size.");
               }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
