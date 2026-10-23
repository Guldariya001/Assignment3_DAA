package daa;

public class Metrics {
    public long dsuOps = 0;
    public long edgeScans = 0;
    public long comparisons = 0;
    public int maxDepth = 0;

    public void depth(int d) {
        maxDepth = Math.max(maxDepth, d);
    }
}
