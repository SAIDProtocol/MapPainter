/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

import java.io.IOException;
import org.jdom2.JDOMException;

/**
 *
 * @author jiachen
 */
public class RawMapParserTest {

    public RawMapParserTest() {
    }

    @org.junit.Test
    public void test1() throws IOException, JDOMException {
//        RawMapParser.parseMap("RawMapTX.txt", "ResultMapTX.json", "ResultDataTX.json");
//        RawMapParser.parseMap("RawMapLA.txt", "ResultMapLA.json", "ResultDataLA.json");
        RawMapParser.parseMap("RawMapFL.txt", "ResultMapFL.json", "ResultDataFL.json");
        RawMapParser.parseMap("RawMapAL.txt", "ResultMapAL.json", "ResultDataAL.json");
        RawMapParser.parseMap("RawMapGA.txt", "ResultMapGA.json", "ResultDataGA.json");
    }

}
