package com.xkball.dyson_cube_program.utils;

import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector4f;

@NonNullByDefault
public class CodecUtils {
    
    public static class StreamCodecs{
        
        public static final StreamCodec<ByteBuf, Vector4f> VECTOR4F = new StreamCodec<>() {
            public Vector4f decode(ByteBuf buffer) {
                return new Vector4f(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
            
            public void encode(ByteBuf buffer, Vector4f vec) {
                buffer.writeFloat(vec.x);
                buffer.writeFloat(vec.y);
                buffer.writeFloat(vec.z);
                buffer.writeFloat(vec.w);
            }
        };
        
        public static final StreamCodec<ByteBuf, Long> LONG = new StreamCodec<>() {
            
            @Override
            public void encode(ByteBuf buffer, Long value) {
                buffer.writeLong(value);
            }
            
            @Override
            public Long decode(ByteBuf buffer) {
                return buffer.readLong();
            }
        };
        
    }
    
}
