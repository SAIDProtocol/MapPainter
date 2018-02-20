/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import java.awt.geom.PathIterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author jiachen
 */
public class MapItem {

    private final String d;
    private final String FIPS;
    private final String name;
    private transient MapPathIterator path;

    public MapItem(String d, String FIPS, String name) {
        this.d = d;
        this.FIPS = FIPS;
        this.name = name;
        this.path = new MapPathIterator(PathIterator.WIND_NON_ZERO, d);
    }

    public String getD() {
        return d;
    }

    public String getFIPS() {
        return FIPS;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MapItem{" + "d=" + d + ", FIPS=" + FIPS + ", name=" + name + '}';
    }

    public static boolean isEmpty(char c) {
        switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                return true;
            default:
                return false;
        }
    }

    public static boolean isNumber(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == '-';
    }

    public static void skipEmpty(char[] string, int[] start) {
        int idx = start[0];
        for (; idx < string.length && isEmpty(string[idx]); idx++) {
        }
        start[0] = idx;
    }

    public static double readDouble(char[] string, int[] start) {
        int idx = start[0], end;
        for (end = idx; end < string.length && isNumber(string[end]); end++) {
        }
        double ret = Double.parseDouble(new String(string, idx, end - idx));
        start[0] = end;
        return ret;
    }

    public synchronized PathIterator getPath() {
        if (path == null) {
            path = new MapPathIterator(PathIterator.WIND_NON_ZERO, d);
        }
        return path.sharedCopy();
    }

    private static class MapPathIterator implements PathIterator {

        private static class MapPathItem {

            public int action;
            public double[] points;
            public float[] pointsF;

            public MapPathItem(int action, double[] points, float[] pointsF) {
                this.action = action;
                this.points = points;
                this.pointsF = pointsF;
            }
        }
        private int windingRule;
        private final MapPathItem[] items;
        private int pos = 0;

        public MapPathIterator(int windingRule, String d) {
            this.windingRule = windingRule;
            LinkedList<MapPathItem> tmpItems = new LinkedList<>();

            char[] str = d.toCharArray();
            int[] start = new int[]{0};
            double[] val = new double[1];
            double d0, d1, d2, d3, d4, d5;

            skipEmpty(str, start);
            while (start[0] < str.length) {
                skipEmpty(str, start);
                char action = str[start[0]];
                switch (action) {
                    case 'M':
                    case 'm':
                        start[0]++;
                        skipEmpty(str, start);
                        d0 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d1 = readDouble(str, start);
                        tmpItems.add(new MapPathItem(SEG_MOVETO, new double[]{d0, d1, 0, 0, 0, 0}, new float[]{(float) d0, (float) d1, 0, 0, 0, 0}));
                        break;
                    case 'L':
                    case 'l':
                        start[0]++;
                        skipEmpty(str, start);
                        d0 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d1 = readDouble(str, start);
                        tmpItems.add(new MapPathItem(SEG_LINETO, new double[]{d0, d1, 0, 0, 0, 0}, new float[]{(float) d0, (float) d1, 0, 0, 0, 0}));
                        break;
                    case 'C':
                    case 'c':
                        start[0]++;
                        skipEmpty(str, start);
                        d0 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d1 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d2 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d3 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d4 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d5 = readDouble(str, start);
                        tmpItems.add(new MapPathItem(SEG_LINETO, new double[]{d0, d1, d2, d3, d4, d5}, new float[]{(float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5}));
                        break;
                    case 'Q':
                    case 'q':
                        start[0]++;
                        skipEmpty(str, start);
                        d0 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d1 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d2 = readDouble(str, start);
                        skipEmpty(str, start);
                        if (start[0] < str.length && str[start[0]] == ',') {
                            start[0]++;
                        }
                        skipEmpty(str, start);
                        d3 = readDouble(str, start);
                        tmpItems.add(new MapPathItem(SEG_LINETO, new double[]{d0, d1, d2, d3, 0, 0}, new float[]{(float) d0, (float) d1, (float) d2, (float) d3, 0, 0}));
                        break;
                    case 'Z':
                    case 'z':
                        start[0]++;
                        tmpItems.add(new MapPathItem(SEG_CLOSE, new double[6], new float[6]));
                        break;
                    default:
                        throw new IllegalArgumentException("Error action in d");
                }
            }
            this.items = new MapPathItem[tmpItems.size()];
            tmpItems.toArray(this.items);
        }

        public MapPathIterator(int windingRule, MapPathItem[] items) {
            this.windingRule = windingRule;
            this.items = items;
            this.pos = 0;
        }

        public MapPathIterator sharedCopy() {
            return new MapPathIterator(windingRule, items);
        }

        public void setWindingRule(int windingRule) {
            this.windingRule = windingRule;
        }

        @Override
        public int getWindingRule() {
            return windingRule;
        }

        @Override
        public boolean isDone() {
            return pos >= items.length;
        }

        @Override
        public void next() {
            pos++;
        }

        @Override
        public int currentSegment(float[] arg0) {
//            System.out.println("currentSegment F");
            MapPathItem item = items[pos];
            System.arraycopy(item.pointsF, 0, arg0, 0, item.pointsF.length);
            return item.action;
        }

        @Override
        public int currentSegment(double[] arg0) {
//            System.out.println("currentSegment D");
            MapPathItem item = items[pos];
            System.arraycopy(item.points, 0, arg0, 0, item.points.length);
            return item.action;
        }

    }

}
