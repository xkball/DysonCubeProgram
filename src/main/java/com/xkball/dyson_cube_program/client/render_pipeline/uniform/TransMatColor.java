package com.xkball.dyson_cube_program.client.render_pipeline.uniform;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.dyson_cube_program.api.client.ISTD140Writer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public record TransMatColor(Matrix4f mat, Vector4f color) implements ISTD140Writer{
    
    @Override
    public void calculateSize(Std140SizeCalculator calculator) {
        calculator.putMat4f().putVec4();
    }
    
    @Override
    public void writeToBuffer(Std140Builder builder) {
        builder.putMat4f(mat).putVec4(color);
    }
}
