// src/graphs/paths/PathPlanner.java
package graphs.paths;

import util.Metrics;
import java.util.*;

public class PathPlanner {
    private final Map<Integer, List<int[]>> g;
    private final Metrics m;

    public PathPlanner(Map<Integer, List<int[]>> g, Metrics m) {
        this.g = g; this.m = m;
    }

    public Map<Integer, Double> shortest(int s, List<Integer> topo) {
        m.start();
        Map<Integer, Double> dist = new HashMap<>();
        for (int k : g.keySet()) dist.put(k, Double.POSITIVE_INFINITY);
        dist.put(s, 0.0);
        for (int u : topo) {
            if (dist.get(u) != Double.POSITIVE_INFINITY) {
                for (int[] e : g.getOrDefault(u, List.of())) {
                    int v = e[0]; double w = e[1];
                    m.inc("relax");
                    if (dist.get(u) + w < dist.get(v))
                        dist.put(v, dist.get(u) + w);
                }
            }
        }
        m.stop();
        return dist;
    }

    public Map<Integer, Double> longest(int s, List<Integer> topo) {
        m.start();
        Map<Integer, Double> dist = new HashMap<>();
        for (int k : g.keySet()) dist.put(k, Double.NEGATIVE_INFINITY);
        dist.put(s, 0.0);
        for (int u : topo) {
            if (dist.get(u) != Double.NEGATIVE_INFINITY) {
                for (int[] e : g.getOrDefault(u, List.of())) {
                    int v = e[0]; double w = e[1];
                    m.inc("relax");
                    if (dist.get(u) + w > dist.get(v))
                        dist.put(v, dist.get(u) + w);
                }
            }
        }
        m.stop();
        return dist;
    }
}
