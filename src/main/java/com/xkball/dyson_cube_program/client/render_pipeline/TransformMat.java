package com.xkball.dyson_cube_program.client.render_pipeline;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.dyson_cube_program.api.client.ISTD140Writer;
import org.joml.Matrix4f;

public record TransformMat(Matrix4f transformMatrix) implements ISTD140Writer{
    
    @Override
    public void calculateSize(Std140SizeCalculator calculator) {
        calculator.putMat4f();
    }
    
    @Override
    public void writeToBuffer(Std140Builder builder) {
        builder.putMat4f(transformMatrix);
    }
}
