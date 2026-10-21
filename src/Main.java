import connectivity.SCC;
import connectivity.TopologicalSort;
import graph.Edge;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.List;

public class Main {
    static String[] districts = {"Center", "North", "East", "South", "West", "Airport"};
    static String[] services = {"Power", "Water", "Roads", "Internet", "TrafficLights"};

    public static void main(String[] args) {
        Graph roads = new Graph(6, false);
        roads.addEdge(0, 1, 4);
        roads.addEdge(0, 2, 3);
        roads.addEdge(0, 3, 6);
        roads.addEdge(0, 4, 5);
        roads.addEdge(1, 2, 2);
        roads.addEdge(2, 3, 7);
        roads.addEdge(3, 4, 1);
        roads.addEdge(2, 5, 8);
        roads.addEdge(3, 5, 9);

        MSTResult k = Kruskal.run(roads);
        MSTResult p = Prim.run(roads);
        System.out.println("=== Task 1: MST ===");
        System.out.println("Kruskal: weight = " + k.totalWeight + ", connected = " + k.connected);
        System.out.println("Prim:    weight = " + p.totalWeight + ", connected = " + p.connected);
        for (Edge e : k.edges) {
            System.out.println("  " + districts[e.from] + " - " + districts[e.to] + " (" + e.weight + ")");
        }

        Graph deps = new Graph(5, true);
        deps.addEdge(0, 1, 1);
        deps.addEdge(0, 3, 1);
        deps.addEdge(1, 2, 1);
        deps.addEdge(3, 4, 1);
        deps.addEdge(2, 4, 1);

        System.out.println("\n=== Task 2: topological order ===");
        TopologicalSort.Result topo = TopologicalSort.run(deps);
        if (topo.isDag) {
            System.out.print("Build order:");
            for (int v : topo.order) System.out.print(" " + services[v]);
            System.out.println();
        } else {
            System.out.println("Cycle found, no valid order");
        }

        Graph oneWay = new Graph(6, true);
        oneWay.addEdge(0, 1, 2);
        oneWay.addEdge(1, 2, 2);
        oneWay.addEdge(2, 0, 3);
        oneWay.addEdge(2, 3, 4);
        oneWay.addEdge(3, 4, 1);
        oneWay.addEdge(4, 3, 1);
        oneWay.addEdge(4, 5, 5);

        System.out.println("Topological order of one-way roads possible: "
                + TopologicalSort.run(oneWay).isDag);
        List<List<Integer>> comps = new SCC(oneWay).findComponents();
        System.out.println("Strongly connected components: " + comps.size());
        for (List<Integer> comp : comps) {
            System.out.print("  {");
            for (int v : comp) System.out.print(" " + districts[v]);
            System.out.println(" }");
        }

        System.out.println("\n=== Task 3: Dijkstra from South ===");
        Dijkstra d = new Dijkstra();
        d.run(oneWay, 3);
        for (int v = 0; v < oneWay.getN(); v++) {
            if (d.dist[v] == Dijkstra.INF) {
                System.out.println(districts[v] + ": unreachable");
            } else {
                System.out.print(districts[v] + ": " + d.dist[v] + "  path:");
                for (int x : d.getPath(v)) System.out.print(" " + districts[x]);
                System.out.println();
            }
        }
    }
}