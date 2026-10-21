package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GraphGenerator {

    private static int randomWeight(Random rnd) {
        return rnd.nextInt(100) + 1;
    }

    private static long key(int a, int b) {
        return a * 1000000L + b;
    }

    public static Graph connectedUndirected(int n, int m, long seed) {
        if (m < n - 1 || m > (long) n * (n - 1) / 2) {
            throw new IllegalArgumentException("Impossible number of edges");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, false);
        Set<Long> used = new HashSet<>();

        for (int i = 1; i < n; i++) {
            int j = rnd.nextInt(i);
            g.addEdge(i, j, randomWeight(rnd));
            used.add(key(Math.min(i, j), Math.max(i, j)));
        }
        while (g.getEdges().size() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v) continue;
            long k = key(Math.min(u, v), Math.max(u, v));
            if (used.contains(k)) continue;
            used.add(k);
            g.addEdge(u, v, randomWeight(rnd));
        }
        return g;
    }

    public static Graph randomDag(int n, int m, long seed) {
        if (m > (long) n * (n - 1) / 2) {
            throw new IllegalArgumentException("Too many edges for a DAG");
        }
        Random rnd = new Random(seed);
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < n; i++) order.add(i);
        Collections.shuffle(order, rnd);
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[order.get(i)] = i;

        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdges().size() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            if (a == b) continue;
            if (pos[a] > pos[b]) {
                int t = a; a = b; b = t;
            }
            long k = key(a, b);
            if (used.contains(k)) continue;
            used.add(k);
            g.addEdge(a, b, randomWeight(rnd));
        }
        return g;
    }

    public static Graph randomDirected(int n, int m, long seed) {
        if (m > (long) n * (n - 1)) {
            throw new IllegalArgumentException("Too many edges");
        }
        Random rnd = new Random(seed);
        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.getEdges().size() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (u == v) continue;
            long k = key(u, v);
            if (used.contains(k)) continue;
            used.add(k);
            g.addEdge(u, v, randomWeight(rnd));
        }
        return g;
    }
}