package mst;

import graph.Edge;
import java.util.ArrayList;
import java.util.List;

public class MSTResult {
    public List<Edge> edges = new ArrayList<>();
    public long totalWeight = 0;
    public boolean connected;

    public long edgeExaminations = 0;
    public long comparisons = 0;
    public long dsuOps = 0;

    public long totalOps() {
        return edgeExaminations + comparisons + dsuOps;
    }
}