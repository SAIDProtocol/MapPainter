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
import static edu.rutgers.winlab.mappainter.DataLogicCommon.findCounty;
import static edu.rutgers.winlab.mappainter.DataLogicCommon.squareMeterToSquareMile;
import static edu.rutgers.winlab.mappainter.DataLogicHarvey.getShortCountyName;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author jiachen
 */
public class DataLogicIrma {

    private static final String PREFIX = "Irma_";
    private static final double BOUND_X = 55, BOUND_Y = 270, BOUND_WIDTH = 545, BOUND_HEIGHT = 595;

    public static String renameCounty(String prefix, String county) {
        return county;
    }

    public static void drawDisasterArea(boolean crop) throws IOException {
        double imageScale = 10;
        double strokeThickness = 3;
        Color disasterAreaColor = Color.red;
        Color disasterArea2Color = Color.blue;
        Color disasterArea3Color = Color.orange;
        int fontSize = 30;

        HashMap<MapItem, Color> colors = new HashMap<>();
        HashMap<MapItem, String> texts = new HashMap<>();
        String[] FLCounties = "Alachua,Baker,Bay,Bradford,Brevard,Broward,Calhoun,Charlotte,Citrus,Clay,Collier,Columbia,DeSoto,Dixie,Duval,Escambia,Flagler,Franklin,Gadsden,Gilchrist,Glades,Gulf,Hamilton,Hardee,Hendry,Hernando,Highlands,Hillsborough,Holmes,Indian River,Jackson,Jefferson,Lafayette,Lake,Lee,Leon,Levy,Liberty,Madison,Manatee,Marion,Martin,Miami-Dade,Monroe,Nassau,Okaloosa,Okeechobee,Orange,Osceola,Palm Beach,Pasco,Pinellas,Polk,Putnam,Santa Rosa,Sarasota,Seminole,St. Johns,St. Lucie,Sumter,Suwannee,Taylor,Union,Volusia,Wakulla,Walton,Washington".split(",");
        String[] ALCounties = "Houston,Geneva,Henry".split(",");
        String[] GACounties = "Seminole,Decatur,Grady,Thomas,Brooks,Lowndes,Lanier,Echols,Clinch,Ware,Charlton,Camden,Glynn,Early,Miller,Baker,Mitchell,Colquitt".split(",");

        MapPainter.DrawSetting[] settings = new MapPainter.DrawSetting[]{
            new MapPainter.DrawSetting("ResultMapFL.json", "FL", imageScale, (59.65 + 0) * imageScale, (339.9 + 0) * imageScale),
            new MapPainter.DrawSetting("ResultMapAL.json", "AL", imageScale * 0.748, 0, 0),
            new MapPainter.DrawSetting("ResultMapGA.json", "GA", imageScale * 0.726, (203.6 + 0) * imageScale, (0.9 + 0) * imageScale),
        };
//        for (MapItem mi : settings[0].getMis()) {
//            System.out.printf("name: %s%n", mi.getName());
//        }
        for (String FLCounty : FLCounties) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicIrma::renameCounty, FLCounty, "FL");
            if (mi != null) {
                colors.put(mi, disasterAreaColor);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }
        for (String ALCounty : ALCounties) {
            MapItem mi = findCounty(settings[1].getMis(), DataLogicIrma::renameCounty, ALCounty, "AL");
            if (mi != null) {
                colors.put(mi, disasterArea2Color);
                texts.put(mi, getShortCountyName(mi.getName()));
            }
        }
        for (String GACounty : GACounties) {
            MapItem mi = findCounty(settings[2].getMis(), DataLogicIrma::renameCounty, GACounty, "AL");
            if (mi != null) {
                colors.put(mi, disasterArea3Color);
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
        MapPainter.saveImage(img, PREFIX, "disasterArea.png");
    }

    public static void drawPopulationDensityDisasterArea(boolean crop) throws IOException {
        double imageScale = 10;
        double strokeThickness = 3;
        int fontSize = 20;
        float hue = 0, saturation = 100, minBrightness = 95.0f, maxBrightness = 5.0f;
        String[] FLCounties = "Alachua,Baker,Bay,Bradford,Brevard,Broward,Calhoun,Charlotte,Citrus,Clay,Collier,Columbia,DeSoto,Dixie,Duval,Escambia,Flagler,Franklin,Gadsden,Gilchrist,Glades,Gulf,Hamilton,Hardee,Hendry,Hernando,Highlands,Hillsborough,Holmes,Indian River,Jackson,Jefferson,Lafayette,Lake,Lee,Leon,Levy,Liberty,Madison,Manatee,Marion,Martin,Miami-Dade,Monroe,Nassau,Okaloosa,Okeechobee,Orange,Osceola,Palm Beach,Pasco,Pinellas,Polk,Putnam,Santa Rosa,Sarasota,Seminole,St. Johns,St. Lucie,Sumter,Suwannee,Taylor,Union,Volusia,Wakulla,Walton,Washington".split(",");
        String[] ALCounties = "Houston,Geneva,Henry".split(",");
        String[] GACounties = "Seminole,Decatur,Grady,Thomas,Brooks,Lowndes,Lanier,Echols,Clinch,Ware,Charlton,Camden,Glynn,Early,Miller,Baker,Mitchell,Colquitt".split(",");

        Gson gson = new Gson();
        CountyItem[] resultDataFL, resultDataAL, resultDataGA, resultData;
        try (FileReader reader = new FileReader("ResultDataFL.json")) {
            resultDataFL = gson.fromJson(reader, CountyItem[].class);
        }
        try (FileReader reader = new FileReader("ResultDataAL.json")) {
            resultDataAL = gson.fromJson(reader, CountyItem[].class);
        }
        try (FileReader reader = new FileReader("ResultDataGA.json")) {
            resultDataGA = gson.fromJson(reader, CountyItem[].class);
        }
        resultData = concatenate(concatenate(resultDataFL, resultDataAL), resultDataGA);

        MapPainter.DrawSetting[] settings = new MapPainter.DrawSetting[]{
            new MapPainter.DrawSetting("ResultMapFL.json", "FL", imageScale, (59.65 + 0) * imageScale, (339.9 + 0) * imageScale),
            new MapPainter.DrawSetting("ResultMapAL.json", "AL", imageScale * 0.748, 0, 0),
            new MapPainter.DrawSetting("ResultMapGA.json", "GA", imageScale * 0.726, (203.6 + 0) * imageScale, (0.9 + 0) * imageScale),
        };
        HashMap<String, MapItem> areas = new HashMap<>();
        for (String FLCounty : FLCounties) {
            MapItem mi = findCounty(settings[0].getMis(), DataLogicHarvey::renameCounty, FLCounty, "FL");
            if (mi != null) {
                areas.put(mi.getFIPS(), mi);
            }
        }
        for (String ALCounty : ALCounties) {
            MapItem mi = findCounty(settings[1].getMis(), DataLogicHarvey::renameCounty, ALCounty, "AL");
            if (mi != null) {
                areas.put(mi.getFIPS(), mi);
            }
        }
        for (String GACounty : GACounties) {
            MapItem mi = findCounty(settings[2].getMis(), DataLogicHarvey::renameCounty, GACounty, "GA");
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
}
