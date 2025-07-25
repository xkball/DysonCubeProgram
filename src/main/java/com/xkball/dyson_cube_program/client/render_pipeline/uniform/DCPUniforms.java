package com.xkball.dyson_cube_program.client.render_pipeline.uniform;

import com.xkball.dyson_cube_program.api.client.UpdateWhen;
import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.utils.ClientUtils;

public class DCPUniforms {
    
    public static final UpdatableUBO THE_SUN_UNIFORM = new UpdatableUBO.UBOBuilder("the_sun_uniform")
            .closeOnExit()
            .updateWhen(UpdateWhen.EveryRenderPass)
            .putVec3("RenderDir", TheSunRenderer::getRenderDirection)
            .putFloat("ClientTime", ClientUtils::clientTickWithPartialTick)
            .putInt("Color", TheSunRenderer::getContextColor)
            .build();
    
}
