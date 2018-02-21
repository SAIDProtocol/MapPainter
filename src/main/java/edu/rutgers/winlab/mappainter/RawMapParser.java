/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author jiachen
 */
public class RawMapParser {

    public static void parseMap(String mapFile, String outputMap, String outputContent) throws IOException, JDOMException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        LinkedList<MapItem> mapItems = new LinkedList<>();
        LinkedList<CountyItem> countyItems = new LinkedList<>();
        HashSet<String> FIPSs = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
            String line;
            int lineId = 0;
            SAXBuilder builder = new SAXBuilder();
            while ((line = reader.readLine()) != null) {
                lineId++;
                try (ByteArrayInputStream bais = new ByteArrayInputStream(line.getBytes())) {
                    Document doc = builder.build(bais);
                    Element e = doc.getRootElement();
                    if (e == null || !e.getName().equals("path")) {
                        System.out.printf("Cannot find element path in line %d, skip. %s%n", lineId, line);
                        continue;
                    }
                    String d = e.getAttributeValue("d");
                    if (d == null) {
                        System.out.printf("Cannot find attribute d in line %d, skip. %s%n", lineId, line);
                        continue;
                    }
                    String dataItem = e.getAttributeValue("data-item");
                    if (dataItem == null) {
                        System.out.printf("Cannot find attribute data-item in line %d, skip. %s%n", lineId, line);
                        continue;
                    }
                    DataItem di = gson.fromJson(dataItem, DataItem.class);
                    String fips = di.getCounty().getFIPS();
                    if (!FIPSs.add(fips)) {
                        System.out.printf("Duplicate FIPS %s in line %d, skip. %s%n", fips, lineId, line);
                        continue;
                    }
                    mapItems.push(new MapItem(d, fips, di.getCounty().getCTYNAME()));
                    countyItems.push(new CountyItem(di.getArea(), di.getCounty()));
                }
            }
        }
        try (FileWriter writer = new FileWriter(outputMap)) {
            gson.toJson(mapItems, writer);
            writer.flush();
        }
        try (FileWriter writer = new FileWriter(outputContent)) {
            gson.toJson(countyItems, writer);
            writer.flush();
        }
    }
}
