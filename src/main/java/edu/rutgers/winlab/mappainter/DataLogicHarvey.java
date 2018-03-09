/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import com.google.gson.Gson;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.FLOAT_FORMAT;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.INTEGER_FORMAT;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.concatenate;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.drawBarCharts;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.findCounty;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.squareMeterToSquareMile;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jiachen
 */
public class DataLogicHarvey {

    public static String renameCounty(String prefix, String county) {
        switch (prefix) {
            case "TX":
                switch (county) {
                    case "Harris":
                        return "Harris County";
                }
                break;
            case "LA":
                switch (county) {
                    case "Jefferson":
                        return "Jefferson Parish";
                }
                break;
        }
        return county;
    }

    public static String getShortCountyName(String county) {
        if (county.endsWith(" Parish")) {
            return county.substring(0, county.length() - 7).trim();
        }
        if (county.endsWith(" County")) {
            return county.substring(0, county.length() - 7).trim();
        }
        return county;
    }

    private static final double BOUND_X = 310, BOUND_Y = 240, BOUND_WIDTH = 430, BOUND_HEIGHT = 280;
    private static final String PREFIX = "Harvey_";

    public static void drawDisasterArea(boolean crop) throws IOException {
        double imageScale = 10;
        double strokeThickness = 3;
        Color disasterAreaColor = Color.red;
        Color disasterArea2Color = Color.blue;
        Color disasterArea3Color = Color.gray;
        int fontSize = 30;

        HashMap<MapItem, Color> colors = new HashMap<>();
        HashMap<MapItem, String> texts = new HashMap<>();
        String[] TXCounties = "Aransas,Atascosa,Bee,Brazoria,Brooks,Calhoun,Cameron,Chambers,Colorado,DeWitt,Duval,Fort Bend,Galveston,Goliad,Gonzales,Harris,Hidalgo,Jackson,Jefferson,Jim Wells,Karnes,Kenedy,Kleberg,Lavaca,Liberty,Live Oak,Matagorda,McMullen,Nueces,Orange,Refugio,San Patricio,Victoria,Wharton,Willacy,Wilson".split(",");
        String[] LACounties = "Acadia,Assumption,Calcasieu,Cameron,Iberia,Jefferson,Jefferson Davis,Lafayette,Lafourche,Orleans,Plaquemines,St. Bernard,St. Charles,St. James,St. John the Baptist,St. Martin,St. Mary,Terrebonne,Vermilion".split(",");
        String[] TXCounties2 = "Bexar,Fayette,Hardin,Jasper,Montgomery,Newton,Sabine,San Jacinto,Waller".split(",");
        String[] TXCounties3 = "Aransas,Calhoun,Chambers,Hardin,Harris,Jefferson,Matagorda,Nueces,Orange,Refugio,San Patricio,Victoria,Wharton".split(",");

        MapPainter.DrawSetting[] settings = new MapPainter.DrawSetting[]{
            new MapPainter.DrawSetting("ResultMapTX.json", "TX", imageScale, 0, 0),
            new MapPainter.DrawSetting("ResultMapLA.json", "LA", imageScale * 1102.0 / 2770, (518.0 + 3.0) * imageScale, (174.2 + 0.0) * imageScale)
        };
//        for (MapItem mi : settings[0].getMis()) {
//            System.out.printf("name: %s%n", mi.getName());
//        }
        for (String TXCounty : TXCounties) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicHarvey::renameCounty, TXCounty, "TX");
            if (mi != null) {
                colors.put(mi, disasterAreaColor);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }
        for (String LACounty : LACounties) {
            MapItem mi = findCounty(settings[1].getMis(), DataLogicHarvey::renameCounty, LACounty, "LA");
            if (mi != null) {
                colors.put(mi, disasterAreaColor);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }

        BufferedImage img;
        Graphics2D g2d;

        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, colors, texts, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, PREFIX, "disasterArea1.png");

