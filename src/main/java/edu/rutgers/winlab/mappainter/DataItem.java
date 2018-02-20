/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.rutgers.winlab.mappainter;

/**
 *
 * @author jiachen
 */
public class DataItem {

    private long area;
    private County county;

    public long getArea() {
        return area;
    }

    public County getCounty() {
        return county;
    }

    @Override
    public String toString() {
        return "DataItem{" + "A=" + area + ", C=" + county + '}';
    }

}
