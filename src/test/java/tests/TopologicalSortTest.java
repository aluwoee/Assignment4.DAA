package tests;

import graphs.sorting.TaskSorter;
import util.MetricsTimer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TopologicalSortTest {
    @Test
    void testTopoOrder() {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, List.of(1, 2));
        g.put(1, List.of(3));
        g.put(2, List.of(3));
        g.put(3, List.of());

        TaskSorter sorter = new TaskSorter(g, new MetricsTimer());
        List<Integer> order = sorter.perform();

        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }
}
