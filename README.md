# Assignment 3 — Graph Algorithms

This is my Assignment 3 project for Design and Analysis of Algorithms.

I used Java and Maven to implement and test several graph algorithms.

## Algorithms

The project includes:

- DSU with union by size and path compression
- Kruskal's algorithm
- Prim's algorithm
- Kosaraju's algorithm
- Kahn's topological sorting
- Shortest paths in a DAG

## Technologies

- Java 17 or newer
- Maven
- JUnit 5
- IntelliJ IDEA

## Project Structure

```text
src/main/java/daa/
    Graph.java
    DSU.java
    Metrics.java
    Algorithms.java
    Generator.java
    Benchmark.java
    PlotGenerator.java

src/test/java/daa/
    AlgorithmsTest.java

data/
    connected.txt
    disconnected.txt
    cyclic.txt
    dag.txt

results/
    results.csv
    plots/
        mst_operations.png
        edge_scans.png
        dsu_operations.png

pom.xml
README.md
REPORT.md
```

## How to Run

Open the project in IntelliJ IDEA and wait for Maven to load.

To run the tests, open `AlgorithmsTest.java` and click the green Run button.

You can also run the tests from the terminal:

```bash
mvn test
```

To generate benchmark results, run the `main` method in `Benchmark.java`.

The program saves the results in:

`results/results.csv`

To create the three graphs, run the `main` method in `PlotGenerator.java`.

The PNG files will be saved in the `plots` folder.

## Benchmark

I tested five algorithms on ten graph configurations.

The graph sizes are 1,000, 3,162, 10,000, 31,623, and 100,000 vertices.

For each size, I used two densities: `m = 4n` and `m = 20n`.

The generator uses `Random(42)` to make the results reproducible.

The benchmark saves execution time and operation counters.

## Results

All eight JUnit tests passed.

Kruskal and Prim gave the same MST weights on all generated graphs.

The benchmark correctness results matched the reference table from the assignment.

More information about the algorithms, complexity, and results is available in [REPORT.md](REPORT.md).