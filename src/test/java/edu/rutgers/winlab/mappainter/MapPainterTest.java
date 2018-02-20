/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jiachen
 */
public class MapPainterTest {

    public MapPainterTest() {
    }

    @Test
    public void test1() throws IOException {
//        MapItem[] mapItems = MapPainter.getFromMapFile("ResultMap.json");
        char[] str = "212".toCharArray();
        int[] start = new int[]{0};
        double[] val = new double[1];
        MapItem.skipEmpty(str, start);
        assertEquals(0, start[0]);

        str = "     212.456,4a3.3.4".toCharArray();
        start[0] = 0;
        MapItem.skipEmpty(str, start);
        assertEquals(5, start[0]);

        assertEquals(212.456, MapItem.readDouble(str, start), 0.000001);
        assertEquals(12, start[0]);
        start[0]++;
        assertEquals(4.0, MapItem.readDouble(str, start), 0.000001);
        assertEquals(14, start[0]);
        try {
            MapItem.readDouble(str, start);
            fail();
        } catch (Exception e) {
            assertEquals(14, start[0]);
        }
        start[0]++;
        try {
            MapItem.readDouble(str, start);
            fail();
        } catch (Exception e) {
            assertEquals(15, start[0]);
        }

        str = "    ".toCharArray();
        start[0] = 0;
        MapItem.skipEmpty(str, start);
        assertEquals(str.length, start[0]);
        try {
            MapItem.readDouble(str, start);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void drawDisasterAreaMapStart() throws IOException {
//        DataLogic.drawDisasterArea();
        DataLogic.drawPopulationDensityDisasterArea();
    }
}
