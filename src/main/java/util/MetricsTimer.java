// src/util/MetricsTimer.java
package util;

import java.util.HashMap;
import java.util.Map;

public class MetricsTimer implements Metrics {
    private long start;
    private long elapsed;
    private final Map<String, Long> map = new HashMap<>();

    public void start() { start = System.nanoTime(); }
    public void stop() { elapsed = System.nanoTime() - start; }
    public void inc(String s) { map.put(s, map.getOrDefault(s, 0L) + 1); }
    public long get(String s) { return map.getOrDefault(s, 0L); }
    public long nanos() { return elapsed; }
    public double ms() { return elapsed / 1_000_000.0; }
    public void reset() { map.clear(); elapsed = 0; }
}
