package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public record DysonNodeData(
        int id,
        int protoID,
        boolean use,
        boolean reserved,
        Vector3f pos,
        int spMax,
        int rid,
        int frameTurn,
        int shellTurn,
        int spReq,
        int cpReq,
        int color // 4 bytes (RGBA)
) {
    
    public static final Codec<DysonNodeData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("id").forGetter(DysonNodeData::id),
            Codec.INT.fieldOf("protoID").forGetter(DysonNodeData::protoID),
            Codec.BOOL.fieldOf("use").forGetter(DysonNodeData::use),
            Codec.BOOL.fieldOf("reserved").forGetter(DysonNodeData::reserved),
            ExtraCodecs.VECTOR3F.fieldOf("pos").forGetter(DysonNodeData::pos),
            Codec.INT.fieldOf("spMax").forGetter(DysonNodeData::spMax),
            Codec.INT.fieldOf("rid").forGetter(DysonNodeData::rid),
            Codec.INT.fieldOf("frameTurn").forGetter(DysonNodeData::frameTurn),
            Codec.INT.fieldOf("shellTurn").forGetter(DysonNodeData::shellTurn),
            Codec.INT.fieldOf("spReq").forGetter(DysonNodeData::spReq),
            Codec.INT.fieldOf("cpReq").forGetter(DysonNodeData::cpReq),
            Codec.INT.fieldOf("color").forGetter(DysonNodeData::color)
    ).apply(ins, DysonNodeData::new));
    
    public static final StreamCodec<ByteBuf, DysonNodeData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonNodeData decode(ByteBuf buf) {
            buf.readInt();
            int id = ByteBufCodecs.INT.decode(buf);
            int protoID = ByteBufCodecs.INT.decode(buf);
            boolean use = ByteBufCodecs.BOOL.decode(buf);
            boolean reserved = ByteBufCodecs.BOOL.decode(buf);
            Vector3f pos = ByteBufCodecs.VECTOR3F.decode(buf);
            int spMax = ByteBufCodecs.INT.decode(buf);
            int rid = ByteBufCodecs.INT.decode(buf);
            int frameTurn = ByteBufCodecs.INT.decode(buf);
            int shellTurn = ByteBufCodecs.INT.decode(buf);
            int spReq = ByteBufCodecs.INT.decode(buf);
            int cpReq = ByteBufCodecs.INT.decode(buf);
            int color = ByteBufCodecs.INT.decode(buf);
            return new DysonNodeData(id, protoID, use, reserved, pos, spMax, rid, frameTurn, shellTurn, spReq, cpReq, color);
        }
        
        @Override
        public void encode(ByteBuf buf, DysonNodeData value) {
            buf.writeInt(5);
            ByteBufCodecs.INT.encode(buf, value.id());
            ByteBufCodecs.INT.encode(buf, value.protoID());
            ByteBufCodecs.BOOL.encode(buf, value.use());
            ByteBufCodecs.BOOL.encode(buf, value.reserved());
            ByteBufCodecs.VECTOR3F.encode(buf, value.pos());
            ByteBufCodecs.INT.encode(buf, value.spMax());
            ByteBufCodecs.INT.encode(buf, value.rid());
            ByteBufCodecs.INT.encode(buf, value.frameTurn());
            ByteBufCodecs.INT.encode(buf, value.shellTurn());
            ByteBufCodecs.INT.encode(buf, value.spReq());
            ByteBufCodecs.INT.encode(buf, value.cpReq());
            ByteBufCodecs.INT.encode(buf, value.color());
        }
    };
    
}