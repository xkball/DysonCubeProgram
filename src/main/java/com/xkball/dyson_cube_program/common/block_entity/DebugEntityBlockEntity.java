package com.xkball.dyson_cube_program.common.block_entity;

import com.xkball.dyson_cube_program.api.annotation.NonNullByDefault;
import com.xkball.dyson_cube_program.common.DCPTempReg;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@NonNullByDefault
public class DebugEntityBlockEntity extends BlockEntity {
    
    public DebugEntityBlockEntity(BlockPos pos, BlockState blockState) {
        super(DCPTempReg.DEBUG_ENTITY_BLOCK_ENTITY.get(), pos, blockState);
        
    }
}
