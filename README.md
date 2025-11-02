README.md — Smart City Scheduling (Assignment 4)
# Project Overview

This project implements a modular Java 17 system for analyzing and scheduling directed task graphs in a “Smart City / Smart Campus” scenario.
It consolidates three main algorithmic topics:

Strongly Connected Components (SCC) detection and condensation.

Topological Ordering of condensed DAGs.

Shortest / Longest Path analysis in DAGs for critical path estimation.

The implementation follows a performance-driven structure using a unified metrics system to record time and operation counts for each algorithm.

# 1. Project Structure
   
src/ <br>
├── app/ <br>
│   └── MainAnalyzer.java   ← Main entry: runs all analyses and writes results <br>
│ <br>
├── graphs/ <br>
│   ├── components/ <br>
│   │   ├── StrongCompFinder.java   ← Tarjan SCC implementation <br>
│   │   └── CondensedGraph.java   ← Builds condensation DAG <br>
│   │ <br>
│   ├── sorting/ <br>
│   │   └── TaskSorter.java   ← Kahn’s topological sort with metrics <br>
│   │ <br>
│   └── paths/ <br>
│     └── PathPlanner.java   ← DAG shortest/longest path calculation <br>
│ <br>
├── io/ <br>
│   ├── DataReader.java   ← Reads JSON graph files <br>
│   └── CsvReport.java   ← Writes results to CSV with Locale.US format <br>
│ <br>
├── util/ <br>
│   ├── Metrics.java   ← Interface for metrics tracking <br>
│   └── MetricsTimer.java   ← Implementation for timing and operation count <br>
│ <br>
└── datafactory/ <br>
    └── GraphDatasetBuilder.java   ← Random graph dataset generator <br>

## Tests

src/test/java/tests/ <br>
├── DataReaderIntegrationTest.java   ← Validates JSON loading and data structure <br>
├── StrongCompFinderTest.java   ← Verifies SCC correctness <br>
├── TaskSorterTest.java   ← Tests topological sort (acyclic / cyclic) <br>
└── PathPlannerTest.java   ← Tests DAG shortest and longest path logic <br>

# 3. Dependencies

All dependencies are declared in pom.xml:

Gson 2.11.0 – reading / writing JSON graphs.

JUnit 5.10.2 – unit testing (API + Engine).

Guava 33.1.0-jre – optional utilities.

Compiler: Java 17
Build system: Apache Maven 3.11+

# 3. Modules and Classes
Module	Main Classes	Description
Metrics	Metrics, MetricsTimer	Provides timing and operation-count tracking for each algorithm.
Graph Input/Output	DataReader, CsvReport	Loads JSON datasets and writes result tables in CSV format.
SCC Analysis	StrongCompFinder, CondensedGraph	Implements Tarjan’s SCC algorithm and builds the condensation DAG.
Topological Ordering	TaskSorter	Implements Kahn’s algorithm for topological sorting with instrumentation.
Path Analysis	PathPlanner	Computes both shortest and longest (critical) paths on DAGs.
Dataset Generator	GraphDatasetBuilder	Produces random weighted graphs of various sizes and densities.
Main Application	MainAnalyzer	Runs the full workflow: load → analyze → record results.

# 4. Execution Workflow

Generate datasets (optional):

mvn exec:java -Dexec.mainClass="datafactory.GraphDatasetBuilder"


Analyze graphs:

mvn exec:java -Dexec.mainClass="app.MainAnalyzer"


Results appear in

data/results/analytics.csv


Each row contains:
File, ID, Nodes, Edges, Cyclic, Algorithm, Time(ms), Ops, CriticalLen.

# 5. Algorithms Summary
## 5.1 Strongly Connected Components

StrongCompFinder uses Tarjan’s DFS-based method to identify SCCs.
Detected SCCs are passed to CondensedGraph, which compresses the original graph into a DAG of components.

## 5.2 Condensation DAG + Topological Sort

CondensedGraph outputs adjacency lists among components;
TaskSorter applies Kahn’s algorithm to order them linearly.
Operation counts (queuePush, queuePop) are tracked via MetricsTimer.

## 5.3 DAG Paths (Shortest / Longest)

PathPlanner implements dynamic programming over topological order to compute path lengths.
The longest path represents the critical path (maximum cumulative weight).

# 6. Dataset Generation

GraphDatasetBuilder generates 9 JSON graphs categorized as follows:

Category	Nodes (n)	Description	Count
Small	6–10	Simple cyclic or acyclic graphs	3
Medium	10–20	Graphs with several SCCs	3
Large	20–50	Dense graphs for performance testing	3

