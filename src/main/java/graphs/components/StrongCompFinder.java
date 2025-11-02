// src/graphs/components/StrongCompFinder.java
package graphs.components;

import util.Metrics;
import java.util.*;

public class StrongCompFinder {
    private final Map<Integer, List<Integer>> g;
    private final Metrics m;
    private final Map<Integer, Integer> id = new HashMap<>();
    private final Map<Integer, Integer> low = new HashMap<>();
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Set<Integer> inStack = new HashSet<>();
    private final List<List<Integer>> out = new ArrayList<>();
    private int idx = 0;

    public StrongCompFinder(Map<Integer, List<Integer>> g, Metrics m) {
        this.g = g; this.m = m;
    }

    public List<List<Integer>> execute() {
        m.start();
        for (int node : g.keySet()) if (!id.containsKey(node)) dfs(node);
        m.stop();
        return out;
    }

    private void dfs(int u) {
        m.inc("dfsV");
        id.put(u, idx);
        low.put(u, idx++);
        stack.push(u);
        inStack.add(u);
        for (int v : g.getOrDefault(u, List.of())) {
            m.inc("dfsE");
            if (!id.containsKey(v)) {
                dfs(v);
                low.put(u, Math.min(low.get(u), low.get(v)));
            } else if (inStack.contains(v)) {
                low.put(u, Math.min(low.get(u), id.get(v)));
            }
        }
        if (Objects.equals(id.get(u), low.get(u))) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int x = stack.pop();
                inStack.remove(x);
                comp.add(x);
                if (x == u) break;
            }
            out.add(comp);
        }
    }
}
