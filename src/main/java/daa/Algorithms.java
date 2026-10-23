package daa;

import java.util.*;

public class Algorithms {

    public record MST(long weight, int edgeCount,
                      int components) {}

    public static MST kruskal(Graph g, Metrics m) {
        if (g.directed)
            throw new IllegalArgumentException("Undirected only");

        ArrayList<Graph.Edge> edges =
                new ArrayList<>(g.edges);

        edges.sort((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.w(), b.w());
        });

        DSU dsu = new DSU(g.n);
        long total = 0;
        int used = 0;

        for (Graph.Edge e : edges) {
            m.edgeScans++;

            if (dsu.union(e.u(), e.v())) {
                total += e.w();
                used++;
            }
        }

        m.dsuOps = dsu.operations();
        return new MST(total, used, dsu.components());
    }

    public static MST prim(Graph g, Metrics m) {
        if (g.directed)
            throw new IllegalArgumentException("Undirected only");

        boolean[] visited = new boolean[g.n];

        PriorityQueue<Graph.Edge> pq =
                new PriorityQueue<>((a, b) -> {
                    m.comparisons++;
                    return Integer.compare(a.w(), b.w());
                });

        long total = 0;
        int used = 0;
        int components = 0;

        for (int start = 0; start < g.n; start++) {
            if (visited[start]) continue;

            components++;
            pq.add(new Graph.Edge(-1, start, 0));

            while (!pq.isEmpty()) {
                Graph.Edge e = pq.poll();
                int v = e.v();

                if (visited[v]) continue;
                visited[v] = true;

                if (e.u() != -1) {
                    total += e.w();
                    used++;
                }

                for (Graph.Edge next : g.adj[v]) {
                    m.edgeScans++;

                    if (!visited[next.v()]) {
                        pq.add(next);
                    }
                }
            }
        }

        return new MST(total, used, components);
    }

    public record SCC(int count, int[] component) {}

    private static class Frame {
        int v;
        int index;

        Frame(int v) {
            this.v = v;
            this.index = 0;
        }
    }

    public static SCC kosaraju(Graph g, Metrics m) {
        int n = g.n;
        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int k = 0;

        ArrayDeque<Frame> stack = new ArrayDeque<>();

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;

            visited[start] = true;
            stack.push(new Frame(start));
            m.depth(stack.size());

            while (!stack.isEmpty()) {
                Frame f = stack.peek();

                if (f.index < g.adj[f.v].size()) {
                    Graph.Edge e =
                            g.adj[f.v].get(f.index++);
                    m.edgeScans++;

                    if (!visited[e.v()]) {
                        visited[e.v()] = true;
                        stack.push(new Frame(e.v()));
                        m.depth(stack.size());
                    }
                } else {
                    order[k++] = f.v;
                    stack.pop();
                }
            }
        }

        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] reverse = new ArrayList[n];

        for (int i = 0; i < n; i++) {
            reverse[i] = new ArrayList<>();
        }

        for (Graph.Edge e : g.edges) {
            reverse[e.v()].add(e.u());

            if (!g.directed) {
                reverse[e.u()].add(e.v());
            }
        }

        int[] component = new int[n];
        Arrays.fill(component, -1);

        int count = 0;
        ArrayDeque<Integer> s = new ArrayDeque<>();

        for (int i = n - 1; i >= 0; i--) {
            int v = order[i];

            if (component[v] != -1) continue;

            component[v] = count;
            s.push(v);
            m.depth(s.size());

            while (!s.isEmpty()) {
                int u = s.pop();

                for (int w : reverse[u]) {
                    m.edgeScans++;

                    if (component[w] == -1) {
                        component[w] = count;
                        s.push(w);
                        m.depth(s.size());
                    }
                }
            }
            count++;
        }

        return new SCC(count, component);
    }

    public static int[] kahn(Graph g, Metrics m) {
        int[] indegree = new int[g.n];

        for (Graph.Edge e : g.edges) {
            indegree[e.v()]++;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < g.n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        int[] order = new int[g.n];
        int count = 0;

        while (!queue.isEmpty()) {
            int v = queue.remove();
            order[count++] = v;

            for (Graph.Edge e : g.adj[v]) {
                m.edgeScans++;
                indegree[e.v()]--;

                if (indegree[e.v()] == 0) {
                    queue.add(e.v());
                }
            }
        }

        if (count != g.n) {
            throw new IllegalArgumentException(
                    "Graph contains a cycle");
        }

        return order;
    }

    public static boolean validOrder(Graph g, int[] order) {
        if (order.length != g.n) return false;

        int[] pos = new int[g.n];
        Arrays.fill(pos, -1);

        for (int i = 0; i < order.length; i++) {
            int v = order[i];

            if (v < 0 || v >= g.n || pos[v] != -1)
                return false;

            pos[v] = i;
        }

        for (Graph.Edge e : g.edges) {
            if (pos[e.u()] >= pos[e.v()])
                return false;
        }

        return true;
    }

    public record Paths(long[] dist, int[] parent) {
        public List<Integer> pathTo(int v) {
            ArrayList<Integer> path = new ArrayList<>();

            for (int i = v; i != -1; i = parent[i]) {
                path.add(i);
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Paths dagShortest(Graph g, Metrics m) {
        int[] order = kahn(g, new Metrics());
        int[] indegree = new int[g.n];

        for (Graph.Edge e : g.edges) {
            indegree[e.v()]++;
        }

        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[g.n];
        int[] parent = new int[g.n];

        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);

        for (int i = 0; i < g.n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int u : order) {
            for (Graph.Edge e : g.adj[u]) {
                m.edgeScans++;

                if (dist[u] != INF &&
                        dist[u] + e.w() < dist[e.v()]) {

                    dist[e.v()] = dist[u] + e.w();
                    parent[e.v()] = u;
                }
            }
        }

        return new Paths(dist, parent);
    }
}
