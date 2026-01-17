package com.xkball.dyson_cube_program.client.resources.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;

import java.util.Map;

public record TextureMetadataUsingMipmap(int mipmapLevels, Map<String, Identifier> manualMipmaps, boolean useGLGenerateMipmap) {
    
    public static final Codec<TextureMetadataUsingMipmap> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("mipmapLevels").forGetter(TextureMetadataUsingMipmap::mipmapLevels),
            Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("manualMipmaps").forGetter(TextureMetadataUsingMipmap::manualMipmaps),
            Codec.BOOL.fieldOf("useGLGenerateMipmap").forGetter(TextureMetadataUsingMipmap::useGLGenerateMipmap)
    ).apply(ins, TextureMetadataUsingMipmap::new));
    
    public static final MetadataSectionType<TextureMetadataUsingMipmap> TYPE = new MetadataSectionType<>("using_mipmap", CODEC);
    
    
}
