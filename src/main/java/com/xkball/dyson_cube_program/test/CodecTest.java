package com.xkball.dyson_cube_program.test;

import com.mojang.serialization.JsonOps;
import com.xkball.dyson_cube_program.client.resources.metadata.TextureMetadataUsingMipmap;
import com.xkball.dyson_cube_program.utils.VanillaUtils;

import java.util.Map;

public class CodecTest {
    public static void main(String[] args) {
        var jsonOps = JsonOps.INSTANCE;
        
        TextureMetadataUsingMipmap.CODEC.encodeStart(jsonOps, new TextureMetadataUsingMipmap(5, Map.of("1", VanillaUtils.modRL("d")),true)).result().ifPresent(System.out::println);
    }
    
}
