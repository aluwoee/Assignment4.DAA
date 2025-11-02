// src/app/MainAnalyzer.java
package app;

import graphs.components.StrongCompFinder;
import graphs.components.CondensedGraph;
import graphs.sorting.TaskSorter;
import graphs.paths.PathPlanner;
import io.DataReader;
import io.CsvReport;
import util.MetricsTimer;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainAnalyzer {
    public static void main(String[] args) {
        String inputDir = "data";
        String outputFile = "data/results/analytics.csv";
        CsvReport csv = new CsvReport(outputFile);
        try { csv.createHeader(); } catch (IOException e) { return; }

        File dir = new File(inputDir);
        File[] jsons = dir.listFiles((d, n) -> n.endsWith(".json"));
        if (jsons == null || jsons.length == 0) return;
        Arrays.sort(jsons);

        int id = 1;
        for (File file : jsons) {
            System.out.println("Processing " + file.getName());
            try {
                DataReader.GraphData g = DataReader.read(file.getPath());
                int n = g.nodes;
                int m = g.edges.size();

                Map<Integer, List<Integer>> adj = new HashMap<>();
                Map<Integer, List<int[]>> weighted = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    adj.put(i, new ArrayList<>());
                    weighted.put(i, new ArrayList<>());
                }
                for (int[] e : g.edges) {
                    int u = e[0], v = e[1];
                    int w = (e.length >= 3) ? e[2] : 1;
                    adj.get(u).add(v);
                    weighted.get(u).add(new int[]{v, w});
                }

                MetricsTimer sccM = new MetricsTimer();
                StrongCompFinder finder = new StrongCompFinder(adj, sccM);
                sccM.start();
                List<List<Integer>> comps = finder.execute();
                sccM.stop();
                boolean cyclic = comps.stream().anyMatch(c -> c.size() > 1);
                long sccOps = sccM.get("dfsV") + sccM.get("dfsE");
                double sccTime = sccM.ms();
                csv.addRow(file.getName(), id, n, m, cyclic, "SCC", sccTime, (int)sccOps, null);

                double[] nodeDur = new double[n];
                Arrays.fill(nodeDur, 1.0);
                CondensedGraph cond = new CondensedGraph(
                        new ArrayList<>(adj.values()),
                        comps,
                        nodeDur
                );
                List<List<Integer>> compAdj = cond.getAdj();
                int compCount = cond.getCount();

                MetricsTimer topoM = new MetricsTimer();
                Map<Integer, List<Integer>> compMap = new HashMap<>();
                for (int i = 0; i < compCount; i++) compMap.put(i, compAdj.get(i));
                TaskSorter sorter = new TaskSorter(compMap, topoM);
                topoM.start();
                List<Integer> topoOrder = sorter.perform();
                topoM.stop();
                long topoOps = topoM.get("qPush") + topoM.get("qPop");
                double topoTime = topoM.ms();
                csv.addRow(file.getName(), id, n, m, cyclic, "Topo", topoTime, (int)topoOps, null);

                MetricsTimer dagM = new MetricsTimer();
                PathPlanner planner = new PathPlanner(weighted, dagM);
                double maxLen = Double.NEGATIVE_INFINITY;
                long relaxOps = 0;
                double dagTime = 0;

                if (!cyclic && !topoOrder.isEmpty()) {
                    dagM.start();
                    for (int src : topoOrder) {
                        Map<Integer, Double> dist = planner.longest(src, topoOrder);
                        for (double val : dist.values()) if (val > maxLen) maxLen = val;
                    }
                    dagM.stop();
                    relaxOps = dagM.get("relax");
                    dagTime = dagM.ms();
                } else {
                    dagM.start(); dagM.stop();
                    relaxOps = dagM.get("relax");
                    dagTime = dagM.ms();
                }

                Double critical = (maxLen == Double.NEGATIVE_INFINITY) ? null : maxLen;
                csv.addRow(file.getName(), id, n, m, cyclic, "DAGPaths", dagTime, (int)relaxOps, critical);

                id++;
            } catch (IOException | JsonSyntaxException e) {
                System.err.println("Load error: " + file.getName());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Done → " + outputFile);
    }
}
