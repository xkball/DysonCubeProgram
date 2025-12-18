package com.xkball.dyson_cube_program.utils.math;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record LatAndLon(double lat, double lon) {
    
    public static LatAndLon fromSperePos(Vector3f pos){
        var n = pos.normalize(new Vector3f());
        var lat = Math.asin(n.y);
        var lon = Math.atan2(n.z, n.x);
        return new LatAndLon(lat, lon);
    }
    
    public static LatAndLon ofDegree(double lat, double lon){
        if(lat > 90) lat = lat - (lat - 90) * 2;
        if(lat < -90) lat = lat + (lat + 90) * 2;
        if(lon > 180) lon -= 360;
        if(lon < -180) lon += 360;
        return new LatAndLon(Math.toRadians(lat), Math.toRadians(lon));
    }
    
    public Vector3f toSperePos(){
        float cosLat = (float) Math.cos(lat);
        float x = cosLat * (float) Math.cos(lon);
        float z = cosLat * (float) Math.sin(lon);
        float y = (float) Math.sin(lat);
        return new Vector3f(x, y, z);
    }
    
    public double getLatDegree(){
        return Math.toDegrees(lat);
    }
    
    public double getLonDegree(){
        return Math.toDegrees(lon);
    }
    
    public Vector2f distanceTo(LatAndLon other){
        var dLat = other.lat - lat;
        var dLon = other.lon - lon;
        //if(dLon > Math.PI) dLon = Math.PI * 2 - dLon;
        return new Vector2f((float) dLat,(float) dLon);
    }
    
    public Matrix4f rotationTo(LatAndLon other){
        var d = distanceTo(other);
        return new Matrix4f().rotateY(d.y).rotateX(d.x);
    }
}
