package com.xkball.dyson_cube_program.utils.math;

import com.xkball.dyson_cube_program.client.renderer.SphereHexGridMesh;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathConstants {
    
    public static final Vector3f X_POSITIVE = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f Y_POSITIVE = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f Z_POSITIVE = new Vector3f(0.0f, 0.0f, 1.0f);
    public static final Vector3f X_NEGATIVE = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static final Vector3f Y_NEGATIVE = new Vector3f(0.0f, -1.0f, 0.0f);
    public static final Vector3f Z_NEGATIVE = new Vector3f(0.0f, 0.0f, -1.0f);
    
    public static final Vector3f ICOSAHEDRON0 = SphereHexGridMesh.icosahedronVertices.get(0);
    public static final Vector3f ICOSAHEDRON1 = SphereHexGridMesh.icosahedronVertices.get(1);
    public static final Vector3f ICOSAHEDRON2 = SphereHexGridMesh.icosahedronVertices.get(2);
    public static final Vector3f ICOSAHEDRON_FACE1_CENTER = new Vector3f().add(ICOSAHEDRON0).add(ICOSAHEDRON1).add(ICOSAHEDRON2).normalize();
    
    public static final Vector4f ZERO_VEC4 = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
}
