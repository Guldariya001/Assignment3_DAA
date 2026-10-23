package daa;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class Benchmark {

    interface Task {
        long run(Graph g, Metrics m);
    }

    static long median(Graph g, Task task) {
        long[] times = new long[5];

        for (int i = 0; i < 5; i++) {
            Metrics metrics = new Metrics();
            long start = System.nanoTime();

            task.run(g, metrics);

            times[i] = System.nanoTime() - start;
        }

        Arrays.sort(times);
        return times[2];
    }

    static void record(PrintWriter out, String name,
                       String graph, int n, int edges,
                       Graph g, Task task) {

        Metrics metrics = new Metrics();
        long result = task.run(g, metrics);
        double ms = median(g, task) / 1e6;

        out.printf(Locale.US,
                "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n",
                name, graph, n, edges, ms,
                metrics.dsuOps, metrics.edgeScans,
                metrics.comparisons, metrics.maxDepth,
                result);
    }

    public static void main(String[] args) throws Exception {
        Path dir = Paths.get("results");
        Files.createDirectories(dir);

        int[] sizes = {1000, 3162, 10000, 31623, 100000};

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(dir.resolve("results.csv")))) {

            out.println("algorithm,graph,n,m,time_ms,dsu_ops,"
                    + "edge_scans,comparisons,max_depth,result");

            for (int n : sizes) {
                for (int factor : new int[]{4, 20}) {
                    int m = n * factor;
                    String label = n + "_" + m;

                    System.out.println("Running " + label);

                    Graph mst = Generator.generate(n, m, "mst");

                    Algorithms.MST a =
                            Algorithms.kruskal(mst, new Metrics());

                    Algorithms.MST b =
                            Algorithms.prim(mst, new Metrics());

                    if (a.weight() != b.weight()
                            || a.edgeCount() != n - 1
                            || b.edgeCount() != n - 1) {
                        throw new AssertionError("MST mismatch");
                    }

                    record(out, "Kruskal", label, n, m, mst,
                            (g, x) -> Algorithms.kruskal(g, x).weight());

                    record(out, "Prim", label, n, m, mst,
                            (g, x) -> Algorithms.prim(g, x).weight());

                    Graph directed =
                            Generator.generate(n, m, "directed");

                    record(out, "Kosaraju", label, n, m, directed,
                            (g, x) -> Algorithms.kosaraju(g, x).count());

                    Graph dag = Generator.generate(n, m, "dag");

                    int[] order = Algorithms.kahn(dag, new Metrics());

                    if (!Algorithms.validOrder(dag, order)) {
                        throw new AssertionError("Invalid order");
                    }

                    record(out, "Kahn", label, n, m, dag,
                            (g, x) -> Algorithms.kahn(g, x).length);

                    record(out, "DagShortestPath", label, n, m, dag,
                            (g, x) -> {
                                long sum = 0;

                                for (long d :
                                        Algorithms.dagShortest(g, x).dist()) {
                                    sum += d;
                                }

                                return sum;
                            });

                    out.flush();
                }
            }
        }

        System.out.println("Done: results/results.csv");
    }
}
