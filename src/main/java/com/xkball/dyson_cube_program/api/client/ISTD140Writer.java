package com.xkball.dyson_cube_program.api.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.xkball.dyson_cube_program.utils.ClientUtils;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public interface ISTD140Writer {
    
    void calculateSize(Std140SizeCalculator calculator);
    
    void writeToBuffer(Std140Builder builder);
    
    default GpuBuffer buildStd140Block(){
      return batchBuildStd140Block(List.of(this));
    }
    static <T extends ISTD140Writer> GpuBuffer batchBuildStd140Block(List<T> list){
        var calculator = new Std140SizeCalculator();
        for(var it :  list){
            it.calculateSize(calculator);
        }
        var size = calculator.get();
        GpuBuffer result;
        try(var memStack = MemoryStack.stackPush()){
            var builder = Std140Builder.onStack(memStack,size);
            for(var it : list){
                it.writeToBuffer(builder);
            }
            result = ClientUtils.getGpuDevice().createBuffer(() -> "std140buffer",GpuBuffer.USAGE_UNIFORM,builder.get());
        }
        
        return result;
    }
}
