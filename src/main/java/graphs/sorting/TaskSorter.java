// src/graphs/sorting/TaskSorter.java
package graphs.sorting;

import util.Metrics;
import java.util.*;

public class TaskSorter {
    private final Map<Integer, List<Integer>> g;
    private final Metrics m;

    public TaskSorter(Map<Integer, List<Integer>> g, Metrics m) {
        this.g = g; this.m = m;
    }

    public List<Integer> perform() {
        m.start();
        Map<Integer, Integer> indeg = new HashMap<>();
        for (int k : g.keySet()) indeg.put(k, 0);
        for (List<Integer> lst : g.values())
            for (int v : lst)
                indeg.put(v, indeg.getOrDefault(v, 0) + 1);

        Queue<Integer> q = new ArrayDeque<>();
        for (var e : indeg.entrySet())
            if (e.getValue() == 0) q.add(e.getKey());

        List<Integer> res = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll(); m.inc("qPop");
            res.add(u);
            for (int v : g.getOrDefault(u, List.of())) {
                indeg.put(v, indeg.get(v) - 1);
                m.inc("qPush");
                if (indeg.get(v) == 0) q.add(v);
            }
        }
        m.stop();
        return res;
    }
}
