package tests;

import connectivity.SCC;
import connectivity.TopologicalSort;
import dsu.DSU;
import graph.Edge;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.Arrays;
import java.util.List;

public class Tests {
    static int passed = 0;
    static int failed = 0;

    static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }

    public static void main(String[] args) {
        testDSU();
        testMST();
        testTopological();
        testSCC();
        testDijkstra();
        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    static void testDSU() {
        DSU d = new DSU(6);
        check("DSU: 6 components at start", d.getComponents() == 6);
        check("DSU: union(0,1) merges", d.union(0, 1));
        d.union(2, 3);
        d.union(1, 3);
        check("DSU: 0 and 3 in the same set", d.find(0) == d.find(3));
        check("DSU: 4 is separate", d.find(4) != d.find(0));
        check("DSU: repeated union returns false", !d.union(0, 2));
        check("DSU: 3 components left", d.getComponents() == 3);
    }

    static void checkMST(String name, Graph g, long weight, boolean connected) {
        MSTResult k = Kruskal.run(g);
        MSTResult p = Prim.run(g);
        check(name + ": Kruskal weight = " + weight, k.totalWeight == weight);
        check(name + ": Prim weight = " + weight, p.totalWeight == weight);
        check(name + ": connected flag", k.connected == connected && p.connected == connected);
    }

    static void testMST() {
        Graph small = new Graph(4, false);
        small.addEdge(0, 1, 1);
        small.addEdge(1, 2, 2);
        small.addEdge(2, 3, 3);
        small.addEdge(0, 3, 10);
        small.addEdge(0, 2, 4);
        checkMST("MST small", small, 6, true);

        Graph disc = new Graph(4, false);
        disc.addEdge(0, 1, 1);
        disc.addEdge(2, 3, 2);
        checkMST("MST disconnected", disc, 3, false);

        Graph triangle = new Graph(3, false);
        triangle.addEdge(0, 1, 1);
        triangle.addEdge(1, 2, 1);
        triangle.addEdge(0, 2, 1);
        checkMST("MST multiple MSTs", triangle, 2, true);

        Graph square = new Graph(4, false);
        square.addEdge(0, 1, 5);
        square.addEdge(1, 2, 5);
        square.addEdge(2, 3, 5);
        square.addEdge(3, 0, 5);
        checkMST("MST duplicate weights", square, 15, true);

        checkMST("MST single vertex", new Graph(1, false), 0, true);

        boolean allEqual = true;
        for (int seed = 1; seed <= 30; seed++) {
            Graph g = GraphGenerator.connectedUndirected(50, 200, seed);
            MSTResult k = Kruskal.run(g);
            MSTResult p = Prim.run(g);
            if (k.totalWeight != p.totalWeight || !k.connected || !p.connected
                    || k.edges.size() != 49 || p.edges.size() != 49) {
                allEqual = false;
            }
        }
        check("MST: Kruskal == Prim on 30 random graphs", allEqual);
    }

    static void testTopological() {
        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 3, 1);
        dag.addEdge(2, 3, 1);
        TopologicalSort.Result r = TopologicalSort.run(dag);
        check("Topo: DAG detected", r.isDag);
        check("Topo: order is valid", TopologicalSort.isValidOrder(dag, r.order));

        Graph cycle = new Graph(3, true);
        cycle.addEdge(0, 1, 1);
        cycle.addEdge(1, 2, 1);
        cycle.addEdge(2, 0, 1);
        check("Topo: cycle detected", !TopologicalSort.run(cycle).isDag);

        TopologicalSort.Result single = TopologicalSort.run(new Graph(1, true));
        check("Topo: single vertex", single.isDag && single.order.size() == 1);

        boolean allValid = true;
        for (int seed = 1; seed <= 30; seed++) {
            Graph g = GraphGenerator.randomDag(40, 150, seed);
            TopologicalSort.Result res = TopologicalSort.run(g);
            if (!res.isDag || !TopologicalSort.isValidOrder(g, res.order)) allValid = false;
        }
        check("Topo: valid order on 30 random DAGs", allValid);
    }

    static boolean eachVertexOnce(List<List<Integer>> comps, int n) {
        int[] count = new int[n];
        for (List<Integer> comp : comps) {
            for (int v : comp) count[v]++;
        }
        for (int c : count) {
            if (c != 1) return false;
        }
        return true;
    }

    static void testSCC() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 1);
        g.addEdge(2, 0, 1);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 1);
        g.addEdge(4, 3, 1);
        List<List<Integer>> comps = new SCC(g).findComponents();
        check("SCC: 2 components", comps.size() == 2);
        check("SCC: every vertex in exactly one component", eachVertexOnce(comps, 5));
        boolean has3 = false;
        boolean has2 = false;
        for (List<Integer> c : comps) {
            if (c.size() == 3) has3 = true;
            if (c.size() == 2) has2 = true;
        }
        check("SCC: sizes are 3 and 2", has3 && has2);

        Graph dag = new Graph(4, true);
        dag.addEdge(0, 1, 1);
        dag.addEdge(1, 2, 1);
        dag.addEdge(2, 3, 1);
        check("SCC: DAG gives n components", new SCC(dag).findComponents().size() == 4);

        check("SCC: single vertex", new SCC(new Graph(1, true)).findComponents().size() == 1);

        boolean ok = true;
        for (int seed = 1; seed <= 30; seed++) {
            Graph r = GraphGenerator.randomDirected(100, 300, seed);
            if (!eachVertexOnce(new SCC(r).findComponents(), 100)) ok = false;
        }
        check("SCC: partition is correct on 30 random graphs", ok);
    }

    static long[] bellmanFord(Graph g, int s) {
        int n = g.getN();
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[s] = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int u = 0; u < n; u++) {
                if (dist[u] == Long.MAX_VALUE) continue;
                for (Edge e : g.getAdj(u)) {
                    if (dist[u] + e.weight < dist[e.to]) {
                        dist[e.to] = dist[u] + e.weight;
                    }
                }
            }
        }
        return dist;
    }

    static void testDijkstra() {
        Graph g = new Graph(5, true);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 5);
        Dijkstra d = new Dijkstra();
        d.run(g, 0);
        check("Dijkstra: dist to 1 is 3", d.dist[1] == 3);
        check("Dijkstra: dist to 2 is 1", d.dist[2] == 1);
        check("Dijkstra: dist to 3 is 4", d.dist[3] == 4);
        check("Dijkstra: vertex 4 unreachable", d.dist[4] == Dijkstra.INF);
        check("Dijkstra: path to 3", d.getPath(3).equals(Arrays.asList(0, 2, 1, 3)));
        check("Dijkstra: no path to 4", d.getPath(4).isEmpty());

        Graph neg = new Graph(3, true);
        neg.addEdge(0, 1, 2);
        neg.addEdge(0, 2, 5);
        neg.addEdge(2, 1, -4);
        boolean thrown = false;
        try {
            new Dijkstra().run(neg, 0);
        } catch (IllegalArgumentException ex) {
            thrown = true;
        }
        check("Dijkstra: negative weight rejected", thrown);

        Dijkstra one = new Dijkstra();
        one.run(new Graph(1, false), 0);
        check("Dijkstra: single vertex", one.dist[0] == 0);

        boolean same = true;
        for (int seed = 1; seed <= 30; seed++) {
            Graph r = GraphGenerator.connectedUndirected(60, 200, seed);
            Dijkstra dj = new Dijkstra();
            dj.run(r, 0);
            if (!Arrays.equals(dj.dist, bellmanFord(r, 0))) same = false;
        }
        check("Dijkstra == Bellman-Ford on 30 random graphs", same);
    }
}