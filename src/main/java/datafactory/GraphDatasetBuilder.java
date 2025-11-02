// src/datafactory/GraphDatasetBuilder.java
package datafactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GraphDatasetBuilder {
    private static final Random random = new Random();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws IOException {
        generateAll();
        System.out.println("✅ Datasets created in /data/");
    }

    public static void generateAll() throws IOException {
        new java.io.File("data").mkdirs();
        createSet("small", 3, 6, 10);
        createSet("medium", 3, 10, 20);
        createSet("large", 3, 20, 50);
    }

    private static void createSet(String prefix, int count, int minN, int maxN) throws IOException {
        for (int i = 1; i <= count; i++) {
            int n = randBetween(minN, maxN);
            boolean cyclic = (i % 2 == 1);
            double density = 0.25 + random.nextDouble() * 0.5;

            Map<Integer, List<int[]>> graph = makeGraph(n, density, cyclic);
            int edgeCount = countEdges(graph);

            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("nodes", n);
            meta.put("edges", edgeCount);
            meta.put("cyclic", cyclic);

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("meta", meta);
            root.put("graph", graph);

            String filename = String.format("data/%s_%d.json", prefix, i);
            try (FileWriter fw = new FileWriter(filename)) {
                gson.toJson(root, fw);
            }
            System.out.printf("📁 %-12s | n=%d | e=%d | cyclic=%b | dens=%.2f%n",
                    filename, n, edgeCount, cyclic, density);
        }
    }

    private static Map<Integer, List<int[]>> makeGraph(int n, double density, boolean cyclic) {
        Map<Integer, List<int[]>> g = new HashMap<>();
        for (int i = 0; i < n; i++) g.put(i, new ArrayList<>());

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (random.nextDouble() < density) {
                    int w = randBetween(1, 10);
                    if (!cyclic && j <= i) continue;
                    g.get(i).add(new int[]{j, w});
                }
            }
        }

        if (cyclic && n >= 3) {
            int a = random.nextInt(n - 1);
            int b = a + 1;
            g.get(b).add(new int[]{a, randBetween(1, 10)});
        }

        return g;
    }

    private static int countEdges(Map<Integer, List<int[]>> g) {
        int total = 0;
        for (List<int[]> lst : g.values()) total += lst.size();
        return total;
    }

    private static int randBetween(int a, int b) {
        return a + random.nextInt(b - a + 1);
    }
}
