package com.xkball.dyson_cube_program.utils.math;

import com.xkball.dyson_cube_program.utils.ClientUtils;
import org.joml.Vector3f;

import javax.annotation.Nullable;
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
    
    private static final Vector3f pn = new Vector3f();
    private static final Vector3f an = new Vector3f();
    private static final Vector3f bn = new Vector3f();
    private static final Vector3f cn = new Vector3f();
    private static final Vector3f dn = new Vector3f();
    public static boolean isInside(Vector3f p, Quad quad){
        p.normalize(pn);
        quad.a().normalize(an);
        quad.b().normalize(bn);
        quad.c().normalize(cn);
        quad.d().normalize(dn);
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
    
    public static Vector3f slerp(Vector3f a, Vector3f b, float t) {
        var dest = new Vector3f();
        float dot = a.dot(b);
        dot = Math.max(-1.0f, Math.min(1.0f, dot));
        float theta = (float) Math.acos(dot);
        
        if (theta < 1e-6f) {
            dest.set(a).lerp(b, t).normalize();
            return dest;
        }
        
        float sinTheta = (float) Math.sin(theta);
        float w0 = (float) Math.sin((1.0f - t) * theta) / sinTheta;
        float w1 = (float) Math.sin(t * theta) / sinTheta;
        
        dest.set(a).mul(w0).add(new Vector3f(b).mul(w1));
        return dest;
    }
    
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static boolean insideQuads(Vector3f pos, List<Quad> quads){
        for (int i = 0; i < quads.size(); i++) {
            if (SphereGeometryUtils.isInside(pos, quads.get(i))) {
                return true;
            }
        }
        return false;
    }
    
    public static @Nullable Vector3f intersection(Vector3f l1a, Vector3f l1b, Vector3f l2a, Vector3f l2b){
        var l1an = l1a.normalize(new Vector3f());
        var l1bn = l1b.normalize(new Vector3f());
        var l2an = l2a.normalize(new Vector3f());
        var l2bn = l2b.normalize(new Vector3f());
        var n1 = l1an.cross(l1bn, new Vector3f());
        var n2 = l2an.cross(l2bn, new Vector3f());
        if (n1.lengthSquared() < 1e-10f || n2.lengthSquared() < 1e-10f) {
            return null;
        }
        var v = n1.normalize().cross(n2.normalize(), new Vector3f());
        if (v.lengthSquared() < 1e-10f) {
            return null;
        }
        v.normalize();
        var nv = v.negate(new Vector3f());
        
        boolean p1on = onArc(v, l1an, l1bn) && onArc(v, l2an, l2bn);
        boolean p2on = onArc(nv, l1an, l1bn) && onArc(nv, l2an, l2bn);
        if (p1on ^ p2on) {
            return p1on ? v : nv;
        }
        
        return null;
    }
    
    public static boolean onArc(Vector3f p, Vector3f a, Vector3f b) {
        var ap = a.cross(p, new Vector3f()).normalize();
        var ab = a.cross(b, new Vector3f()).normalize();
        var bp = b.cross(p, new Vector3f()).normalize();
        var ba = b.cross(a, new Vector3f()).normalize();
        return ap.dot(ab) >= 0 && bp.dot(ba) >= 0;
    }
}
