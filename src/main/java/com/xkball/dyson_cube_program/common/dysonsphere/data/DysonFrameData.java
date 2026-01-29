package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

@NonNullByDefault
public record DysonFrameData(
        int id,
        int protoID,
        boolean reserved,
        int nodeAID,
        int nodeBID,
        boolean euler,
        int spMax,
        int color  // 4 bytes (RGBA)
) {
    
    public static final Codec<DysonFrameData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("id").forGetter(DysonFrameData::id),
            Codec.INT.fieldOf("protoID").forGetter(DysonFrameData::protoID),
            Codec.BOOL.fieldOf("reserved").forGetter(DysonFrameData::reserved),
            Codec.INT.fieldOf("nodeAID").forGetter(DysonFrameData::nodeAID),
            Codec.INT.fieldOf("nodeBID").forGetter(DysonFrameData::nodeBID),
            Codec.BOOL.fieldOf("euler").forGetter(DysonFrameData::euler),
            Codec.INT.fieldOf("spMax").forGetter(DysonFrameData::spMax),
            Codec.INT.fieldOf("color").forGetter(DysonFrameData::color)
    ).apply(ins, DysonFrameData::new));
    
    
    public static final StreamCodec<ByteBuf, DysonFrameData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonFrameData decode(ByteBuf buf) {
            var version = buf.readInt();
            int id = ByteBufCodecs.INT.decode(buf);
            int protoID = ByteBufCodecs.INT.decode(buf);
            boolean reserved = ByteBufCodecs.BOOL.decode(buf);
            int nodeAID = ByteBufCodecs.INT.decode(buf);
            int nodeBID = ByteBufCodecs.INT.decode(buf);
            boolean euler = ByteBufCodecs.BOOL.decode(buf);
            int spMax = ByteBufCodecs.INT.decode(buf);
            int color = 0;
            if(version >= 1){
                color = ByteBufCodecs.INT.decode(buf);
            }
            return new DysonFrameData(id, protoID, reserved, nodeAID, nodeBID, euler, spMax, color);
        }
        
        @Override
        public void encode(ByteBuf buf, DysonFrameData value) {
            buf.writeInt(1);
            ByteBufCodecs.INT.encode(buf, value.id());
            ByteBufCodecs.INT.encode(buf, value.protoID());
            ByteBufCodecs.BOOL.encode(buf, value.reserved());
            ByteBufCodecs.INT.encode(buf, value.nodeAID());
            ByteBufCodecs.INT.encode(buf, value.nodeBID());
            ByteBufCodecs.BOOL.encode(buf, value.euler());
            ByteBufCodecs.INT.encode(buf, value.spMax());
            ByteBufCodecs.INT.encode(buf, value.color());
        }
    };
    
}