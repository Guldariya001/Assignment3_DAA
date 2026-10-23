package daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class AlgorithmsTest {

    @Test
    void testDSU() {
        DSU dsu = new DSU(5);

        dsu.union(0, 1);
        dsu.union(1, 2);

        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
        assertEquals(3, dsu.components());
    }

    @Test
    void testMST() {
        Random rnd = new Random(42);

        for (int t = 0; t < 100; t++) {
            int n = 5 + rnd.nextInt(20);
            Graph g = new Graph(n, false);

            for (int i = 1; i < n; i++) {
                g.add(i, rnd.nextInt(i),
                        1 + rnd.nextInt(100));
            }

            for (int i = 0; i < n * 3; i++) {
                g.add(rnd.nextInt(n), rnd.nextInt(n),
                        1 + rnd.nextInt(100));
            }

            Algorithms.MST a =
                    Algorithms.kruskal(g, new Metrics());

            Algorithms.MST b =
                    Algorithms.prim(g, new Metrics());

            assertEquals(a.weight(), b.weight());
            assertEquals(n - 1, a.edgeCount());
        }
    }

    @Test
    void testDisconnected() {
        Graph g = new Graph(5, false);
        g.add(0, 1, 3);
        g.add(2, 3, 2);

        assertEquals(3,
                Algorithms.kruskal(g, new Metrics()).components());

        assertEquals(5,
                Algorithms.prim(g, new Metrics()).weight());
    }

    @Test
    void testSCC() {
        Graph g = new Graph(4, true);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(2, 0, 1);
        g.add(2, 3, 1);

        Algorithms.SCC result =
                Algorithms.kosaraju(g, new Metrics());

        assertEquals(2, result.count());
        assertEquals(result.component()[0],
                result.component()[2]);
    }

    @Test
    void testTopologicalOrder() {
        Graph g = new Graph(4, true);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(0, 3, 1);

        int[] order = Algorithms.kahn(g, new Metrics());

        assertTrue(Algorithms.validOrder(g, order));
    }

    @Test
    void testCycle() {
        Graph g = new Graph(3, true);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(2, 0, 1);

        assertThrows(IllegalArgumentException.class,
                () -> Algorithms.kahn(g, new Metrics()));
    }

    @Test
    void testDAGShortestPath() {
        Graph g = new Graph(4, true);
        g.add(0, 1, 3);
        g.add(1, 2, -5);
        g.add(0, 2, 4);
        g.add(2, 3, 2);

        Algorithms.Paths result =
                Algorithms.dagShortest(g, new Metrics());

        assertEquals(-2, result.dist()[2]);
        assertEquals(0, result.dist()[3]);

        assertEquals(Arrays.asList(0, 1, 2, 3),
                result.pathTo(3));
    }

    @Test
    void testStackSafety() {
        Graph g = new Graph(100000, true);

        for (int i = 0; i < 99999; i++) {
            g.add(i, i + 1, 1);
        }

        assertEquals(100000,
                Algorithms.kosaraju(g, new Metrics()).count());
    }
}
