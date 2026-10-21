package experiments;

import connectivity.SCC;
import connectivity.TopologicalSort;
import dsu.DSU;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Benchmark {
    static final int REPEATS = 5;
    static final int WARMUP = 3;
    static final long SEED = 42;
    static final int[][] SIZES = {{10, 30}, {100, 500}, {1000, 5000}};

    interface Task {
        long run();
    }

    static class Row {
        String algorithm;
        int v;
        int e;
        double avgMs;
        long ops;

        Row(String algorithm, int v, int e, double avgMs, long ops) {
            this.algorithm = algorithm;
            this.v = v;
            this.e = e;
            this.avgMs = avgMs;
            this.ops = ops;
        }
    }

    static List<Row> rows = new ArrayList<>();

    static void measure(String name, Graph g, Task task) {
        for (int i = 0; i < WARMUP; i++) task.run();

        long total = 0;
        long ops = 0;
        for (int i = 0; i < REPEATS; i++) {
            long start = System.nanoTime();
            ops = task.run();
            total += System.nanoTime() - start;
        }
        double avgMs = total / (double) REPEATS / 1e6;
        rows.add(new Row(name, g.getN(), g.getEdges().size(), avgMs, ops));
    }

    public static void main(String[] args) throws Exception {
        new File("results/tables").mkdirs();
        new File("results/plots").mkdirs();

        for (int[] size : SIZES) {
            int v = size[0];
            int e = size[1];
            final Graph undirected = GraphGenerator.connectedUndirected(v, e, SEED);
            final Graph dag = GraphGenerator.randomDag(v, e, SEED);
            final Graph cyclic = GraphGenerator.randomDirected(v, e, SEED);

            measure("Kruskal", undirected, () -> Kruskal.run(undirected).totalOps());
            measure("Prim", undirected, () -> Prim.run(undirected).totalOps());
            measure("Topo (DAG)", dag, () -> TopologicalSort.run(dag).edgeExaminations);
            measure("Topo (cyclic)", cyclic, () -> TopologicalSort.run(cyclic).edgeExaminations);
            measure("SCC", cyclic, () -> {
                SCC s = new SCC(cyclic);
                s.findComponents();
                return s.edgeExaminations;
            });
            measure("Dijkstra", undirected, () -> {
                Dijkstra d = new Dijkstra();
                d.run(undirected, 0);
                return d.edgeExaminations + d.comparisons;
            });

            System.out.println("V=" + v + " E=" + e);
            System.out.println("  MST weight: Kruskal=" + Kruskal.run(undirected).totalWeight
                    + " Prim=" + Prim.run(undirected).totalWeight);
            System.out.println("  random DAG is DAG: " + TopologicalSort.run(dag).isDag
                    + ", random directed graph is DAG: " + TopologicalSort.run(cyclic).isDag);
            System.out.println("  SCC count in random directed graph: "
                    + new SCC(cyclic).findComponents().size());
        }

        dsuExperiment();
        writeTables();
        makePlots();
        System.out.println("Done. See the results/ folder.");
    }

    static void dsuExperiment() throws Exception {
        boolean[][] variants = {{false, false}, {true, false}, {false, true}, {true, true}};
        String[] names = {"naive", "path compression", "union by rank", "both"};

        FileWriter csv = new FileWriter("results/tables/dsu.csv");
        csv.write("n,variant,path_steps,avg_ms\n");
        System.out.println("\nDSU experiment:");

        for (int n : new int[]{10, 100, 1000, 10000}) {
            for (int i = 0; i < variants.length; i++) {
                long total = 0;
                long steps = 0;
                for (int r = 0; r < REPEATS; r++) {
                    DSU dsu = new DSU(n, variants[i][0], variants[i][1]);
                    long start = System.nanoTime();
                    for (int k = 0; k < n - 1; k++) dsu.union(k + 1, k);
                    for (int k = 0; k < n; k++) dsu.find(0);
                    total += System.nanoTime() - start;
                    steps = dsu.pathSteps;
                }
                double avgMs = total / (double) REPEATS / 1e6;
                String line = n + "," + names[i] + "," + steps + ","
                        + String.format(Locale.US, "%.4f", avgMs);
                csv.write(line + "\n");
                System.out.println("  " + line);
            }
        }
        csv.close();
    }

    static void writeTables() throws Exception {
        FileWriter csv = new FileWriter("results/tables/results.csv");
        FileWriter md = new FileWriter("results/tables/results.md");
        csv.write("algorithm,V,E,avg_ms,operations\n");
        md.write("| Algorithm | V | E | Avg time (ms) | Operations |\n");
        md.write("|---|---|---|---|---|\n");
        for (Row r : rows) {
            String ms = String.format(Locale.US, "%.4f", r.avgMs);
            csv.write(r.algorithm + "," + r.v + "," + r.e + "," + ms + "," + r.ops + "\n");
            md.write("| " + r.algorithm + " | " + r.v + " | " + r.e + " | " + ms + " | " + r.ops + " |\n");
        }
        csv.close();
        md.close();
    }

    static void makePlots() throws Exception {
        Map<String, List<Row>> byAlgorithm = new LinkedHashMap<>();
        for (Row r : rows) {
            byAlgorithm.computeIfAbsent(r.algorithm, key -> new ArrayList<>()).add(r);
        }
        List<String> names = new ArrayList<>(byAlgorithm.keySet());
        int count = names.size();
        double[][] xV = new double[count][];
        double[][] xE = new double[count][];
        double[][] yTime = new double[count][];
        double[][] yOps = new double[count][];

        for (int i = 0; i < count; i++) {
            List<Row> list = byAlgorithm.get(names.get(i));
            xV[i] = new double[list.size()];
            xE[i] = new double[list.size()];
            yTime[i] = new double[list.size()];
            yOps[i] = new double[list.size()];
            for (int j = 0; j < list.size(); j++) {
                xV[i][j] = list.get(j).v;
                xE[i][j] = list.get(j).e;
                yTime[i][j] = list.get(j).avgMs;
                yOps[i][j] = list.get(j).ops;
            }
        }

        PlotMaker.draw("results/plots/time_vs_vertices.png", "Execution time vs vertices",
                "Vertices (V)", "Time (ms)", names, xV, yTime);
        PlotMaker.draw("results/plots/time_vs_edges.png", "Execution time vs edges",
                "Edges (E)", "Time (ms)", names, xE, yTime);
        PlotMaker.draw("results/plots/operations_vs_vertices.png", "Operations vs vertices",
                "Vertices (V)", "Operations", names, xV, yOps);
    }
}