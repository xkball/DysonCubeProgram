package com.xkball.dyson_cube_program.common.dysonsphere.data;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.utils.CodecUtils;
import com.xkball.dyson_cube_program.utils.VanillaUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector4f;

import javax.annotation.Nullable;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@NonNullByDefault
public record DysonSpareBlueprintData(
        long timestamp,
        String gameVersion,
        DysonBlueprintType type,
        int latLimit,
        
        int editorRenderMaskS,
        int gameRenderMaskS,
        List<DysonOrbitData> swarmOrbits,
        List<Vector4f> sailOrbitColorHSVA,
        int editorRenderMaskL,
        int gameRenderMaskL,
        List<DysonOrbitData> layerOrbits,
        List<DysonSphereLayerData> layers,
        @Nullable DysonSphereLayerData singleLayer
) {
    
    public static final Codec<DysonSpareBlueprintData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.LONG.fieldOf("timestamp").forGetter(DysonSpareBlueprintData::timestamp),
            Codec.STRING.fieldOf("gameVersion").forGetter(DysonSpareBlueprintData::gameVersion),
            DysonBlueprintType.CODEC.fieldOf("type").forGetter(DysonSpareBlueprintData::type),
            Codec.INT.fieldOf("latLimit").forGetter(DysonSpareBlueprintData::latLimit),
            Codec.INT.fieldOf("editorRenderMaskS").forGetter(DysonSpareBlueprintData::editorRenderMaskS),
            Codec.INT.fieldOf("gameRenderMaskS").forGetter(DysonSpareBlueprintData::gameRenderMaskS),
            DysonOrbitData.CODEC.listOf().fieldOf("swarmOrbits").forGetter(DysonSpareBlueprintData::swarmOrbits),
            ExtraCodecs.VECTOR4F.listOf().fieldOf("sailOrbitColorHSVA").forGetter(DysonSpareBlueprintData::sailOrbitColorHSVA),
            Codec.INT.fieldOf("editorRenderMaskL").forGetter(DysonSpareBlueprintData::editorRenderMaskL),
            Codec.INT.fieldOf("gameRenderMaskL").forGetter(DysonSpareBlueprintData::gameRenderMaskL),
            DysonOrbitData.CODEC.listOf().fieldOf("layerOrbits").forGetter(DysonSpareBlueprintData::layerOrbits),
            DysonSphereLayerData.CODEC.listOf().fieldOf("layers").forGetter(DysonSpareBlueprintData::layers),
            DysonSphereLayerData.CODEC.fieldOf("singleLayer").forGetter(DysonSpareBlueprintData::singleLayer)
    ).apply(ins, DysonSpareBlueprintData::new));
    
    
    public static final StreamCodec<ByteBuf, DysonSpareBlueprintData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonSpareBlueprintData decode(ByteBuf buf) {
            long timestamp = CodecUtils.StreamCodecs.LONG.decode(buf);
            String gameVersion = ByteBufCodecs.STRING_UTF8.decode(buf);
            DysonBlueprintType type = DysonBlueprintType.STREAM_CODEC.decode(buf);
            int latLimit = ByteBufCodecs.INT.decode(buf);
            int editorRenderMaskS = ByteBufCodecs.INT.decode(buf);
            int gameRenderMaskS = ByteBufCodecs.INT.decode(buf);
            List<DysonOrbitData> swarmOrbits = DysonOrbitData.LIST_STREAM_CODEC.decode(buf);
            List<Vector4f> sailOrbitColorHSVA = CodecUtils.StreamCodecs.VECTOR4F_LIST.decode(buf);
            int editorRenderMaskL = ByteBufCodecs.INT.decode(buf);
            int gameRenderMaskL = ByteBufCodecs.INT.decode(buf);
            List<DysonOrbitData> layerOrbits = DysonOrbitData.LIST_STREAM_CODEC.decode(buf);
            List<DysonSphereLayerData> layers = DysonSphereLayerData.LIST_STREAM_CODEC.decode(buf);
            DysonSphereLayerData singleLayer = DysonSphereLayerData.STREAM_CODEC.decode(buf);
            return new DysonSpareBlueprintData(timestamp, gameVersion, type, latLimit, editorRenderMaskS, gameRenderMaskS, swarmOrbits, sailOrbitColorHSVA, editorRenderMaskL, gameRenderMaskL, layerOrbits, layers, singleLayer);
        }
        
        @Override
        public void encode(ByteBuf buf, DysonSpareBlueprintData value) {
            CodecUtils.StreamCodecs.LONG.encode(buf, value.timestamp());
            ByteBufCodecs.STRING_UTF8.encode(buf, value.gameVersion());
            DysonBlueprintType.STREAM_CODEC.encode(buf, value.type());
            ByteBufCodecs.INT.encode(buf, value.latLimit());
            ByteBufCodecs.INT.encode(buf, value.editorRenderMaskS());
            ByteBufCodecs.INT.encode(buf, value.gameRenderMaskS());
            DysonOrbitData.LIST_STREAM_CODEC.encode(buf, value.swarmOrbits());
            CodecUtils.StreamCodecs.VECTOR4F_LIST.encode(buf, value.sailOrbitColorHSVA());
            ByteBufCodecs.INT.encode(buf, value.editorRenderMaskL());
            ByteBufCodecs.INT.encode(buf, value.gameRenderMaskL());
            DysonOrbitData.LIST_STREAM_CODEC.encode(buf, value.layerOrbits());
            DysonSphereLayerData.LIST_STREAM_CODEC.encode(buf, value.layers());
            DysonSphereLayerData.STREAM_CODEC.encode(buf, value.singleLayer());
        }
    };
    
    public static DysonSpareBlueprintData parse(String str){
        var l1data = str.split("\"");
        var data = VanillaUtils.unGzip(VanillaUtils.unBase64(l1data[1]));
        var head = l1data[0].split(",");
        
        assert head.length == 5 && "DYBP:0".equals(head[0]);
        var timeStamp = Long.parseLong(head[1]);
        var gameVersion = head[2];
        var type = DysonBlueprintType.VALUES[Integer.parseInt(head[3])];
        var latLimit = Integer.parseInt(head[4]);
        
        int editorRenderMaskS = 0;
        int gameRenderMaskS = 0;
        List<DysonOrbitData> swarmOrbits = new ArrayList<>();
        List<Vector4f> sailOrbitColorHSVA = new ArrayList<>();
        int editorRenderMaskL = 0;
        int gameRenderMaskL = 0;
        List<DysonOrbitData> layerOrbits = new ArrayList<>();
        List<DysonSphereLayerData> layers = new ArrayList<>();
        DysonSphereLayerData singleLayer = null;
        
        @SuppressWarnings("deprecation")
        var byteBuf = Unpooled.copiedBuffer(data).order(ByteOrder.LITTLE_ENDIAN);
        
        byteBuf.readInt();
        if(type == DysonBlueprintType.SwarmOrbits || type == DysonBlueprintType.DysonSphere){
            editorRenderMaskS = byteBuf.readInt();
            gameRenderMaskS = byteBuf.readInt();
            for(var i = 0; i < 20; i++){
                swarmOrbits.add(DysonOrbitData.STREAM_CODEC.decode(byteBuf));
            }
            sailOrbitColorHSVA = CodecUtils.StreamCodecs.VECTOR4F_LIST.decode(byteBuf);
        }
        
        if(type == DysonBlueprintType.Layers || type == DysonBlueprintType.DysonSphere){
            editorRenderMaskL = byteBuf.readInt();
            gameRenderMaskL = byteBuf.readInt();
            layerOrbits = DysonOrbitData.NULLABLE_LIST_STREAM_CODEC.decode(byteBuf);
            layers = DysonSphereLayerData.NULLABLE_LIST_STREAM_CODEC.decode(byteBuf);
            
        }
        
        if(type == DysonBlueprintType.SingleLayer){
            singleLayer = DysonSphereLayerData.STREAM_CODEC.decode(byteBuf);
        }
        return new DysonSpareBlueprintData(timeStamp,gameVersion,type,latLimit,editorRenderMaskS,gameRenderMaskS,swarmOrbits,sailOrbitColorHSVA,editorRenderMaskL,gameRenderMaskL,layerOrbits,layers,singleLayer);
    }
}