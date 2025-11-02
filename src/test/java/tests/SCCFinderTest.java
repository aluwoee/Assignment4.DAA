package tests;

import graphs.components.StrongCompFinder;
import util.MetricsTimer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class SCCFinderTest {
    @Test
    void testTwoComponents() {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, List.of(1));
        g.put(1, List.of(0, 2));
        g.put(2, List.of(3));
        g.put(3, List.of());

        StrongCompFinder finder = new StrongCompFinder(g, new MetricsTimer());
        List<List<Integer>> comps = finder.execute();

        assertEquals(2, comps.size());
        boolean hasCycle = comps.stream().anyMatch(c -> c.size() > 1);
        assertTrue(hasCycle);
    }
}
