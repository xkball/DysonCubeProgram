package com.xkball.dyson_cube_program.utils;

import com.mojang.serialization.Codec;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.IntFunction;

@NonNullByDefault
public class CodecUtils {
    
    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    floats -> Util.fixedSize(floats, 3).map(floats1 -> new Vector3f(floats1.get(0), floats1.get(1), floats1.get(2))),
                    vector3f -> List.of(vector3f.x(), vector3f.y(), vector3f.z())
            );
    
    public static final Codec<Vector4f> VECTOR4F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    floats -> Util.fixedSize(floats, 4)
                            .map(floats1 -> new Vector4f(floats1.get(0), floats1.get(1), floats1.get(2), floats1.get(3))),
                    vector4f -> List.of(vector4f.x(), vector4f.y(), vector4f.z(), vector4f.w())
            );
    
    public static final Codec<Quaternionf> QUATERNIONF = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    floats -> Util.fixedSize(floats, 4)
                            .map(floats1 -> new Quaternionf(floats1.get(0), floats1.get(1), floats1.get(2), floats1.get(3)).normalize()),
                    quaternionf -> List.of(quaternionf.x(), quaternionf.y(), quaternionf.z(), quaternionf.w())
            );
    
    public static class StreamCodecs{
        
        public static final StreamCodec<ByteBuf, Vector3f> VECTOR3F = new StreamCodec<>() {
            public Vector3f decode(ByteBuf p_331901_) {
                return FriendlyByteBuf.readVector3f(p_331901_);
            }
            
            public void encode(ByteBuf p_331539_, Vector3f p_455271_) {
                FriendlyByteBuf.writeVector3f(p_331539_, p_455271_);
            }
        };
        
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
        
        public static final StreamCodec<ByteBuf, Quaternionf> QUATERNIONF = new StreamCodec<>() {
            public Quaternionf decode(ByteBuf p_332082_) {
                return FriendlyByteBuf.readQuaternion(p_332082_);
            }
            
            public void encode(ByteBuf p_331172_, Quaternionf p_455381_) {
                FriendlyByteBuf.writeQuaternion(p_331172_, p_455381_);
            }
        };
        
        //列表长度不以var int存取
        public static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(
                final IntFunction<C> factory, final StreamCodec<? super B, V> codec){
            return new StreamCodec<>() {
                
                @Override
                public C decode(B buffer) {
                    int i = buffer.readInt();
                    C c = factory.apply(Mth.clamp(i, 0, 65536));
                    
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
        
        public static <B extends ByteBuf, V> StreamCodec<B, V> nullable(final StreamCodec<? super B, V> codec){
            return new StreamCodec<>() {
                
                @Override
                public void encode(B buffer,@Nullable V value) {
                    if(value == null) buffer.writeBoolean(false);
                    else {
                        buffer.writeBoolean(true);
                        codec.encode(buffer, value);
                    }
                }
                
                @Override
                public V decode(B buffer) {
                    if(!buffer.readBoolean()) return null;
                    return codec.decode(buffer);
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
