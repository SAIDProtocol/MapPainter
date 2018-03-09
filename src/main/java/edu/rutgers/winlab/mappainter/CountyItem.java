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
public class CountyItem extends County {

    private long area;
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public long getArea() {
        return area;
    }

    public void setArea(long area) {
        this.area = area;
    }

    public CountyItem(long area, String FIPS, long Pop, int Year, double GrowthRate, String CTYNAME, double StatePercentage, int StateRank) {
        super(FIPS, Pop, Year, GrowthRate, CTYNAME, StatePercentage, StateRank);
        this.area = area;
    }

    public CountyItem(long area, County county) {
        this(area, county.getFIPS(), county.getPop(), county.getYear(), county.getGrowthRate(), county.getCTYNAME(), county.getStatePercentage(), county.getStateRank());
    }

    @Override
    public String toString() {
        super.toString();
        return "CountyItem{" + "area=" + area + '}';
    }

}
