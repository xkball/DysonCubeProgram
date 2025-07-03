package com.xkball.dyson_cube_program.common.dysonsphere.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

@NonNullByDefault
public record DysonSphereLayerData(
    int nodeCapacity,
    int nodeCursor,
    int nodeRecycleCursor,
    List<DysonNodeData> nodePool,
    List<Integer> nodeRecycle,

    int frameCapacity,
    int frameCursor,
    int frameRecycleCursor,
    List<DysonFrameData> framePool,
    List<Integer> frameRecycle,

    int shellCapacity,
    int shellCursor,
    int shellRecycleCursor,
    List<DysonShellData> shellPool,
    List<Integer> shellRecycle,

    int paintGridMode,
    @Nullable List<Integer> cellColors
) {
    
    public DysonSphereLayerData(DysonSphereLayer_NodeData nodeData, DysonSphereLayer_FrameData frameData, DysonSphereLayer_ShellData shellData, int paintGridMode, List<Integer> cellColors){
        this(nodeData.nodeCapacity,   nodeData.nodeCursor,   nodeData.nodeRecycleCursor,   nodeData.nodePool,   nodeData.nodeRecycle,
             frameData.frameCapacity, frameData.frameCursor, frameData.frameRecycleCursor, frameData.framePool, frameData.frameRecycle,
             shellData.shellCapacity, shellData.shellCursor, shellData.shellRecycleCursor, shellData.shellPool, shellData.shellRecycle, paintGridMode, cellColors);
    }
    
    public static final Codec<DysonSphereLayerData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            DysonSphereLayer_NodeData.CODEC.fieldOf("nodeData").forGetter(DysonSphereLayerData::getNodeData),
            DysonSphereLayer_FrameData.CODEC.fieldOf("frameData").forGetter(DysonSphereLayerData::getFrameData),
            DysonSphereLayer_ShellData.CODEC.fieldOf("shellData").forGetter(DysonSphereLayerData::getShellData),
            Codec.INT.fieldOf("paintGridMode").forGetter(DysonSphereLayerData::paintGridMode),
            Codec.INT.listOf().fieldOf("cellColors").forGetter(DysonSphereLayerData::cellColors)
    ).apply(ins, DysonSphereLayerData::new));
    
    public static final StreamCodec<ByteBuf, DysonSphereLayerData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public DysonSphereLayerData decode(ByteBuf buf) {
            buf.readInt();
            int nodeCapacity = ByteBufCodecs.INT.decode(buf);
            int nodeCursor = ByteBufCodecs.INT.decode(buf);
            int nodeRecycleCursor = ByteBufCodecs.INT.decode(buf);
            List<DysonNodeData> nodePool = CodecUtils.StreamCodecs.fixLengthNullableList(ArrayList::new,DysonNodeData.STREAM_CODEC,nodeCursor-1).decode(buf);
            List<Integer> nodeRecycle = CodecUtils.StreamCodecs.fixLengthList(ArrayList::new,ByteBufCodecs.INT,nodeRecycleCursor).decode(buf);
            int frameCapacity = ByteBufCodecs.INT.decode(buf);
            int frameCursor = ByteBufCodecs.INT.decode(buf);
            int frameRecycleCursor = ByteBufCodecs.INT.decode(buf);
            List<DysonFrameData> framePool = CodecUtils.StreamCodecs.fixLengthNullableList(ArrayList::new,DysonFrameData.STREAM_CODEC,frameCursor-1).decode(buf);;
            List<Integer> frameRecycle = CodecUtils.StreamCodecs.fixLengthList(ArrayList::new,ByteBufCodecs.INT,frameRecycleCursor).decode(buf);
            int shellCapacity = ByteBufCodecs.INT.decode(buf);
            int shellCursor = ByteBufCodecs.INT.decode(buf);
            int shellRecycleCursor = ByteBufCodecs.INT.decode(buf);
            List<DysonShellData> shellPool = CodecUtils.StreamCodecs.fixLengthNullableList(ArrayList::new,DysonShellData.STREAM_CODEC,shellCursor-1).decode(buf);
            List<Integer> shellRecycle = CodecUtils.StreamCodecs.fixLengthList(ArrayList::new,ByteBufCodecs.INT,shellRecycleCursor).decode(buf);
            int paintGridMode = ByteBufCodecs.INT.decode(buf);
            List<Integer> cellColors = null;
            if(buf.readBoolean()) cellColors = CodecUtils.StreamCodecs.INT_LIST.decode(buf);
            return new DysonSphereLayerData(nodeCapacity, nodeCursor, nodeRecycleCursor, nodePool, nodeRecycle, frameCapacity, frameCursor, frameRecycleCursor, framePool, frameRecycle, shellCapacity, shellCursor, shellRecycleCursor, shellPool, shellRecycle, paintGridMode, cellColors);
        }
        
        @Override
        public void encode(ByteBuf buf, DysonSphereLayerData value) {
            buf.writeInt(2);
            ByteBufCodecs.INT.encode(buf, value.nodeCapacity());
            ByteBufCodecs.INT.encode(buf, value.nodeCursor());
            ByteBufCodecs.INT.encode(buf, value.nodeRecycleCursor());
            CodecUtils.StreamCodecs.fixLengthNullableList((IntFunction<List<DysonNodeData>>) ArrayList::new,DysonNodeData.STREAM_CODEC, value.nodeCursor()-1).encode(buf, value.nodePool());
            CodecUtils.StreamCodecs.fixLengthList((IntFunction<List<Integer>>) ArrayList::new,ByteBufCodecs.INT,value.nodeRecycleCursor()).encode(buf, value.nodeRecycle());
            ByteBufCodecs.INT.encode(buf, value.frameCapacity());
            ByteBufCodecs.INT.encode(buf, value.frameCursor());
            ByteBufCodecs.INT.encode(buf, value.frameRecycleCursor());
            CodecUtils.StreamCodecs.fixLengthNullableList((IntFunction<List<DysonFrameData>>) ArrayList::new,DysonFrameData.STREAM_CODEC, value.nodeCursor()-1).encode(buf, value.framePool());
            CodecUtils.StreamCodecs.fixLengthList((IntFunction<List<Integer>>) ArrayList::new,ByteBufCodecs.INT,value.frameRecycleCursor()).encode(buf, value.frameRecycle());
            ByteBufCodecs.INT.encode(buf, value.shellCapacity());
            ByteBufCodecs.INT.encode(buf, value.shellCursor());
            ByteBufCodecs.INT.encode(buf, value.shellRecycleCursor());
            CodecUtils.StreamCodecs.fixLengthNullableList((IntFunction<List<DysonShellData>>) ArrayList::new,DysonShellData.STREAM_CODEC, value.nodeCursor()-1).encode(buf, value.shellPool());
            CodecUtils.StreamCodecs.fixLengthList((IntFunction<List<Integer>>) ArrayList::new,ByteBufCodecs.INT,value.shellRecycleCursor()).encode(buf, value.shellRecycle());
            ByteBufCodecs.INT.encode(buf, value.paintGridMode());
            if(value.cellColors() != null){
                buf.writeBoolean(true);
                CodecUtils.StreamCodecs.INT_LIST.encode(buf, value.cellColors());
            }
            else {
                buf.writeBoolean(false);
            }
        }
    };
    
    public static final StreamCodec<ByteBuf, List<DysonSphereLayerData>> LIST_STREAM_CODEC = CodecUtils.StreamCodecs.collection(ArrayList::new, DysonSphereLayerData.STREAM_CODEC);
    
    public static final StreamCodec<ByteBuf, List<DysonSphereLayerData>> NULLABLE_LIST_STREAM_CODEC = CodecUtils.StreamCodecs.nullableList(ArrayList::new, DysonSphereLayerData.STREAM_CODEC);
    
    public DysonSphereLayer_NodeData getNodeData(){
        return new DysonSphereLayer_NodeData(nodeCapacity, nodeCursor, nodeRecycleCursor, nodePool, nodeRecycle);
    }
    
    public DysonSphereLayer_FrameData getFrameData(){
        return new DysonSphereLayer_FrameData(frameCapacity, frameCursor, frameRecycleCursor, framePool, frameRecycle);
    }
    
    public DysonSphereLayer_ShellData getShellData(){
        return new DysonSphereLayer_ShellData(shellCapacity, shellCursor, shellRecycleCursor, shellPool, shellRecycle);
    }
    
    public record DysonSphereLayer_NodeData(int nodeCapacity,
                                            int nodeCursor,
                                            int nodeRecycleCursor,
                                            List<DysonNodeData> nodePool,
                                            List<Integer> nodeRecycle){
        
        public static final Codec<DysonSphereLayer_NodeData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.INT.fieldOf("nodeCapacity").forGetter(DysonSphereLayer_NodeData::nodeCapacity),
                Codec.INT.fieldOf("nodeCursor").forGetter(DysonSphereLayer_NodeData::nodeCursor),
                Codec.INT.fieldOf("nodeRecycleCursor").forGetter(DysonSphereLayer_NodeData::nodeRecycleCursor),
                DysonNodeData.CODEC.listOf().fieldOf("nodePool").forGetter(DysonSphereLayer_NodeData::nodePool),
                Codec.INT.listOf().fieldOf("nodeRecycle").forGetter(DysonSphereLayer_NodeData::nodeRecycle)
        ).apply(ins, DysonSphereLayer_NodeData::new));
        
    }
    
    public record DysonSphereLayer_FrameData(int frameCapacity,
                                             int frameCursor,
                                             int frameRecycleCursor,
                                             List<DysonFrameData> framePool,
                                             List<Integer> frameRecycle){
        
        public static final Codec<DysonSphereLayer_FrameData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.INT.fieldOf("frameCapacity").forGetter(DysonSphereLayer_FrameData::frameCapacity),
                Codec.INT.fieldOf("frameCursor").forGetter(DysonSphereLayer_FrameData::frameCursor),
                Codec.INT.fieldOf("frameRecycleCursor").forGetter(DysonSphereLayer_FrameData::frameRecycleCursor),
                DysonFrameData.CODEC.listOf().fieldOf("framePool").forGetter(DysonSphereLayer_FrameData::framePool),
                Codec.INT.listOf().fieldOf("frameRecycle").forGetter(DysonSphereLayer_FrameData::frameRecycle)
        ).apply(ins, DysonSphereLayer_FrameData::new));
    }
    
    public record DysonSphereLayer_ShellData(int shellCapacity,
                                             int shellCursor,
                                             int shellRecycleCursor,
                                             List<DysonShellData> shellPool,
                                             List<Integer> shellRecycle){
        
        public static final Codec<DysonSphereLayer_ShellData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Codec.INT.fieldOf("shellCapacity").forGetter(DysonSphereLayer_ShellData::shellCapacity),
                Codec.INT.fieldOf("shellCursor").forGetter(DysonSphereLayer_ShellData::shellCursor),
                Codec.INT.fieldOf("shellRecycleCursor").forGetter(DysonSphereLayer_ShellData::shellRecycleCursor),
                DysonShellData.CODEC.listOf().fieldOf("shellPool").forGetter(DysonSphereLayer_ShellData::shellPool),
                Codec.INT.listOf().fieldOf("shellRecycle").forGetter(DysonSphereLayer_ShellData::shellRecycle)
        ).apply(ins, DysonSphereLayer_ShellData::new));
    }
}