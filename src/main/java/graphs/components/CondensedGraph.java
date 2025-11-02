// src/graphs/components/CondensedGraph.java
package graphs.components;

import java.util.*;

public class CondensedGraph {
    private final List<List<Integer>> adj;
    private final double[] compWeights;
    private final int count;

    public CondensedGraph(List<List<Integer>> origAdj, List<List<Integer>> comps, double[] nodeWeights) {
        int n = origAdj.size();
        this.count = comps.size();
        int[] belong = new int[n];
        for (int i = 0; i < comps.size(); i++) {
            for (int v : comps.get(i)) belong[v] = i;
        }

        adj = new ArrayList<>(count);
        for (int i = 0; i < count; i++) adj.add(new ArrayList<>());

        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < n; u++) {
            for (int v : origAdj.get(u)) {
                int a = belong[u], b = belong[v];
                if (a != b) {
                    long key = ((long)a << 32) | (b & 0xffffffffL);
                    if (seen.add(key)) adj.get(a).add(b);
                }
            }
        }

        compWeights = new double[count];
        Arrays.fill(compWeights, 0.0);
        for (int i = 0; i < comps.size(); i++) {
            for (int v : comps.get(i)) compWeights[i] += nodeWeights[v];
        }
    }

    public List<List<Integer>> getAdj() { return adj; }
    public double[] getCompWeights() { return compWeights; }
    public int getCount() { return count; }
}
