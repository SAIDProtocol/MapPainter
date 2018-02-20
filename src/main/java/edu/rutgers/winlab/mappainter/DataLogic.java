/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import com.google.gson.Gson;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author jiachen
 */
public class DataLogic {

    public static <T> T[] concatenate(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static double squareMeterToSquareMile(double squareMeter) {
        return squareMeter * 0.386102159 / 1000000;
    }

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

    public static MapItem findCounty(MapItem[] items, String countyName, String prefix) {
        MapItem candidate = null;
        countyName = renameCounty(prefix, countyName);
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
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#,##0.####");

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
            MapItem mi = findCounty(settings[0].getMis(), TXCounty, "TX");
            if (mi != null) {
                colors.put(mi, disasterAreaColor);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }
        for (String LACounty : LACounties) {
            MapItem mi = findCounty(settings[1].getMis(), LACounty, "LA");
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
        MapPainter.saveImage(img, "disasterArea1.png");

        for (String TXCounty : TXCounties2) {
            MapItem mi = findCounty(settings[0].getMis(), TXCounty, "TX");
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
        MapPainter.saveImage(img, "disasterArea2.png");

        colors.keySet().forEach((mapItem) -> {
            colors.put(mapItem, disasterArea3Color);
        });
        for (String TXCounty : TXCounties3) {
            MapItem mi = findCounty(settings[0].getMis(), TXCounty, "TX");
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
        MapPainter.saveImage(img, "disasterArea3.png");
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
            MapItem mi = findCounty(settings[0].getMis(), TXCounty, "TX");
            if (mi != null) {
                areas.put(mi.getFIPS(), mi);
            }
        }
        for (String LACounty : LACounties) {
            MapItem mi = findCounty(settings[1].getMis(), LACounty, "LA");
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
        MapPainter.saveImage(img, "population.png");

        img = MapPainter.generateBufferedImage(settings);
        g2d = MapPainter.getGraphicsFromImage(img, fontSize);
        MapPainter.drawSetting(g2d, settings, densityColors, densityStrings, strokeThickness);
        if (crop) {
            img = MapPainter.cropImage(img, (int) (BOUND_X * imageScale), (int) (BOUND_Y * imageScale), (int) (BOUND_WIDTH * imageScale), (int) (BOUND_HEIGHT * imageScale));
        }
        MapPainter.saveImage(img, "density.png");

    }
}
