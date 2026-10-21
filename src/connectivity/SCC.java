package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;

public class SCC {
    private Graph g;
    private boolean[] visited;
    private List<Integer> finishOrder;
    public long edgeExaminations = 0;

    public SCC(Graph g) {
        this.g = g;
    }

    public List<List<Integer>> findComponents() {
        int n = g.getN();
        edgeExaminations = 0;

        visited = new boolean[n];
        finishOrder = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfs1(v);
        }

        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < n; i++) rev.add(new ArrayList<>());
        for (Edge e : g.getEdges()) {
            rev.get(e.to).add(e.from);
        }

        visited = new boolean[n];
        List<List<Integer>> components = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            int v = finishOrder.get(i);
            if (!visited[v]) {
                List<Integer> comp = new ArrayList<>();
                dfs2(v, rev, comp);
                components.add(comp);
            }
        }
        return components;
    }

    private void dfs1(int u) {
        visited[u] = true;
        for (Edge e : g.getAdj(u)) {
            edgeExaminations++;
            if (!visited[e.to]) dfs1(e.to);
        }
        finishOrder.add(u);
    }

    private void dfs2(int u, List<List<Integer>> rev, List<Integer> comp) {
        visited[u] = true;
        comp.add(u);
        for (int w : rev.get(u)) {
            edgeExaminations++;
            if (!visited[w]) dfs2(w, rev, comp);
        }
    }
}