        for (String TXCounty : TXCounties2) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicHarvey::renameCounty, TXCounty, "TX");
            if (mi != null) {
                colors.put(mi, disasterArea2Color);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }
        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, colors, texts, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, PREFIX, "disasterArea2.png");

        colors.keySet().forEach((mapItem) -> {
            colors.put(mapItem, disasterArea3Color);
        });
        for (String TXCounty : TXCounties3) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicHarvey::renameCounty, TXCounty, "TX");
            if (mi != null) {
                colors.put(mi, disasterAreaColor);
            }
        }
        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, colors, texts, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, PREFIX, "disasterArea3.png");
    }

    public static void drawPopulationDensityDisasterArea(boolean crop) throws IOException {
        double imageScale = 10;
        double strokeThickness = 3;
        int fontSize = 20;
        float hue = 0, saturation = 100, minBrightness = 95.0f, maxBrightness = 5.0f;
        String[] TXCounties = "Aransas,Atascosa,Bee,Brazoria,Brooks,Calhoun,Cameron,Chambers,Colorado,DeWitt,Duval,Fort Bend,Galveston,Goliad,Gonzales,Harris,Hidalgo,Jackson,Jefferson,Jim Wells,Karnes,Kenedy,Kleberg,Lavaca,Liberty,Live Oak,Matagorda,McMullen,Nueces,Orange,Refugio,San Patricio,Victoria,Wharton,Willacy,Wilson,Bexar,Fayette,Hardin,Jasper,Montgomery,Newton,Sabine,San Jacinto,Waller".split(",");
        String[] LACounties = "Acadia,Assumption,Calcasieu,Cameron,Iberia,Jefferson,Jefferson Davis,Lafayette,Lafourche,Orleans,Plaquemines,St. Bernard,St. Charles,St. James,St. John the Baptist,St. Martin,St. Mary,Terrebonne,Vermilion".split(",");

        Gson gson = new Gson();
        CountyItem[] resultDataLA, resultDataTX, resultData;
        try (FileReader reader = new FileReader("ResultDataLA.json")) {
            resultDataLA = gson.fromJson(reader, CountyItem[].class);
        }
        try (FileReader reader = new FileReader("ResultDataTX.json")) {
            resultDataTX = gson.fromJson(reader, CountyItem[].class);
        }
        resultData = concatenate(resultDataLA, resultDataTX);

        MapPainter.DrawSetting[] settings = new MapPainter.DrawSetting[]{
            new MapPainter.DrawSetting("ResultMapTX.json", "TX", imageScale, 0, 0),
            new MapPainter.DrawSetting("ResultMapLA.json", "LA", imageScale * 1102.0 / 2770, (518.0 + 3.0) * imageScale, (174.2 + 0.0) * imageScale)
        };
        HashMap<String, MapItem> areas = new HashMap<>();
        for (String TXCounty : TXCounties) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicHarvey::renameCounty, TXCounty, "TX");
            if (mi != null) {
                areas.put(mi.getFIPS(), mi);
            }
        }
        for (String LACounty : LACounties) {
            MapItem mi = findCounty(settings[1].getMis(), DataLogicHarvey::renameCounty, LACounty, "LA");
            if (mi != null) {
                areas.put(mi.getFIPS(), mi);
            }
        }
        long maxPop = Long.MIN_VALUE;
        double maxDensity = Double.NEGATIVE_INFINITY;
        int count = 0;
        for (CountyItem countyItem : resultData) {
            if (!areas.containsKey(countyItem.getFIPS())) {
                continue;
            }
            count++;
            long pop = countyItem.getPop();
            double area = squareMeterToSquareMile(countyItem.getArea());
            double density = pop / area;
            maxPop = Math.max(pop, maxPop);
            maxDensity = Math.max(density, maxDensity);
//            System.out.printf("%s\t%s\t%d\t%f\t%f%n", countyItem.getFIPS(), countyItem.getCTYNAME(), pop, area, density);
        }
        System.out.printf("areas: %d|%d, maxPop: %d, maxDensity: %f%n", areas.size(), count, maxPop, maxDensity);

        HashMap<MapItem, Color> populationColors = new HashMap<>(), densityColors = new HashMap<>();
        HashMap<MapItem, String> populationStrings = new HashMap<>(), densityStrings = new HashMap<>();
        for (CountyItem countyItem : resultData) {
            MapItem mi;
            if ((mi = areas.get(countyItem.getFIPS())) == null) {
                continue;
            }
            long pop = countyItem.getPop();
            double area = squareMeterToSquareMile(countyItem.getArea());
            double density = pop / area;
            String shortName = getShortCountyName(mi.getName());
            populationColors.put(mi, HSLColor.toRGB(hue, saturation, pop * (maxBrightness - minBrightness) / maxPop + minBrightness));
            densityColors.put(mi, HSLColor.toRGB(hue, saturation, (float) (density * (maxBrightness - minBrightness) / maxDensity + minBrightness)));
            populationStrings.put(mi, String.format("%s:%s", shortName, INTEGER_FORMAT.format(pop)));
            densityStrings.put(mi, String.format("%s:%s", shortName, FLOAT_FORMAT.format(density)));
        }

        System.out.println(HSLColor.toRGB(0, 100.0f, 100.0f));

        BufferedImage img;
        Graphics2D g2d;

        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, populationColors, populationStrings, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, PREFIX, "population.png");

        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, densityColors, densityStrings, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, PREFIX, "density.png");

    }

    public static void drawCellSites(boolean crop) throws IOException {
        double imageScale = 10;
        double strokeThickness = 3;
        int fontSize = 20;
        float hue = 0, saturation = 100, minBrightness = 95.0f, maxBrightness = 5.0f;
        int maxOut = 160, maxPopOut = 234554;
        double maxOutPercent = 18.0 / 19 * 100;
        int days = 11;
        Gson gson = new Gson();
        Color[] colorSetting = new Color[]{
            new Color(255, 0, 0, 64),
            Color.green,
            Color.blue
        };

        MapPainter.DrawSetting[] settings = new MapPainter.DrawSetting[]{
            new MapPainter.DrawSetting("ResultMapTX.json", "TX", imageScale, 0, 0),
            new MapPainter.DrawSetting("ResultMapLA.json", "LA", imageScale * 1102.0 / 2770, (518.0 + 3.0) * imageScale, (174.2 + 0.0) * imageScale)
        };
        CountyItem[] resultDataLA, resultDataTX;
        HashMap<String, CountyItem> resultData = new HashMap<>();
        try (FileReader reader = new FileReader("ResultDataLA.json")) {
            resultDataLA = gson.fromJson(reader, CountyItem[].class);
        }
        try (FileReader reader = new FileReader("ResultDataTX.json")) {
            resultDataTX = gson.fromJson(reader, CountyItem[].class);
        }
        for (CountyItem countyItem : resultDataTX) {
            resultData.put(countyItem.getFIPS(), countyItem);
        }
        for (CountyItem countyItem : resultDataLA) {
            resultData.put(countyItem.getFIPS(), countyItem);
        }

        HashMap<MapItem, Color>[] outColors = (HashMap<MapItem, Color>[]) new HashMap<?, ?>[days];
        HashMap<MapItem, Color>[] outPercentColors = (HashMap<MapItem, Color>[]) new HashMap<?, ?>[days];
        HashMap<MapItem, Color>[] popColors = (HashMap<MapItem, Color>[]) new HashMap<?, ?>[days];
        HashMap<MapItem, String>[] texts = (HashMap<MapItem, String>[]) new HashMap<?, ?>[days];
        HashMap<MapItem, String>[] popTexts = (HashMap<MapItem, String>[]) new HashMap<?, ?>[days];

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<Double>[][] values = (ArrayList<Double>[][]) new ArrayList<?>[days][3];

        for (int i = 0; i < outColors.length; i++) {
            outColors[i] = new HashMap<>();
            outPercentColors[i] = new HashMap<>();
            texts[i] = new HashMap<>();
            popColors[i] = new HashMap<>();
            popTexts[i] = new HashMap<>();
        }
        for (ArrayList<Double>[] value : values) {
            for (int j = 0; j < value.length; j++) {
                value[j] = new ArrayList<>();
            }
        }

        try (BufferedReader br1 = new BufferedReader(new FileReader("Harvey_CellSitesServed.txt"))) {
            try (BufferedReader br2 = new BufferedReader(new FileReader("Harvey_CellSitesOut.txt"))) {
                br1.readLine();
                br2.readLine();
                String line1, line2;
                int lineCount = 1;
                while ((line1 = br1.readLine()) != null) {
                    line2 = br2.readLine();
                    lineCount++;
                    String[] parts1 = line1.split("\t");
                    String[] parts2 = line2.split("\t");
                    if (parts1.length != parts2.length || parts1.length < 2 || !parts1[0].equals(parts2[0]) || !parts1[1].equals(parts2[1])) {
                        throw new IllegalArgumentException("Files not aligned at line " + lineCount);
                    }
                    MapItem mi = findCounty((parts1[0].equals("TX") ? settings[0] : settings[1]).getMis(), DataLogicHarvey::renameCounty, parts1[1], parts1[0]);
                    String shortName = getShortCountyName(mi.getName());
                    titles.add(shortName);
                    long population = resultData.get(mi.getFIPS()).getPop();
                    for (int i = 0; i < days; i++) {
                        int pos = i + 2;
                        if (pos >= parts1.length) {
                            values[i][0].add(0.0);
                            values[i][1].add(0.0);
                            values[i][2].add(0.0);
                            continue;
                        }
                        if (parts1[pos].equals("")) {
                            if (!parts2[pos].equals("")) {
                                throw new IllegalArgumentException("Files not aligned at line " + lineCount + ", part " + pos);
                            } else {
                                values[i][0].add(0.0);
                                values[i][1].add(0.0);
                                values[i][2].add(0.0);
                                continue;
                            }
                        }
                        int served = Integer.parseInt(parts1[pos]),
                                out = Integer.parseInt(parts2[pos]);
                        double percent = out * 100.0 / served;
                        int popOut = (int) Math.ceil(out * 1.0 / served * population);
                        values[i][0].add(percent / maxOutPercent);
                        values[i][1].add(out * 1.0 / maxOut);
                        values[i][2].add(popOut * 1.0 / maxPopOut);

                        if (out == 0) {
                            texts[i].put(mi, String.format("%s:%s", shortName, INTEGER_FORMAT.format(served)));
//                            outPercentColors[i].put(mi, Color.white);
//                            outColors[i].put(mi, Color.white);
                            popTexts[i].put(mi, String.format("%s", shortName));
//                            popColors[i].put(mi, Color.white);
                        } else {
                            texts[i].put(mi, String.format("%s:%s/%s(%.2f%%)", shortName, INTEGER_FORMAT.format(out), INTEGER_FORMAT.format(served), percent));
                            outPercentColors[i].put(mi, HSLColor.toRGB(hue, saturation, (float) (percent * (maxBrightness - minBrightness) / maxOutPercent + minBrightness)));
                            outColors[i].put(mi, HSLColor.toRGB(hue, saturation, (float) (out * (maxBrightness - minBrightness) / maxOut + minBrightness)));
                            popTexts[i].put(mi, String.format("%s:%s", shortName, INTEGER_FORMAT.format(popOut)));
                            popColors[i].put(mi, HSLColor.toRGB(hue, saturation, (float) (popOut * (maxBrightness - minBrightness) / maxPopOut + minBrightness)));
                        }
                    }
                }
            }
        }

        BufferedImage img;
        Graphics2D g2d;

        for (int i = 0; i < texts.length; i++) {
            img = MapPainter.generateBufferedImage(settings);
            g2d = MapPainter.getGraphicsFromImage(img, fontSize);
            MapPainter.drawSetting(g2d, settings, outColors[i], texts[i], strokeThickness);
            if (crop) {
                img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
            }
            MapPainter.saveImage(img, PREFIX, "out_" + i + ".png");

            img = MapPainter.generateBufferedImage(settings);
            g2d = MapPainter.getGraphicsFromImage(img, fontSize);
            MapPainter.drawSetting(g2d, settings, outPercentColors[i], texts[i], strokeThickness);
            if (crop) {
                img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
            }
            MapPainter.saveImage(img, PREFIX, "outPercent_" + i + ".png");

            img = MapPainter.generateBufferedImage(settings);
            g2d = MapPainter.getGraphicsFromImage(img, fontSize);
            MapPainter.drawSetting(g2d, settings, popColors[i], popTexts[i], strokeThickness);
            if (crop) {
                img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
            }
            MapPainter.saveImage(img, PREFIX, "outPop_" + i + ".png");

            drawBarCharts(titles, colorSetting, values[i], 10, 300, fontSize, PREFIX, "bar_" + i + ".png");
        }

    }
}
