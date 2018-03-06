/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

/**
 *
 * @author jiachen
 */
public class DataLogicCommon {
    public static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,##0");
    public static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#,##0.####");

    public static <T> T[] concatenate(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static double squareMeterToSquareMile(double squareMeter) {
        return squareMeter * 0.386102159 / 1000000;
    }

    public static MapItem findCounty(MapItem[] items, BiFunction<String, String, String> renameCounty, String countyName, String prefix) {
        MapItem candidate = null;
        countyName = renameCounty.apply(prefix, countyName);
        for (MapItem mi : items) {
            if (mi.getName().contains(countyName)) {
                if (candidate != null) {
                    System.out.printf("%s county: %s, duplicate: %s, %s%n", prefix, countyName, candidate.getName(), mi.getName());
                }
                candidate = mi;
            }
        }
        if (candidate == null) {
            System.out.printf("%s county: %s, not found%n", prefix, countyName);
        }
        return candidate;
    }

    // values[d1][d2] d1==colors.length  d2==titles.length
    public static void drawBarCharts(ArrayList<String> titles, Color[] colors, ArrayList<Double>[] values, int barWidth, int maxHeight, int fontSize, String prefix, String fileName) throws IOException {
        int totalWidth = barWidth * colors.length * titles.size();
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        int maxFontWidth = 0;
        FontMetrics metrics = g2d.getFontMetrics();
        for (String title : titles) {
            maxFontWidth = Math.max(maxFontWidth, metrics.stringWidth(" " + title));
        }
        int totalHeight = maxHeight + maxFontWidth;
        img = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_4BYTE_ABGR);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        metrics = g2d.getFontMetrics();
        for (int j = 0; j < colors.length; j++) {
            g2d.setColor(colors[j]);
            for (int i = 0; i < titles.size(); i++) {
                int height = (int) Math.round(values[j].get(i) * maxHeight);
                g2d.fillRect((i * colors.length + j) * barWidth, maxHeight - height, barWidth, height);
            }
        }
        g2d.setColor(Color.black);
        for (int i = 0; i < titles.size(); i++) {
            g2d.drawLine((i * colors.length) * barWidth, maxHeight - 4, (i * colors.length) * barWidth, maxHeight + 4);
            g2d.drawLine(((i + 1) * colors.length) * barWidth - 1, maxHeight - 4, ((i + 1) * colors.length) * barWidth - 1, maxHeight + 4);
        }
        g2d.drawLine(0, maxHeight, totalWidth, maxHeight);
        g2d.drawLine(0, maxHeight + 1, totalWidth, maxHeight + 1);
        g2d.rotate(Math.PI / 2);
        for (int i = 0; i < titles.size(); i++) {
            String title = " " + titles.get(i);
            g2d.drawString(title, maxHeight, (float) (metrics.getHeight() / 2 - metrics.getDescent() - (i + 0.5) * barWidth * colors.length));
        }
        MapPainter.saveImage(img, prefix, fileName);
    }

}
