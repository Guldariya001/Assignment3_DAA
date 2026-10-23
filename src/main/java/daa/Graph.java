package daa;

import java.util.*;
import java.nio.file.*;

public class Graph {
    public record Edge(int u, int v, int w) {}

    public final int n;
    public final boolean directed;
    public final ArrayList<Edge>[] adj;
    public final ArrayList<Edge> edges = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        adj = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    public void add(int u, int v, int w) {
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj[u].add(e);

        if (!directed) {
            adj[v].add(new Edge(v, u, w));
        }
    }

    public static Graph read(Path path) throws Exception {
        List<String> lines = Files.readAllLines(path);
        String[] first = lines.get(0).trim().split("\\s+");

        int n = Integer.parseInt(first[0]);
        int m = Integer.parseInt(first[1]);
        boolean directed = first[2].equals("directed");

        Graph g = new Graph(n, directed);

        for (int i = 1; i <= m; i++) {
            String[] a = lines.get(i).trim().split("\\s+");
            g.add(Integer.parseInt(a[0]),
                    Integer.parseInt(a[1]),
                    Integer.parseInt(a[2]));
        }
        return g;
    }
}
