package com.xkball.dyson_cube_program.mixin;

import com.xkball.dyson_cube_program.client.DCPTextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas {
    
//    @WrapOperation(method = "upload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureAtlas;uploadInitialContents()V"))
//    public void beforeUpload(TextureAtlas instance, Operation<Void> original){
//        var target = instance.getSprite(VanillaUtils.modRL("block/dyson-shell-e14"));
//        if(!target.equals(instance.missingSprite())){
//            for (int i = 1; i < target.contents().byMipLevel.length; i++) {
//                var newMipmap = ClientUtils.readImage(VanillaUtils.modRL("textures/dyson-shell-e14-mipmap"+i+".png"));
//                if(newMipmap == null) continue;
//                target.contents().byMipLevel[i].close();
//                target.contents().byMipLevel[i] = newMipmap;
//            }
//        }
//        original.call(instance);
//    }
    
    @Shadow @Final private Identifier location;
    
    @ModifyVariable(method = "createTexture", at = @At(value = "HEAD"), ordinal = 2, argsOnly = true)
    public int beforeCreate(int mipmapLevel){
        if(this.location.equals(DCPTextureAtlas.DYSON_SHELL_ATLAS_LOCATION)){
            return 10;
        }
        return mipmapLevel;
    }
}
