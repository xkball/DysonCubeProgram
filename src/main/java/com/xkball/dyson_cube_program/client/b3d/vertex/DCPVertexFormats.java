package com.xkball.dyson_cube_program.client.b3d.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class DCPVertexFormats {
    
    public static final VertexFormat POSITION_DUAL_TEX_COLOR = VertexFormat.builder()
            .add("Position", VertexFormatElement.POSITION)
            .add("Color", VertexFormatElement.COLOR)
            .add("UV0", VertexFormatElement.UV0)
            .build();
    
}
