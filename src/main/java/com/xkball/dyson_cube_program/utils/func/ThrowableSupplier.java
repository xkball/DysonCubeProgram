package com.xkball.dyson_cube_program.utils.func;

import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    
    Logger LOGGER = LogUtils.getLogger();
    
    static <T> Supplier<T> unwrapOrThrow(ThrowableSupplier<T> supplier) {
        return () -> getOrThrow(supplier);
    }
    
    static <T> T getOrThrow(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    
    static <T> T getOrThrow(ThrowableSupplier<T> supplier, String errMsg) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            LOGGER.error(errMsg, e);
            throw new RuntimeException(errMsg,e);
        }
    }
    
    static <T> T getOrElse(ThrowableSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return defaultValue;
        }
    }
    
    static <T> T getOrElse(ThrowableSupplier<T> supplier, T defaultValue, String errMsg) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            LOGGER.error(errMsg, e);
            return defaultValue;
        }
    }
    
    static <T> T getOrElse(ThrowableSupplier<T> supplier, T defaultValue, Consumer<Throwable> exceptionHandler) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            exceptionHandler.accept(e);
            return defaultValue;
        }
    }
    
    @Nullable
    static <T> T getOrNull(ThrowableSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return null;
        }
    }
    
    @Nullable
    static <T> T getOrNull(ThrowableSupplier<T> supplier, String errMsg) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            LOGGER.error(errMsg, e);
            return null;
        }
    }
    
    @Nullable
    static <T> T getOrNull(ThrowableSupplier<T> supplier, Consumer<Throwable> exceptionHandler) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            exceptionHandler.accept(e);
            return null;
        }
    }
    
    @NotNull
    T get() throws Throwable;
    
}
