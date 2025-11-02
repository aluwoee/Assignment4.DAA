// src/util/Metrics.java
package util;

public interface Metrics {
    void start();
    void stop();
    void inc(String name);
    long get(String name);
    long nanos();
    double ms();
    void reset();
}
