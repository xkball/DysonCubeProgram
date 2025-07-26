package com.xkball.dyson_cube_program.client.renderer;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL43;

public class RenderEvents {
    
    //@SubscribeEventEnhanced(value = SubscribeEventEnhanced.Dist.CLIENT)
    public static void afterRenderTE(RenderLevelStageEvent.AfterBlockEntities event){
        //if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        GL43.glPushDebugGroup(1,-1,"Render Sphere");
        //ClientUtils.drawWithRenderType(RenderType.DEBUG_QUADS,TheSunRenderer.SUPER_SUN_MESH.get());
        //ClientUtils.drawWithRenderType(RenderType.DEBUG_QUADS,TheSunRenderer.NEAR_SUN_MESH.get());
        //ClientUtils.drawWithRenderType(RenderType.DEBUG_QUADS,TheSunRenderer.FAR_SUN_MESH.get());
        GL43.glPopDebugGroup();
    }
}
