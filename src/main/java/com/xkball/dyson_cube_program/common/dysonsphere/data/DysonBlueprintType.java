package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

@NonNullByDefault
public enum DysonBlueprintType implements StringRepresentable {
	None,
	SingleLayer,
	Layers,
	SwarmOrbits,
	DysonSphere;
	
	public static final DysonBlueprintType[] VALUES = values();
	
	public static final Codec<DysonBlueprintType> CODEC = StringRepresentable.fromEnum(DysonBlueprintType::values);
	
	public static final StreamCodec<ByteBuf, DysonBlueprintType> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonBlueprintType decode(ByteBuf buffer) {
            return VALUES[buffer.readInt()];
        }
        
        @Override
        public void encode(ByteBuf buffer, DysonBlueprintType value) {
        	buffer.writeInt(value.ordinal());
        }
    };
	
	@Override
	public String getSerializedName() {
		return name();
	}
}
