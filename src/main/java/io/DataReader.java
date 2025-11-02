package io;

import com.google.gson.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataReader {

    public static class GraphData {
        public int n;                // для тестов
        public int nodes;            // для MainAnalyzer
        public List<int[]> edges;
        public double[] weights;

        public GraphData(int n, List<int[]> edges, double[] weights) {
            this.n = n;
            this.nodes = n;
            this.edges = edges;
            this.weights = weights;
        }
    }

    // Метод для тестов
    public static GraphData load(String path) throws IOException {
        return read(path);
    }

    // Метод для MainAnalyzer
    public static GraphData read(String path) throws IOException {
        JsonObject root = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();

        JsonObject meta = root.getAsJsonObject("meta");
        int n = meta.get("nodes").getAsInt();

        JsonObject graphObj = root.getAsJsonObject("graph");
        List<int[]> edges = new ArrayList<>();
        List<Double> weights = new ArrayList<>();

        for (String key : graphObj.keySet()) {
            int u = Integer.parseInt(key);
            JsonArray adjList = graphObj.getAsJsonArray(key);
            for (JsonElement e : adjList) {
                JsonArray pair = e.getAsJsonArray();
                int v = pair.get(0).getAsInt();
                double w = pair.get(1).getAsDouble();
                edges.add(new int[]{u, v});
                weights.add(w);
            }
        }

        double[] weightsArr = new double[weights.size()];
        for (int i = 0; i < weights.size(); i++) weightsArr[i] = weights.get(i);

        return new GraphData(n, edges, weightsArr);
    }
}
