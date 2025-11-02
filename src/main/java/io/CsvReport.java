// src/io/CsvReport.java
package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CsvReport {
    private final String path;
    public CsvReport(String path) { this.path = path; }

    public void createHeader() throws IOException {
        try (FileWriter fw = new FileWriter(path)) {
            fw.write("File,ID,Nodes,Edges,Cyclic,Algo,Time(ms),Ops,CriticalLen\n");
        }
    }

    public void addRow(String f, int id, int n, int e, boolean cyc,
                       String algo, double t, int ops, Double c) throws IOException {
        try (FileWriter fw = new FileWriter(path, true)) {
            String row = String.format(Locale.US,
                    "%s,%d,%d,%d,%b,%s,%.3f,%d,%s\n",
                    f, id, n, e, cyc, algo, t, ops,
                    (c == null ? "" : String.format(Locale.US, "%.3f", c)));
            fw.write(row);
        }
    }
}
