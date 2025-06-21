package com.xkball.dyson_cube_program.common.block.block_entity;

import com.mojang.serialization.MapCodec;
import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@NonNullByDefault
public class DebugEntityBlock extends BaseEntityBlock {
    
    public static final MapCodec<DebugEntityBlock> CODEC = simpleCodec(DebugEntityBlock::new);
    
    public DebugEntityBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    
    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }
    
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DebugEntityBlockEntity(pos, state);
    }
}
