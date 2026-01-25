package com.xkball.dyson_cube_program.client.b3d.uniform;

import com.xkball.dyson_cube_program.client.renderer.TheSunRenderer;
import com.xkball.dyson_cube_program.utils.client.ClientUtils;
import com.xkball.dyson_cube_program.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class DCPUniforms {
    
    public static final UpdatableUBO THE_SUN_UNIFORM = new UpdatableUBO.UBOBuilder("the_sun_uniform")
            .closeOnExit()
            .putVec3("RenderDir", TheSunRenderer::getRenderDirection)
            .putFloat("ClientTime", ClientUtils::clientTickWithPartialTick)
            .putVec3("Color", () -> ColorUtils.Vectorization.rgbColor(TheSunRenderer.getContextColor()))
            .build();
    
    public static final UpdatableUBO BLOOM_DOWN_SAMPLER_UNIFORM = new UpdatableUBO.UBOBuilder("bloom_down_sampler")
            .closeOnExit()
            .putFloat("Factor",() -> 0f)
            .putInt("Radius",() -> 0)
            .putVec2("BlurDir", Vector2f::new)
            .putVec2("OutSize", Vector2f::new)
            .build();
    
    public static final UpdatableUBO BLOOM_COMPOSITE_UNIFORM = new UpdatableUBO.UBOBuilder("bloom_composite")
            .closeOnExit()
            .putVec2("OutSize", () -> Minecraft.getInstance().getMainRenderTarget().width, () -> Minecraft.getInstance().getMainRenderTarget().height)
            .putFloat("BloomRadius",() -> 1.0f)
            .putFloat("BloomIntensive",() -> 1.7f)
            .build();
    
    //目前仅用于戴森球壳反面渲染 与原版ColorModulator设计显然不同 因此不使用原版的
    public static final UpdatableUBO CUSTOM_COLOR_MODULATOR = new UpdatableUBO.UBOBuilder("custom_color_modulator")
            .closeOnExit()
            .putVec4("CustomColorModulator", Vector4f::new)
            .build();
    
    public static final UpdatableUBO SCREEN_SIZE = new UpdatableUBO.UBOBuilder("screen_size")
            .closeOnExit()
            .putVec2("ScreenSize", () -> 114, () -> 514)
            .build();
    
}
