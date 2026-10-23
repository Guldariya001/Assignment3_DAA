package daa;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class PlotGenerator {

    record Row(String algorithm, String graph, int n, int m,
               double time, long dsu, long scans, long comparisons,
               long depth, long result) {}

    static final int W = 1200;
    static final int H = 700;

    public static void main(String[] args) throws Exception {
        List<Row> rows = readCsv("results/results.csv");

        Files.createDirectories(Path.of("results/plots"));

        draw(rows, "MST: weight comparisons",
                "comparisons", "results/plots/mst_operations.png",
                List.of("Kruskal", "Prim"));

        draw(rows, "Edge scans",
                "scans", "results/plots/edge_scans.png",
                List.of("Kruskal", "Prim", "Kosaraju", "Kahn", "DagShortestPath"));

        draw(rows, "DSU operations in Kruskal",
                "dsu", "results/plots/dsu_operations.png",
                List.of("Kruskal"));

        System.out.println("Done: 3 PNG files in results/plots/");
    }

    static List<Row> readCsv(String file) throws IOException {
        List<Row> rows = new ArrayList<>();

        for (String line : Files.readAllLines(Path.of(file))) {
            if (line.isBlank() || line.startsWith("algorithm,")) {
                continue;
            }

            String[] p = line.split(",");

            rows.add(new Row(
                    p[0], p[1],
                    Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]),
                    Double.parseDouble(p[4]),
                    Long.parseLong(p[5]),
                    Long.parseLong(p[6]),
                    Long.parseLong(p[7]),
                    Long.parseLong(p[8]),
                    Long.parseLong(p[9])
            ));
        }

        return rows;
    }

    static long value(Row r, String metric) {
        return switch (metric) {
            case "comparisons" -> r.comparisons();
            case "scans" -> r.scans();
            case "dsu" -> r.dsu();
            default -> 0;
        };
    }

    static void draw(List<Row> rows, String title, String metric,
                     String filename, List<String> algorithms) throws IOException {

        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        int left = 110;
        int right = 45;
        int top = 110;
        int bottom = 115;

        int plotW = W - left - right;
        int plotH = H - top - bottom;

        g.setColor(new Color(35, 45, 60));
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        g.drawString(title, left, 45);

        List<String> graphNames = new ArrayList<>();

        for (Row r : rows) {
            if (!graphNames.contains(r.graph())) {
                graphNames.add(r.graph());
            }
        }

        long max = 1;

        for (Row r : rows) {
            if (algorithms.contains(r.algorithm())) {
                max = Math.max(max, value(r, metric));
            }
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));

        for (int i = 0; i <= 5; i++) {
            int y = top + plotH - i * plotH / 5;
            long tick = max * i / 5;

            g.setColor(new Color(225, 230, 235));
            g.drawLine(left, y, left + plotW, y);

            g.setColor(new Color(65, 75, 85));
            g.drawString(String.format(Locale.US, "%,d", tick), 10, y + 5);
        }

        g.setColor(new Color(65, 75, 85));
        g.drawLine(left, top, left, top + plotH);
        g.drawLine(left, top + plotH, left + plotW, top + plotH);

        Color[] colors = {
                new Color(40, 110, 210),
                new Color(225, 105, 55),
                new Color(35, 155, 110),
                new Color(145, 90, 185),
                new Color(195, 145, 30)
        };

        for (int a = 0; a < algorithms.size(); a++) {
            String algorithm = algorithms.get(a);

            g.setColor(colors[a % colors.length]);
            g.setStroke(new BasicStroke(3f));

            int prevX = -1;
            int prevY = -1;

            for (int i = 0; i < graphNames.size(); i++) {
                String graphName = graphNames.get(i);

                Row selected = null;

                for (Row r : rows) {
                    if (r.graph().equals(graphName)
                            && r.algorithm().equals(algorithm)) {
                        selected = r;
                        break;
                    }
                }

                if (selected == null) {
                    continue;
                }

                int x = left + (graphNames.size() == 1
                        ? plotW / 2
                        : i * plotW / (graphNames.size() - 1));

                int y = top + plotH
                        - (int) (value(selected, metric) * (double) plotH / max);

                if (prevX >= 0) {
                    g.drawLine(prevX, prevY, x, y);
                }

                g.fillOval(x - 5, y - 5, 10, 10);

                prevX = x;
                prevY = y;
            }
        }

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));

        for (int i = 0; i < graphNames.size(); i++) {
            int x = left + (graphNames.size() == 1
                    ? plotW / 2
                    : i * plotW / (graphNames.size() - 1));

            String label = graphNames.get(i).replace("_", "/");

            Graphics2D rotated = (Graphics2D) g.create();
            rotated.translate(x, top + plotH + 12);
            rotated.rotate(-Math.PI / 4);
            rotated.setColor(new Color(65, 75, 85));
            rotated.drawString(label, 0, 0);
            rotated.dispose();
        }

        int legendX = left;

        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        for (int i = 0; i < algorithms.size(); i++) {
            g.setColor(colors[i % colors.length]);
            g.fillRect(legendX, 72, 18, 10);

            g.setColor(new Color(35, 45, 60));
            g.drawString(algorithms.get(i), legendX + 24, 82);

            legendX += algorithms.get(i).length() * 9 + 55;
        }

        g.dispose();

        ImageIO.write(image, "png", new File(filename));
    }
}
