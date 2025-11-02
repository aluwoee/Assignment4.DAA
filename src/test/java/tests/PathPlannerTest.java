package tests;

import graphs.paths.PathPlanner;
import util.MetricsTimer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class PathPlannerTest {
    @Test
    void testLongestPath() {
        Map<Integer, List<int[]>> g = new HashMap<>();
        g.put(0, List.of(new int[]{1, 3}, new int[]{2, 2}));
        g.put(1, List.of(new int[]{3, 4}));
        g.put(2, List.of(new int[]{3, 1}));
        g.put(3, new ArrayList<>());

        List<Integer> topo = List.of(0, 1, 2, 3);
        PathPlanner planner = new PathPlanner(g, new MetricsTimer());
        Map<Integer, Double> dist = planner.longest(0, topo);

        assertEquals(7.0, dist.get(3));
        assertEquals(0.0, dist.get(0));
        assertTrue(dist.get(1) > dist.get(2));
    }
}
