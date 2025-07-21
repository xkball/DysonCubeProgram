package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Set;

@NonNullByDefault
public enum DysonBlueprintType implements StringRepresentable {
	None(Set.of()),
	SingleLayer(Set.of(DysonElementType.LAYER)),
	Layers(Set.of(DysonElementType.LAYERS)),
	SwarmOrbits(Set.of(DysonElementType.SWARMS)),
	DysonSphere(Set.of(DysonElementType.LAYERS,DysonElementType.SWARMS));
	
	public final Set<DysonElementType> elementTypes;
	
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
    
    DysonBlueprintType(Set<DysonElementType> elementTypes) {
        this.elementTypes = elementTypes;
    }
    
    @Override
	public String getSerializedName() {
		return name();
	}
}
