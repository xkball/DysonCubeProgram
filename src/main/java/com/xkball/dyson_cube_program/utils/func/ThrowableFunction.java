package com.xkball.dyson_cube_program.utils.func;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowableFunction<T,R> {
    
    static <T,R> Function<T,R> unwrapOrThrow(ThrowableFunction<T,R> function) {
        return (t) -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
    
    R apply(T t) throws Throwable;
}
