package com.xkball.dyson_cube_program.client.render_pipeline.uniform;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.api.client.ICloseOnExit;
import com.xkball.dyson_cube_program.api.client.IEndFrameListener;
import com.xkball.dyson_cube_program.api.client.IUpdatable;
import com.xkball.dyson_cube_program.api.client.UpdateWhen;
import com.xkball.dyson_cube_program.client.ClientRenderObjects;
import com.xkball.dyson_cube_program.utils.func.FloatSupplier;
import net.minecraft.client.renderer.DynamicUniformStorage;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;
import org.joml.Vector4fc;
import org.joml.Vector4ic;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

//使用MappableRingBuffer仅可用于单帧最多更新一次的UBO
@NonNullByDefault
public class UpdatableUBO implements ICloseOnExit<UpdatableUBO>, IEndFrameListener, IUpdatable  {
    
    private final String name;
    private final int size;
    private final UpdateWhen updateWhen;
    private final DynamicUniformStorage<BuildUniformBlock> buffer;
    private final BuildUniformBlock updateFunc;
    private GpuBufferSlice lastSlice;
    
    public UpdatableUBO(String name, int size, Consumer<Std140Builder> updateFunc, boolean closeOnExit, UpdateWhen updateWhen) {
        this.name = name;
        this.size = size;
        this.updateFunc = new BuildUniformBlock(updateFunc);
        this.updateWhen = updateWhen;
        this.buffer = new DynamicUniformStorage<>(name,size,2);
        ClientRenderObjects.addEndFrameListener(this);
        if (closeOnExit) {
            ClientRenderObjects.addCloseOnExit(this);
        }
        if(updateWhen == UpdateWhen.EveryFrame){
            ClientRenderObjects.addEveryFrameListener(this);
        }
    }
    
    @Override
    public void endFrame() {
        this.buffer.endFrame();
    }
    
    @Override
    public void update(){
        this.lastSlice = this.buffer.writeUniform(updateFunc);
    }
    
    public void updateUnsafe(Consumer<Std140Builder> updateFunc){
        this.lastSlice = this.buffer.writeUniform(new BuildUniformBlock(updateFunc));
    }
    
    @Override
    public void close() {
        this.buffer.close();
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public UpdateWhen getUpdateWhen() {
        return updateWhen;
    }
    
    public GpuBufferSlice getBuffer(){
        return lastSlice;
    }
    
    public int getSize() {
        return size;
    }
    
    public record BuildUniformBlock(Consumer<Std140Builder> updateFunc) implements DynamicUniformStorage.DynamicUniform{
        @Override
        public void write(ByteBuffer buffer) {
            var builder = Std140Builder.intoBuffer(buffer);
            this.updateFunc.accept(builder);
        }
    }
    
    public static class UBOBuilder {
        
        private final String name;
        private final Std140SizeCalculator calculator = new Std140SizeCalculator();
        private final List<Consumer<Std140Builder>> builders = new ArrayList<>();
        private boolean closeOnExit = false;
        private UpdateWhen updateWhen = UpdateWhen.Manual;
        
        public UBOBuilder(String name) {
            this.name = name;
        }
        
        public UBOBuilder closeOnExit(){
            this.closeOnExit = true;
            return this;
        }
        
        public UBOBuilder updateWhen(UpdateWhen updateWhen){
            this.updateWhen = updateWhen;
            return this;
        }
        
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
        
        public UBOBuilder putVec2(String name, IntSupplier xSupplier, IntSupplier ySupplier) {
            calculator.putVec2();
            builders.add(b -> b.putVec2(xSupplier.getAsInt(), ySupplier.getAsInt()));
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
        
        public UpdatableUBO build(){
            Consumer<Std140Builder> updateFunc = b -> {
                for(var builder : builders) {
                    builder.accept(b);
                }
            };
            
            return new UpdatableUBO(this.name,this.calculator.get(), updateFunc, this.closeOnExit, this.updateWhen);
        }
    }
}
