package shortestpath;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static final long INF = Long.MAX_VALUE;

    public long[] dist;
    public int[] prev;
    public long edgeExaminations = 0;
    public long comparisons = 0;

    public void run(Graph g, int source) {
        int n = g.getN();

        for (int u = 0; u < n; u++) {
            for (Edge e : g.getAdj(u)) {
                if (e.weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative edge weight found: Dijkstra cannot be used");
                }
            }
        }

        dist = new long[n];
        prev = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        edgeExaminations = 0;
        comparisons = 0;

        dist[source] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> {
            comparisons++;
            return Long.compare(a[0], b[0]);
        });
        pq.add(new long[]{0, source});

        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            int u = (int) cur[1];
            if (cur[0] > dist[u]) continue;

            for (Edge e : g.getAdj(u)) {
                edgeExaminations++;
                long newDist = dist[u] + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    prev[e.to] = u;
                    pq.add(new long[]{newDist, e.to});
                }
            }
        }
    }

    public List<Integer> getPath(int target) {
        List<Integer> path = new ArrayList<>();
        if (dist[target] == INF) return path;
        for (int v = target; v != -1; v = prev[v]) {
            path.add(v);
        }
        Collections.reverse(path);
        return path;
    }
}