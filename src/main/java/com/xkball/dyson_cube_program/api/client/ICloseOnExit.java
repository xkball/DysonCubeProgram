package com.xkball.dyson_cube_program.api.client;

import com.xkball.dyson_cube_program.client.ClientRenderObjects;

public interface ICloseOnExit<T extends ICloseOnExit<T>> extends AutoCloseable {
    
    @SuppressWarnings("unchecked")
    default T setCloseOnExit(){
        ClientRenderObjects.addCloseOnExit(this);
        return (T) this;
    }
}
