package com.xkball.dyson_cube_program.client.resources.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public record TextureMetaDataRequireSize(int width, int height) {
    
    public static final Codec<TextureMetaDataRequireSize> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("width").forGetter(TextureMetaDataRequireSize::width),
            Codec.INT.fieldOf("height").forGetter(TextureMetaDataRequireSize::height)
    ).apply(ins, TextureMetaDataRequireSize::new));
    
    public static final MetadataSectionType<TextureMetaDataRequireSize> TYPE = new MetadataSectionType<>("require_size", CODEC);
}
