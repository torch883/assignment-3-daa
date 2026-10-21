package mst;

import graph.Edge;
import graph.Graph;
import java.util.PriorityQueue;

public class Prim {

    public static MSTResult run(Graph g) {
        MSTResult result = new MSTResult();
        int n = g.getN();
        final long[] cmp = {0};
        boolean[] visited = new boolean[n];
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;
            components++;

            PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> {
                cmp[0]++;
                return Integer.compare(a.weight, b.weight);
            });

            visited[start] = true;
            addEdges(g, start, visited, pq, result);

            while (!pq.isEmpty()) {
                Edge e = pq.poll();
                if (visited[e.to]) continue;
                visited[e.to] = true;
                result.edges.add(e);
                result.totalWeight += e.weight;
                addEdges(g, e.to, visited, pq, result);
            }
        }

        result.comparisons = cmp[0];
        result.connected = (components == 1);
        return result;
    }

    private static void addEdges(Graph g, int u, boolean[] visited,
                                 PriorityQueue<Edge> pq, MSTResult result) {
        for (Edge e : g.getAdj(u)) {
            result.edgeExaminations++;
            if (!visited[e.to]) {
                pq.add(e);
            }
        }
    }
}