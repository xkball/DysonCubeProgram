package com.xkball.dyson_cube_program.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class ColorUtils {
    //irrelevant vanilla(笑)
    public static int getColor(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
    
    public static int parseColorHEX(String color) throws IllegalArgumentException {
        if (color.length() == 6) {
            return getColor(
                    Integer.parseInt(color.substring(0, 2), 16),
                    Integer.parseInt(color.substring(2, 4), 16),
                    Integer.parseInt(color.substring(4, 6), 16),
                    255);
        }
        if (color.length() == 8) {
            return getColor(
                    Integer.parseInt(color.substring(0, 2), 16),
                    Integer.parseInt(color.substring(2, 4), 16),
                    Integer.parseInt(color.substring(4, 6), 16),
                    Integer.parseInt(color.substring(6, 8), 16)
            );
        }
        throw new IllegalArgumentException("Format of color must be RGB or RGBA digits");
    }
    
    public static String hexColorFromInt(int color) {
        var a = color >>> 24;
        var r = (color >> 16) & 0xFF;
        var g = (color >> 8) & 0xFF;
        var b = color & 0xFF;
        return String.format("%02X%02X%02X%02X", r, g, b, a).toUpperCase();
    }
    
    public static int rgbaToArgb(int rgba) {
        int r = (rgba >> 24) & 0xFF;
        int g = (rgba >> 16) & 0xFF;
        int b = (rgba >> 8)  & 0xFF;
        int a = rgba & 0xFF;
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int abgrToArgb(int rgba) {
        int a = (rgba >> 24) & 0xFF;
        int b = (rgba >> 16) & 0xFF;
        int g = (rgba >> 8)  & 0xFF;
        int r = rgba & 0xFF;
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int hsvaToRgba(int h, int s, int v, int a) {
        float hue = h * 360f / 255f;
        float sat = s / 255f;
        float val = v / 255f;
        
        float c = val * sat;
        float x = c * (1 - Math.abs((hue / 60f) % 2 - 1));
        float m = val - c;
        
        float r1, g1, b1;
        
        if (hue < 60) {
            r1 = c; g1 = x; b1 = 0;
        } else if (hue < 120) {
            r1 = x; g1 = c; b1 = 0;
        } else if (hue < 180) {
            r1 = 0; g1 = c; b1 = x;
        } else if (hue < 240) {
            r1 = 0; g1 = x; b1 = c;
        } else if (hue < 300) {
            r1 = x; g1 = 0; b1 = c;
        } else {
            r1 = c; g1 = 0; b1 = x;
        }
        
        int r = Math.round((r1 + m) * 255);
        int g = Math.round((g1 + m) * 255);
        int b = Math.round((b1 + m) * 255);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    public static int hsvaToRgba(Vector4f hsva) {
        return hsvaToRgba((int) (hsva.x() * 255), (int) (hsva.y() * 255), (int) (hsva.z() * 255), (int) (hsva.w() * 255));
    }
    
    public static int hsvaToRgba(int hsva) {
        int h = (hsva >> 24) & 0xFF;
        int s = (hsva >> 16) & 0xFF;
        int v = (hsva >> 8)  & 0xFF;
        int a = hsva & 0xFF;
        return hsvaToRgba(h, s, v, a);
    }
    
    public static int editA(int argb, int a){
        return (a << 24) | (argb & 0x00FFFFFF);
    }
    
    public static class Vectorization{
        
        public static Vector3f rgbColor(int color){
            return new Vector3f((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f);
        }
        
        public static Vector4f argbColor(int color){
            return new Vector4f((color >> 16 & 0xFF) / 255f, (color >> 8 & 0xFF) / 255f, (color & 0xFF) / 255f,(color >> 24 & 0xFF) / 255f);
        }
        
        public static Vector4f abgrColor(int color){
            return argbColor(abgrToArgb(color));
        }
    }
}
