/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import com.google.gson.Gson;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author jiachen
 */
public class MapPainter {

    public static class DrawSetting {

        private final String mapFileName;
        private final String prefix;
        private final double scale, xShift, yShift;
        private final MapItem[] mis;
        private final Rectangle2D bound;

        public DrawSetting(String mapFileName, String prefix, double scale, double xShift, double yShift) throws IOException {
            this.mapFileName = mapFileName;
            this.prefix = prefix;
            this.scale = scale;
            this.xShift = xShift;
            this.yShift = yShift;
            mis = getFromMapFile(mapFileName);
            Path2D.Double p = new Path2D.Double();
            for (MapItem mi : mis) {
                p.append(mi.getPath(), false);
            }
            bound = p.getBounds2D();
        }

        public String getMapFileName() {
            return mapFileName;
        }

        public String getPrefix() {
            return prefix;
        }

        public double getScale() {
            return scale;
        }

        public double getxShift() {
            return xShift;
        }

        public double getyShift() {
            return yShift;
        }

        public MapItem[] getMis() {
            return mis;
        }

        public Rectangle2D getBound() {
            return bound;
        }

    }

    public static MapItem[] getFromMapFile(String mapFile) throws IOException {
        Gson gson = new Gson();
        MapItem[] items;
        try (FileReader reader = new FileReader(mapFile)) {
            items = gson.fromJson(reader, MapItem[].class);
        }
        return items;
    }

    public static BufferedImage generateBufferedImage(DrawSetting[] settings) {
        Path2D.Double p = new Path2D.Double();
        for (MapPainter.DrawSetting setting : settings) {
            Rectangle2D.Double r2d = new Rectangle2D.Double(
                    setting.getxShift(),
                    setting.getyShift(),
                    setting.getBound().getWidth() * setting.getScale(),
                    setting.getBound().getHeight() * setting.getScale());
//            System.out.println(r2d);
            p.append(r2d, false);
        }

        Rectangle2D r2d = p.getBounds2D();
        double width = r2d.getWidth() + r2d.getX(), height = r2d.getHeight() + r2d.getY();
//        System.out.printf("w=%f, h=%f%n", width, height);

        BufferedImage img = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_4BYTE_ABGR);
        return img;
    }

    public static Graphics2D getGraphicsFromImage(BufferedImage img, int fontSize) {
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font(g2d.getFont().getName(), g2d.getFont().getStyle(), fontSize));
        return g2d;
    }

    public static Graphics2D drawSetting(Graphics2D g2d, DrawSetting[] settings, HashMap<MapItem, Color> colors, HashMap<MapItem, String> texts, double strokeThickness) {
        Rectangle2D r2d;
        HashMap<MapItem, Point2D.Double> centers = new HashMap<>();
        for (MapPainter.DrawSetting setting : settings) {
            AffineTransform tr = g2d.getTransform();
            r2d = setting.getBound();
            double scale = setting.getScale();
            double xShift = -r2d.getX() * scale + setting.getxShift(),
                    yShift = -r2d.getY() * scale + setting.getyShift();
//            System.out.printf("bound:%s, tx=%f, ty=%f%n", r2d, xShift, yShift);
            g2d.translate(xShift, yShift);
            g2d.scale(scale, scale);
            g2d.setStroke(new BasicStroke((float) (strokeThickness / scale)));

            for (MapItem mi : setting.getMis()) {
                Path2D.Double s = new Path2D.Double();
                s.append(mi.getPath(), false);
                Color fill = colors.get(mi);
                if (fill != null) {
                    g2d.setColor(fill);
                    g2d.fill(s);
                }
                g2d.setColor(Color.black);
                g2d.draw(s);

                String text = texts.get(mi);
                if (text != null) {
                    r2d = s.getBounds2D();
                    double centerX = r2d.getCenterX(), centerY = r2d.getCenterY();
                    centerX = centerX * scale + xShift;
                    centerY = centerY * scale + yShift;
                    centers.put(mi, new Point2D.Double(centerX, centerY));
                }
            }
            g2d.setTransform(tr);
        }
        for (MapPainter.DrawSetting setting : settings) {
            for (MapItem mi : setting.getMis()) {
                String text = texts.get(mi);
                if (text != null) {
                    Point2D.Double p = centers.get(mi);
                    double centerX = p.x;
                    double centerY = p.y;
                    r2d = g2d.getFontMetrics().getStringBounds(text, g2d);
                    centerX -= r2d.getWidth() / 2;
                    centerY += r2d.getHeight() / 2;
                    g2d.setColor(Color.white);
                    g2d.drawString(text, (float) (centerX - 2), (float) (centerY));
                    g2d.drawString(text, (float) (centerX + 2), (float) (centerY));
                    g2d.drawString(text, (float) (centerX), (float) (centerY - 2));
                    g2d.drawString(text, (float) (centerX), (float) (centerY + 2));
                    g2d.setColor(Color.black);
                    g2d.drawString(text, (float) (centerX), (float) (centerY));
                }
            }
        }
        return g2d;
    }

    public static BufferedImage cropImage(BufferedImage original, int x, int y, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, original.getType());
        Graphics2D g2d = bi.createGraphics();
        g2d.drawImage(original, -x, -y, null);
        return bi;
    }

    public static void saveImage(BufferedImage img, String outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            ImageIO.write(img, "png", fos);
        }
    }

//    public static void drawLegend(BufferedImage img, int startX, int startY, int endY, int width, int min, int max) {
//        
//    }
}
