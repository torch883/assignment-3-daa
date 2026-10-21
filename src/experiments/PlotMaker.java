package experiments;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class PlotMaker {
    static Color[] COLORS = {
            Color.RED, Color.BLUE, new Color(0, 150, 0),
            Color.ORANGE, Color.MAGENTA, Color.DARK_GRAY
    };

    private static int scale(double v, double min, double max, int size) {
        return (int) ((v - min) / (max - min) * size);
    }

    public static void draw(String path, String title, String xLabel, String yLabel,
                            List<String> names, double[][] xs, double[][] ys) throws Exception {
        int W = 850, H = 550, L = 90, R = 190, T = 50, B = 70;
        int plotW = W - L - R;
        int plotH = H - T - B;

        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        double minX = 1e18, maxX = -1e18, minY = 1e18, maxY = -1e18;
        for (int i = 0; i < names.size(); i++) {
            for (int j = 0; j < xs[i].length; j++) {
                double lx = Math.log10(xs[i][j]);
                double ly = Math.log10(Math.max(ys[i][j], 1e-6));
                minX = Math.min(minX, lx);
                maxX = Math.max(maxX, lx);
                minY = Math.min(minY, ly);
                maxY = Math.max(maxY, ly);
            }
        }
        minX -= 0.1; maxX += 0.1;
        minY -= 0.3; maxY += 0.3;

        for (int k = (int) Math.ceil(minY); k <= (int) Math.floor(maxY); k++) {
            int y = T + plotH - scale(k, minY, maxY, plotH);
            g.setColor(new Color(225, 225, 225));
            g.drawLine(L, y, L + plotW, y);
            g.setColor(Color.BLACK);
            g.drawString("1e" + k, L - 45, y + 5);
        }
        for (int j = 0; j < xs[0].length; j++) {
            int x = L + scale(Math.log10(xs[0][j]), minX, maxX, plotW);
            g.setColor(Color.BLACK);
            g.drawLine(x, T + plotH, x, T + plotH + 5);
            g.drawString(String.valueOf((int) xs[0][j]), x - 12, T + plotH + 20);
        }

        g.setColor(Color.BLACK);
        g.drawLine(L, T + plotH, L + plotW, T + plotH);
        g.drawLine(L, T, L, T + plotH);
        g.drawString(title, L + plotW / 2 - 80, 28);
        g.drawString(xLabel + " (log scale)", L + plotW / 2 - 50, H - 20);
        g.drawString(yLabel + " (log scale)", 10, T - 15);

        for (int i = 0; i < names.size(); i++) {
            g.setColor(COLORS[i % COLORS.length]);
            g.setStroke(new BasicStroke(2f));
            int prevX = 0, prevY = 0;
            for (int j = 0; j < xs[i].length; j++) {
                int x = L + scale(Math.log10(xs[i][j]), minX, maxX, plotW);
                int y = T + plotH - scale(Math.log10(Math.max(ys[i][j], 1e-6)), minY, maxY, plotH);
                g.fillOval(x - 4, y - 4, 8, 8);
                if (j > 0) g.drawLine(prevX, prevY, x, y);
                prevX = x;
                prevY = y;
            }
            g.fillRect(L + plotW + 20, T + 20 + i * 22, 12, 12);
            g.setColor(Color.BLACK);
            g.drawString(names.get(i), L + plotW + 40, T + 31 + i * 22);
        }

        g.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}