package com.silvertiger.sphere;

import de.tschebbischeff.math.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java port of Andreas Kahler's C# IcoSphereCreator class.
 * Creates an icosahedric sphere of a chosen resolution with radius one.
 *
 * @author Andreas Kahler
 * @version 1.0.0
 */
public class IcoSphereCreator {

    private ArrayList<Vector3d> vertices;
    private int index;
    private HashMap<Long, Integer> middlePointIndexCache;

    // add vertex to mesh, fix position to be on unit sphere, return index
    private int addVertex(Vector3d p) {
        this.vertices.add(p.normalize());
        return index++;
    }

    // return index of point in the middle of p1 and p2
    private int getMiddlePoint(int p1, int p2) {
        // first check if we have it already
        boolean firstIsSmaller = p1 < p2;
        long smallerIndex = firstIsSmaller ? p1 : p2;
        long greaterIndex = firstIsSmaller ? p2 : p1;
        long key = (smallerIndex << 32) + greaterIndex;

        if (this.middlePointIndexCache.containsKey(key)) {
            return this.middlePointIndexCache.get(key);
        }

        // not in cache, calculate it
        Vector3d point1 = this.vertices.get(p1);
        Vector3d point2 = this.vertices.get(p2);
        Vector3d middle = new Vector3d(
                (point1.getX() + point2.getX()) / 2.0,
                (point1.getY() + point2.getY()) / 2.0,
                (point1.getZ() + point2.getZ()) / 2.0);

        // add vertex makes sure point is on unit sphere
        int i = addVertex(middle);

        // store it, return index
        this.middlePointIndexCache.put(key, i);
        return i;
    }

    public ArrayList<Vector3d> createIcoSphere(int recursionLevel) {
        this.vertices = new ArrayList<>();
        this.middlePointIndexCache = new HashMap<>();
        this.index = 0;

        // create 12 vertices of a icosahedron
        double t = (1.0d + Math.sqrt(5.0)) / 2.0;

        addVertex(new Vector3d(-1,  t,  0));
        addVertex(new Vector3d( 1,  t,  0));
        addVertex(new Vector3d(-1, -t,  0));
        addVertex(new Vector3d( 1, -t,  0));

        addVertex(new Vector3d( 0, -1,  t));
        addVertex(new Vector3d( 0,  1,  t));
        addVertex(new Vector3d( 0, -1, -t));
        addVertex(new Vector3d( 0,  1, -t));

        addVertex(new Vector3d( t,  0, -1));
        addVertex(new Vector3d( t,  0,  1));
        addVertex(new Vector3d(-t,  0, -1));
        addVertex(new Vector3d(-t,  0,  1));


        // create 20 triangles of the icosahedron
        ArrayList<int[]> faces = new ArrayList<>();

        // 5 faces around point 0
        faces.add(new int[]{0, 11, 5});
        faces.add(new int[]{0, 5, 1});
        faces.add(new int[]{0, 1, 7});
        faces.add(new int[]{0, 7, 10});
        faces.add(new int[]{0, 10, 11});

        // 5 adjacent faces
        faces.add(new int[]{1, 5, 9});
        faces.add(new int[]{5, 11, 4});
        faces.add(new int[]{11, 10, 2});
        faces.add(new int[]{10, 7, 6});
        faces.add(new int[]{7, 1, 8});

        // 5 faces around point 3
        faces.add(new int[]{3, 9, 4});
        faces.add(new int[]{3, 4, 2});
        faces.add(new int[]{3, 2, 6});
        faces.add(new int[]{3, 6, 8});
        faces.add(new int[]{3, 8, 9});

        // 5 adjacent faces
        faces.add(new int[]{4, 9, 5});
        faces.add(new int[]{2, 4, 11});
        faces.add(new int[]{6, 2, 10});
        faces.add(new int[]{8, 6, 7});
        faces.add(new int[]{9, 8, 1});


        // refine triangles
        for(int i = 0; i < recursionLevel; i++) {
            ArrayList<int[]> faces2 = new ArrayList<>();
            for(int[] tri: faces) {
                // replace triangle by 4 triangles
                int a = getMiddlePoint(tri[0], tri[1]);
                int b = getMiddlePoint(tri[1], tri[2]);
                int c = getMiddlePoint(tri[2], tri[0]);

                faces2.add(new int[]{tri[0], a, c});
                faces2.add(new int[]{tri[1], b, a});
                faces2.add(new int[]{tri[2], c, b});
                faces2.add(new int[]{a, b, c});
            }
            faces = faces2;
        }

        ArrayList<Vector3d> mesh = new ArrayList<>();
        // done, now add triangles to mesh
        for(int[] tri: faces) {
            mesh.add(this.vertices.get(tri[0]));
            mesh.add(this.vertices.get(tri[1]));
            mesh.add(this.vertices.get(tri[2]));
        }

        return mesh;
    }
}