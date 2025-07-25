package com.xkball.dyson_cube_program.client;

import com.xkball.dyson_cube_program.utils.VanillaUtils;
import com.xkball.xorlib.api.annotation.SubscribeEventEnhanced;
import net.minecraft.client.resources.model.QuadCollection;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

public class DCPStandaloneModels {
    
    public static final StandaloneModelKey<QuadCollection> DYSON_NODE_KEY = new StandaloneModelKey<>(() -> "dyson_node");
    
    @SubscribeEventEnhanced
    public static void onRegAdditional(ModelEvent.RegisterStandalone event){
        event.register(DCPStandaloneModels.DYSON_NODE_KEY, SimpleUnbakedStandaloneModel.quadCollection(VanillaUtils.modRL("additional/dyson_node")));
    }
}
