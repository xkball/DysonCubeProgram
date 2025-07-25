package com.xkball.dyson_cube_program.client.render_pipeline.uniform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.dyson_cube_program.utils.func.FloatSupplier;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class AutoUpdateUBO implements AutoCloseable {
    private final GpuBuffer buffer;
    private final
    
    @Override
    public void close() throws Exception {
        this.buffer.close();
    }
    
    
    public static class UBOBuilder {
        private final Std140SizeCalculator calculator = new Std140SizeCalculator();
        private final List<Consumer<Std140Builder>> builders = new ArrayList<>();
        
        public UBOBuilder() {}
        
        public UBOBuilder putFloat(String name, FloatSupplier supplier) {
            calculator.putFloat();
            builders.add(b -> b.putFloat(supplier.getAsFloat()));
            return this;
        }
        
        public UBOBuilder putInt(String name, IntSupplier supplier) {
            calculator.putInt();
            builders.add(b -> b.putInt(supplier.getAsInt()));
            return this;
        }
        
        public UBOBuilder putVec2(String name, Supplier<Vector2fc> supplier) {
            calculator.putVec2();
            builders.add(b -> b.putVec2(supplier.get()));
            return this;
        }
        
        public UBOBuilder putVec3(String name, Supplier<Vector3fc> supplier) {
            calculator.putVec3();
            builders.add(b -> b.putVec3(supplier.get()));
            return this;
        }
        
        public UBOBuilder putIVec3(String name, Supplier<Vector3ic> supplier) {
            calculator.putIVec3();
            builders.add(b -> b.putIVec3(supplier.get()));
            return this;
        }
        
        public UBOBuilder putVec4(String name, Supplier<Vector4fc> supplier) {
            calculator.putVec4();
            builders.add(b -> b.putVec4(supplier.get()));
            return this;
        }
        
        public UBOBuilder putIVec4(String name, Supplier<Vector4ic> supplier) {
            calculator.putIVec4();
            builders.add(b -> b.putIVec4(supplier.get()));
            return this;
        }
        
        public UBOBuilder putMat4f(String name, Supplier<Matrix4fc> supplier) {
            calculator.putMat4f();
            builders.add(b -> b.putMat4f(supplier.get()));
            return this;
        }
        
        public AutoUpdateUBO build(){
        
        }
    }
}
