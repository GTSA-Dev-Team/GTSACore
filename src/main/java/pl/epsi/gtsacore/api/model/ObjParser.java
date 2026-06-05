package pl.epsi.gtsacore.api.model;

import net.minecraft.resources.ResourceLocation;
import pl.epsi.gtsacore.util.SACUtils;

import java.io.*;
import java.util.*;

public final class ObjParser {

    private record Key(int v, int vt, int vn) {}

    public static ObjMesh load(ResourceLocation rl) throws IOException {

        List<float[]> positions = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<float[]> normals = new ArrayList<>();

        List<ObjVertexFormat> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Map<Key, Integer> cache = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(SACUtils.asInputStream(rl)))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] p = line.split("\\s+");

                switch (p[0]) {

                    case "v" -> positions.add(new float[]{
                            Float.parseFloat(p[1]),
                            Float.parseFloat(p[2]),
                            Float.parseFloat(p[3])
                    });

                    case "vt" -> uvs.add(new float[]{
                            Float.parseFloat(p[1]),
                            Float.parseFloat(p[2])
                    });

                    case "vn" -> normals.add(new float[]{
                            Float.parseFloat(p[1]),
                            Float.parseFloat(p[2]),
                            Float.parseFloat(p[3])
                    });

                    case "f" -> {
                        for (int i = 1; i < p.length - 2; i++) {
                            addFaceVertex(p[1], positions, uvs, normals, vertices, indices, cache);
                            addFaceVertex(p[i + 1], positions, uvs, normals, vertices, indices, cache);
                            addFaceVertex(p[i + 2], positions, uvs, normals, vertices, indices, cache);
                        }
                    }
                }
            }
        }

        int[] indexArray = indices.stream().mapToInt(i -> i).toArray();

        return new ObjMesh(
                vertices.toArray(new ObjVertexFormat[0]),
                indexArray
        );
    }

    private static void addFaceVertex(
            String token,
            List<float[]> pos,
            List<float[]> uv,
            List<float[]> norm,
            List<ObjVertexFormat> outVerts,
            List<Integer> outIdx,
            Map<Key, Integer> cache
    ) {
        String[] t = token.split("/");

        int vi = parseIndex(t[0], pos.size());
        int vti = (t.length > 1 && !t[1].isEmpty()) ? parseIndex(t[1], uv.size()) : -1;
        int vni = (t.length > 2 && !t[2].isEmpty()) ? parseIndex(t[2], norm.size()) : -1;

        Key key = new Key(vi, vti, vni);

        Integer index = cache.get(key);
        if (index != null) {
            outIdx.add(index);
            return;
        }

        float[] v = pos.get(vi);

        float[] vt = (vti >= 0) ? uv.get(vti) : new float[]{0, 0};
        float[] vn = (vni >= 0) ? norm.get(vni) : new float[]{0, 0, 0};

        ObjVertexFormat vertex = new ObjVertexFormat(
                v[0], v[1], v[2],
                vt[0], vt[1],
                vn[0], vn[1], vn[2]
        );

        int newIndex = outVerts.size();
        outVerts.add(vertex);
        cache.put(key, newIndex);
        outIdx.add(newIndex);
    }

    private static int parseIndex(String s, int size) {
        int i = Integer.parseInt(s);
        return (i < 0) ? size + i : i - 1;
    }
}