package com.xkball.dyson_cube_program.utils;

import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

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
        
        public static final StreamCodec<ByteBuf, List<Vector4f>> VECTOR4F_LIST = collection(ArrayList::new, CodecUtils.StreamCodecs.VECTOR4F);
        
        public static final StreamCodec<ByteBuf, List<Integer>> INT_LIST = collection(ArrayList::new, ByteBufCodecs.INT);
        
        public static final StreamCodec<ByteBuf, List<Integer>> NULLABLE_INT_LIST = nullableList(ArrayList::new, ByteBufCodecs.INT);
        
        //列表长度不以var int存取
        public static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(
                final IntFunction<C> factory, final StreamCodec<? super B, V> codec){
            return new StreamCodec<>() {
                
                @Override
                public C decode(B buffer) {
                    int i = buffer.readInt();
                    C c = factory.apply(Math.min(i, 65536));
                    
                    for (int j = 0; j < i; j++) {
                        c.add(codec.decode(buffer));
                    }
                    
                    return c;
                }
                
                @Override
                public void encode(B buffer, C value) {
                    buffer.writeInt(value.size());
                    
                    for (V v : value) {
                        codec.encode(buffer, v);
                    }
                }
            };
        }
        
        public static <B extends ByteBuf, V, C extends List<V>> StreamCodec<B, C> nullableList(
                final IntFunction<C> factory, final StreamCodec<? super B, V> codec){
            return new StreamCodec<>() {
                
                @Override
                public C decode(B buffer) {
                    int i = buffer.readInt();
                    C c = factory.apply(Math.min(i, 65536));
                    
                    for (int j = 0; j < i; j++) {
                        if(buffer.readBoolean()){
                            c.add(codec.decode(buffer));
                        }
                        else {
                            c.add(null);
                        }
                    }
                    
                    return c;
                }
                
                @Override
                public void encode(B buffer, C value) {
                    buffer.writeInt(value.size());
                    
                    for (V v : value) {
                        if (v != null) {
                            buffer.writeBoolean(true);
                            codec.encode(buffer, v);
                        } else {
                            buffer.writeBoolean(false);
                        }
                    }
                }
            };
        }
        public static <B extends ByteBuf, V, C extends List<V>> StreamCodec<B, C> fixLengthList(
                final IntFunction<C> factory, final StreamCodec<? super B, V> codec, final int length){
            return new StreamCodec<>() {
                
                @Override
                public void encode(B buffer, C value) {
                    for (int i = 0; i < length; i++){
                        var obj = value.get(i);
                        codec.encode(buffer, obj);
                    }
                }
                
                @Override
                public C decode(B buffer) {
                    C c = factory.apply(Math.min(length, 65536));
                    for (int i = 0; i < length; i++) {
                        c.add(codec.decode(buffer));
                    }
                    return c;
                }
            };
        }
        
        public static <B extends ByteBuf, V, C extends List<V>> StreamCodec<B, C> fixLengthNullableList(
                final IntFunction<C> factory, final StreamCodec<? super B, V> codec, final int length){
            return new StreamCodec<>() {
                
                @Override
                public void encode(B buffer, C value) {
                    for (int i = 0; i < length; i++){
                        var obj = value.get(i);
                        if(obj != null){
                            buffer.writeInt(i+1);
                            codec.encode(buffer, obj);
                        }
                        else {
                            buffer.writeInt(0);
                        }
                    }
                }
                
                @Override
                public C decode(B buffer) {
                    C c = factory.apply(Math.min(length, 65536));
                    
                    for (int i = 0; i < length; i++) {
                        if(buffer.readInt() != 0){
                            c.add(codec.decode(buffer));
                        }
                        else {
                            c.add(null);
                        }
                    }
                    return c;
                }
            };
        }
    }
    
}
