package tests;

import io.DataReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataReaderIntegrationTest {

    @Test
    void testLoadCustomGraphs() throws IOException {
        String[] paths = {
                "test_data/empty.json",
                "test_data/single_node.json",
                "test_data/dense.json",
                "test_data/cycle.json"
        };

        for (String path : paths) {
            DataReader.GraphData data = DataReader.load(path);
            assertNotNull(data, "Graph data should not be null for " + path);

            assertTrue(data.n >= 0, "Number of vertices should be non-negative for " + path);

            assertNotNull(data.edges, "Edges list should not be null for " + path);
            assertTrue(data.edges.size() >= 0, "Number of edges should be non-negative for " + path);

            for (int[] edge : data.edges) {
                assertEquals(2, edge.length, "Each edge should have 2 elements for " + path);
                assertTrue(edge[0] >= 0 && edge[0] < data.n,
                        "Edge source out of bounds for " + path);
                assertTrue(edge[1] >= 0 && edge[1] < data.n,
                        "Edge target out of bounds for " + path);
            }

            assertNotNull(data.weights, "Weights array should not be null for " + path);
            assertEquals(data.edges.size(), data.weights.length,
                    "Weights array length must match number of edges for " + path);
        }
    }

    @Test
    void testFileNotFound() {
        String path = "test_data/non_existent.json";
        assertThrows(IOException.class, () -> DataReader.load(path));
    }
}
