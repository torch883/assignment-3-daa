package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TopologicalSort {

    public static class Result {
        public List<Integer> order = new ArrayList<>();
        public boolean isDag;
        public long edgeExaminations = 0;
    }

    public static Result run(Graph g) {
        int n = g.getN();
        Result result = new Result();

        int[] indegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Edge e : g.getAdj(u)) {
                indegree[e.to]++;
                result.edgeExaminations++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            if (indegree[v] == 0) queue.add(v);
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.order.add(u);
            for (Edge e : g.getAdj(u)) {
                result.edgeExaminations++;
                indegree[e.to]--;
                if (indegree[e.to] == 0) queue.add(e.to);
            }
        }

        result.isDag = (result.order.size() == n);
        return result;
    }

    public static boolean isValidOrder(Graph g, List<Integer> order) {
        if (order.size() != g.getN()) return false;
        int[] pos = new int[g.getN()];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (Edge e : g.getEdges()) {
            if (pos[e.from] >= pos[e.to]) return false;
        }
        return true;
    }
}