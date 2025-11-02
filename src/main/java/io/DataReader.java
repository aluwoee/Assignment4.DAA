// src/io/DataReader.java
package io;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataReader {
    public static class GraphData {
        public final int nodes;
        public final List<int[]> edges;
        public final double[] weights;
        public GraphData(int n, List<int[]> e, double[] w) {
            this.nodes = n; this.edges = e; this.weights = w;
        }
    }

    public static GraphData read(String path) throws IOException {
        JsonObject root = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();
        int n = root.getAsJsonObject("meta").get("nodes").getAsInt();
        JsonObject g = root.getAsJsonObject("graph");
        List<int[]> edges = new ArrayList<>();
        List<Double> ws = new ArrayList<>();
        for (String k : g.keySet()) {
            int u = Integer.parseInt(k);
            JsonArray arr = g.getAsJsonArray(k);
            for (JsonElement el : arr) {
                JsonArray a = el.getAsJsonArray();
                int v = a.get(0).getAsInt();
                double w = a.get(1).getAsDouble();
                edges.add(new int[]{u, v});
                ws.add(w);
            }
        }
        double[] wArr = new double[ws.size()];
        for (int i = 0; i < ws.size(); i++) wArr[i] = ws.get(i);
        return new GraphData(n, edges, wArr);
    }
}
