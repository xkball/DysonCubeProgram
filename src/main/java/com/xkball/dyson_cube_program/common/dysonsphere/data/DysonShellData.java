package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

@NonNullByDefault
public record DysonShellData(
        int id,
        int protoID,
        int randSeed,
        int color, // 4 bytes (RGBA)
        List<Integer> nodes  // List of node IDs
) {
    
    public static final Codec<DysonShellData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("id").forGetter(DysonShellData::id),
            Codec.INT.fieldOf("protoID").forGetter(DysonShellData::protoID),
            Codec.INT.fieldOf("randSeed").forGetter(DysonShellData::randSeed),
            Codec.INT.fieldOf("color").forGetter(DysonShellData::color),
            Codec.INT.listOf().fieldOf("nodes").forGetter(DysonShellData::nodes)
    ).apply(ins, DysonShellData::new));
    
    
    public static final StreamCodec<ByteBuf, DysonShellData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonShellData decode(ByteBuf buffer) {
            buffer.readInt();
            var id = buffer.readInt();
            var protoID = buffer.readInt();
            var randSeed = buffer.readInt();
            var color = buffer.readInt();
            var nodes = CodecUtils.StreamCodecs.INT_LIST.decode(buffer);
            return new DysonShellData(id, protoID, randSeed, color, nodes);
        }
        
        @Override
        public void encode(ByteBuf buffer, DysonShellData value) {
            buffer.writeInt(4);
            buffer.writeInt(value.id);
            buffer.writeInt(value.protoID);
            buffer.writeInt(value.randSeed);
            buffer.writeInt(value.color);
            CodecUtils.StreamCodecs.INT_LIST.encode(buffer, value.nodes);
        }
    };
    
}