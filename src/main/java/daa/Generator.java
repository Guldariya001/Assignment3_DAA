
package daa;

import java.util.*;

public class Generator {

    public static Graph generate(int n, int m, String kind) {
        Random rnd = new Random(42);
        boolean undirected = kind.equals("mst");

        Graph g = new Graph(n, !undirected);

        int[] rank = new int[n];

        for (int i = 0; i < n; i++) {
            rank[i] = i;
        }

        if (undirected || kind.equals("dag")) {
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);

                int temp = rank[i];
                rank[i] = rank[j];
                rank[j] = temp;
            }
        }

        if (undirected) {
            for (int i = 1; i < n; i++) {
                int u = rank[rnd.nextInt(i)];
                int v = rank[i];

                g.add(u, v, 1 + rnd.nextInt(1000));
            }
        }

        while (g.edges.size() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);

            while (a == b) {
                b = rnd.nextInt(n);
            }


            if (kind.equals("dag") && rank[a] > rank[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            g.add(a, b, 1 + rnd.nextInt(1000));
        }

        return g;
    }
}
