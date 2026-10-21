package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int n;
    private boolean directed;
    private List<List<Edge>> adj;
    private List<Edge> edges;

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int weight) {
        Edge e = new Edge(u, v, weight);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed) {
            adj.get(v).add(new Edge(v, u, weight));
        }
    }

    public int getN() { return n; }

    public boolean isDirected() { return directed; }

    public List<Edge> getAdj(int u) { return adj.get(u); }

    public List<Edge> getEdges() { return edges; }
}