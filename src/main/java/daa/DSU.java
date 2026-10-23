package daa;

public class DSU {
    private int[] parent;
    private int[] size;
    private int count;

    public long finds = 0;
    public long unions = 0;

    public DSU(int n) {
        parent = new int[n];
        size = new int[n];
        count = n;

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public static DSU makeSet(int n) {
        return new DSU(n);
    }

    public int find(int x) {
        finds++;

        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }

        return parent[x];
    }

    public boolean union(int a, int b) {
        a = find(a);
        b = find(b);

        if (a == b) {
            return false;
        }

        if (size[a] < size[b]) {
            int temp = a;
            a = b;
            b = temp;
        }

        parent[b] = a;
        size[a] += size[b];
        count--;
        unions++;

        return true;
    }

    public int components() {
        return count;
    }

    public long operations() {
        return finds + unions;
    }
}
