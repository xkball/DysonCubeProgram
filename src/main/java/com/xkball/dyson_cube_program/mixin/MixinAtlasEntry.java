package com.xkball.dyson_cube_program.mixin;

import com.xkball.dyson_cube_program.client.DCPTextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.AtlasManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AtlasManager.AtlasEntry.class)
public class MixinAtlasEntry {
    
    @Shadow @Final private TextureAtlas atlas;
    
    @ModifyVariable(method = "scheduleLoad", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
    public int beforeScheduleLoad(int mipLevel){
        if(this.atlas.location().equals(DCPTextureAtlas.DYSON_SHELL_ATLAS_LOCATION)){
            return 10;
        }
        return mipLevel;
    }

}
