package com.xkball.dyson_cube_program.client.postprocess;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class DCPPostProcesses {
    
    public static BloomPostProcess BLOOM;
    
    public static void createPostProcess() {
        var window = Minecraft.getInstance().getWindow();
        BLOOM = new BloomPostProcess(window.getWidth(), window.getHeight());
    }
    
    public static void resize(int width, int height) {
        if(BLOOM != null){
            BLOOM.resize(width, height);
        }
    }
}
