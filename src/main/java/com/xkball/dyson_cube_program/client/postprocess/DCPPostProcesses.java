package com.xkball.dyson_cube_program.client.postprocess;

public class DCPPostProcesses {
    
    public static BloomPostProcess BLOOM;
    
    public static void resize(int width, int height) {
        if(BLOOM != null){
            BLOOM.resize(width, height);
        }
        else {
            BLOOM = new BloomPostProcess(width, height);
        }
    }
}
