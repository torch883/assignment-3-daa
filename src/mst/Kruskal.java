package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MSTResult run(Graph g) {
        MSTResult result = new MSTResult();
        int n = g.getN();
        final long[] cmp = {0};

        List<Edge> sorted = new ArrayList<>(g.getEdges());
        sorted.sort((a, b) -> {
            cmp[0]++;
            return Integer.compare(a.weight, b.weight);
        });
        result.comparisons = cmp[0];

        DSU dsu = new DSU(n);
        for (Edge e : sorted) {
            result.edgeExaminations++;
            if (dsu.union(e.from, e.to)) {
                result.edges.add(e);
                result.totalWeight += e.weight;
                if (result.edges.size() == n - 1) break;
            }
        }

        result.connected = (dsu.getComponents() == 1);
        result.dsuOps = dsu.findCalls + dsu.unionCalls;
        return result;
    }
}