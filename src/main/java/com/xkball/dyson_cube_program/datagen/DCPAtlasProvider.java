package com.xkball.dyson_cube_program.datagen;

import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.xorlib.api.annotation.DataGenProvider;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

@NonNullByDefault
@DataGenProvider
public class DCPAtlasProvider extends AtlasProvider {
    
    public DCPAtlasProvider(PackOutput output) {
        super(output);
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.allOf(
//            this.storeAtlas(output, DCPTextureAtlas.DYSON_SHELL_ATLAS, List.of(new DirectoryLister("dyson_shell", "dyson_shell/")))
        );
    }
}
