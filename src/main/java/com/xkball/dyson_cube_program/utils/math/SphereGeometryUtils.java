package com.xkball.dyson_cube_program.utils.math;

import com.xkball.dyson_cube_program.utils.ClientUtils;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatArraySet;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.floats.FloatOpenHashSet;
import it.unimi.dsi.fastutil.floats.FloatSet;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SphereGeometryUtils {
    public static List<Quad> earClipping(List<Vector3f> points){
        if(points.size() == 3){
            return List.of(new Quad(points.get(0), points.get(1), points.get(2), points.get(2)));
        }
        if(points.size() == 4){
            return List.of(new Quad(points.get(0), points.get(1), points.get(2), points.get(3)));
        }
        var result = new ArrayList<Quad>();
        var plist = new LinkedList<>(points);
        if(isCounterclockwisePoints(points)) {
            plist = plist.reversed();
        }
        var count = 0;
        while (plist.size() > 3){
            count++;
            var iter = plist.iterator();
            var a= iter.next();
            var b = iter.next();
            var c = plist.getLast();
            if(isEar(a,c,b,plist)){
                result.add(new Quad(a,b,c,c));
                plist.removeFirst();
            }
            else {
                plist.addLast(plist.removeFirst());
            }
            if(count > points.size() * 100){
                ClientUtils.LOGGER.error("Failed to triangulated polygons.");
                ClientUtils.LOGGER.error("points: {} ", points);
                break;
            }
        }
        result.add(new Quad(plist.getFirst(),plist.get(1),plist.getLast(),plist.getLast()));
        return result;
    }
    
    public static boolean isCounterclockwisePoints(List<Vector3f> points){
        var n = new Vector3f();
        for(var i = 0; i < points.size() - 1; i++){
            var a = points.get(i);
            var b = points.get(i + 1);
            n.add(a.cross(b,new Vector3f()));
        }
        return n.dot(points.getFirst()) < 0;
    }
    
    public static boolean isEar(Vector3f c, Vector3f l, Vector3f r, Collection<Vector3f> points){
        var nab = l.cross(c,new Vector3f()).normalize();
        var nbc = c.cross(r,new Vector3f()).normalize();
        var nac = r.cross(l,new Vector3f()).normalize();
        
        var d = l.dot(c.cross(r,new Vector3f()));
        if(d < 0) return false;
        
        for(var p : points){
            //此处用 == 是有意义的 因此也要求输入三个点必须要在points中
            if(p == c || p == l || p == r) continue;
            var dab = Math.signum(p.dot(nab));
            var dbc = Math.signum(p.dot(nbc));
            var dac = Math.signum(p.dot(nac));
            if(dab > 0 && dbc > 0 && dac > 0){
                return false;
            }
        }
        return true;
    }
    
    public static boolean isInside(Vector3f p, Quad quad){
        var pn = p.normalize(new Vector3f());
        var an = quad.a().normalize(new Vector3f());
        var bn = quad.b().normalize(new Vector3f());
        var cn = quad.c().normalize(new Vector3f());
        var dn = quad.d().normalize(new Vector3f());
        var pd = pn.dot(an);
        if(pd < 0) return false;
        pd = pn.dot(bn);
        if(pd < 0) return false;
        pd = pn.dot(cn);
        if(pd < 0) return false;
        pd = pn.dot(dn);
        if(pd < 0) return false;
        List<Vector3f> points = new LinkedList<>();
        //if(!quad.a().equals(quad.b()))
        points.add(an.cross(bn,new Vector3f()));
        //if(!quad.b().equals(quad.c()))
        points.add(bn.cross(cn,new Vector3f()));
        if(!quad.c().equals(quad.d())) points.add(cn.cross(dn,new Vector3f()));
      //  if(!quad.d().equals(quad.a()))
        points.add(dn.cross(an,new Vector3f()));
        float sign = Math.signum(pn.dot(points.getFirst()));
        for (int i = 1; i < points.size(); i++) {
            float nSign = Math.signum(pn.dot(points.get(i)));
            if(sign != nSign) return false;
        }
        return true;
    }
}
