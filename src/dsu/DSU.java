package dsu;

public class DSU {
    private int[] parent;
    private int[] rank;
    private int components;
    private boolean pathCompression;
    private boolean unionByRank;

    public long findCalls = 0;
    public long unionCalls = 0;
    public long pathSteps = 0;

    public DSU(int n) {
        this(n, true, true);
    }

    public DSU(int n, boolean pathCompression, boolean unionByRank) {
        this.pathCompression = pathCompression;
        this.unionByRank = unionByRank;
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
        components = n;
    }

    public int find(int x) {
        findCalls++;
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
            pathSteps++;
        }
        if (pathCompression) {
            while (parent[x] != root) {
                int next = parent[x];
                parent[x] = root;
                x = next;
            }
        }
        return root;
    }

    public boolean union(int a, int b) {
        unionCalls++;
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) return false;

        if (unionByRank) {
            if (rank[ra] < rank[rb]) {
                int t = ra; ra = rb; rb = t;
            }
            parent[rb] = ra;
            if (rank[ra] == rank[rb]) rank[ra]++;
        } else {
            parent[rb] = ra;
        }
        components--;
        return true;
    }

    public int getComponents() {
        return components;
    }
}