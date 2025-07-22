package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.IDGetter;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public record DysonOrbitData (
    int id,
    float radius,
    Quaternionf rotation,
    boolean enable
) implements IDGetter{
    
    public static final Codec<DysonOrbitData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("id").forGetter(DysonOrbitData::id),
            Codec.FLOAT.fieldOf("radius").forGetter(DysonOrbitData::radius),
            ExtraCodecs.QUATERNIONF.fieldOf("rotation").forGetter(DysonOrbitData::rotation),
            Codec.BOOL.fieldOf("enable").forGetter(DysonOrbitData::enable)
    ).apply(ins, DysonOrbitData::new));
    
    public static final StreamCodec<ByteBuf, DysonOrbitData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonOrbitData decode(ByteBuf buf) {
            buf.readInt();
            int id = ByteBufCodecs.INT.decode(buf);
            float radius = ByteBufCodecs.FLOAT.decode(buf);
            Quaternionf rotation = ByteBufCodecs.QUATERNIONF.decode(buf);
            boolean enable = ByteBufCodecs.BOOL.decode(buf);
            return new DysonOrbitData(id, radius, rotation, enable);
        }
        
        @Override
        public void encode(ByteBuf buf, DysonOrbitData value) {
            buf.writeInt(0);
            ByteBufCodecs.INT.encode(buf, value.id());
            ByteBufCodecs.FLOAT.encode(buf, value.radius());
            ByteBufCodecs.QUATERNIONF.encode(buf, value.rotation());
            ByteBufCodecs.BOOL.encode(buf, value.enable());
        }
        
    };
    
    public static final StreamCodec<ByteBuf, List<DysonOrbitData>> LIST_STREAM_CODEC = CodecUtils.StreamCodecs.collection(ArrayList::new, DysonOrbitData.STREAM_CODEC);
    
    public static final StreamCodec<ByteBuf, List<DysonOrbitData>> NULLABLE_LIST_STREAM_CODEC = CodecUtils.StreamCodecs.nullableList(ArrayList::new, DysonOrbitData.STREAM_CODEC);
    
    public boolean isValidOrbit(){
        return radius > 0;
    }
    
    @Override
    public int getID() {
        return id;
    }
}