Graphs are stored in /data/ and contain:

{
  "meta": {"nodes":10,"edges":20,"cyclic":false},
  "graph": {
    "0": [[1,3],[2,5]],
    "1": [[3,4]]
  }
}

# 7. Testing
Test Class	Purpose
SCCFinderTest	Verifies correct SCC detection.
TopologicalSortTest	Ensures valid topological ordering.
PathPlannerTest	Validates longest path results in DAG.

Run all tests:

mvn test

# 8. Empirical Validation

Empirical validation in this project is implemented directly inside the algorithms through the metrics system (Metrics / MetricsTimer).
Every key algorithm (SCC, Topological Sort, and Path Planner) starts and stops a timer around its main execution block and records how many operations were performed.

Where exactly it happens:

In StrongCompFinder:

Starts timer in execute() before DFS.

Counts every node visit (dfsV) and edge traversal (dfsE).

In TaskSorter:

Tracks queue operations: queuePush and queuePop.

Measures total runtime for topological ordering.

In PathPlanner:

Increments relax for each edge relaxation.

Measures runtime for shortest/longest path computation.

In MainAnalyzer:

Collects time (in milliseconds) and operation counts from each algorithm.

Writes all results into the CSV file using CsvReport.

Thus, the CSV output serves as empirical performance evidence, showing real measured data for each algorithm and dataset.

This is my empirical validation:
i can measure, record, and analyze the actual time and operations for each algorithm — not just theoretically, but with experimental data.

# 9. Experimental Results (Empirical Data & Visualization)

| Category | File | Nodes | Edges | Cyclic | SCC Time | Topo Time | DAG Time | Critical Path |
|-----------|------|--------|--------|---------|-----------|------------|-----------|----------------|
| **Large** | large_1.json | 46 | 889 | true | 0.672 | 0.088 | 0.000 | — |
|           | large_2.json | 34 | 180 | false | 0.396 | 0.371 | 0.016 | 3.000 |
| **Medium**| medium_2.json| 13 | 53  | false | 0.036 | 0.052 | 0.008 | 2.000 |
| **Small** | small_2.json | 10 | 22  | false | 0.021 | 0.020 | 0.004 | 2.000 |

Interpretation:
Larger graphs (with higher edge density) show increased operation counts and execution times.
Smaller datasets complete almost instantly, validating that all implemented algorithms scale linearly with graph size

Below is a visualization of the mean edge count for each dataset category.
It clearly shows that Large graphs have significantly higher edge density compared to Medium and Small graphs,
which directly impacts the runtime and number of operations measured by the algorithms.

<p align="center"> <img src="data/results/edges_mean_chart.png" alt="Mean edges per dataset" width="700"> </p>

The chart demonstrates the scalability trend of the algorithms:
as the number of edges increases, computation time and operation count grow linearly, confirming empirical complexity O(V + E).

<img width="1235" height="488" alt="image" src="https://github.com/user-attachments/assets/31d03554-d4ea-40e6-ab5d-3d7474402c50" />

# 10. Performance Discussion

Trends observed:

SCC shows higher operation counts on dense graphs (Large).

Topological Sort runs quickly for DAGs, minimal overhead.

DAGPaths executes only for acyclic graphs (critical path length appears).

For cyclic graphs (e.g., large_1.json), critical path computation is skipped (DAGPaths = 0 ms, 0 ops).
For acyclic graphs (e.g., large_2.json, medium_2.json, small_2.json), nonzero path lengths confirm correct path analysis.

These patterns match theoretical expectations:

O(V + E) scaling for SCC and Topo.

DP-based path analysis proportional to edge density.

# 11. Run Instructions
 > How to build, test, and run the project

### Using Maven (Recommended)

### Clean and compile the project
mvn clean compile

### Run the main application
mvn exec:java -Dexec.mainClass="app.AppMain"

### Run all JUnit tests
mvn test

# 13. Conclusion

This project demonstrates the integration of core graph algorithms in a clean and modular Java framework. It includes implementations for SCC detection (Tarjan), Condensation graph construction, Topological Sorting (Kahn), and Longest Path analysis for DAGs.

The metrics system provided empirical evidence of algorithm performance by recording execution time and operation counts across small, medium, and large datasets. The results confirmed the expected linear complexity O(V + E) and showed consistent scalability as graph size and density increased.

Overall, the system meets all Assignment 4 requirements, combining theoretical correctness with experimental validation. It provides a solid foundation for further extensions such as dependency scheduling, process optimization, or algorithm benchmarking in larger graph systems.

