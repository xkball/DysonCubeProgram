package com.xkball.dyson_cube_program.common;

import com.xkball.dyson_cube_program.DysonCubeProgram;
import com.xkball.dyson_cube_program.common.block.block_entity.DebugEntityBlock;
import com.xkball.dyson_cube_program.common.block_entity.DebugEntityBlockEntity;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DCPTempReg {
    
    public static final DeferredRegister.Items ITEM = DeferredRegister.Items.createItems(DysonCubeProgram.MODID);
    public static final DeferredRegister.Blocks BLOCK = DeferredRegister.Blocks.createBlocks(DysonCubeProgram.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DysonCubeProgram.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DysonCubeProgram.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT = DeferredRegister.DataComponents.createDataComponents(Registries.DATA_COMPONENT_TYPE,DysonCubeProgram.MODID);
    
    public static final DeferredBlock<DebugEntityBlock> DEBUG_ENTITY_BLOCK = BLOCK.registerBlock("debug_entity_block",DebugEntityBlock::new);
    
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DebugEntityBlockEntity>> DEBUG_ENTITY_BLOCK_ENTITY = BLOCK_ENTITY.register("debug_entity_block",() ->
                   new BlockEntityType<>(DebugEntityBlockEntity::new, DEBUG_ENTITY_BLOCK.get()));
    
    public static final DeferredItem<BlockItem> DEBUG_ENTITY_BLOCK_ITEM = ITEM.registerSimpleBlockItem(DEBUG_ENTITY_BLOCK);
    
    public static void init(IEventBus bus) {
        ITEM.register(bus);
        BLOCK.register(bus);
        CREATIVE_TAB.register(bus);
        BLOCK_ENTITY.register(bus);
        DATA_COMPONENT.register(bus);
    }
    
    @SubscribeEventEnhanced
    public static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.OP_BLOCKS){
            event.accept(DEBUG_ENTITY_BLOCK_ITEM);
        }
    }
}
