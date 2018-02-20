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
public class County {

    private final String FIPS;
    private final long Pop;
    private final int Year;
    private final double GrowthRate;
    private final String CTYNAME;
    private final double StatePercentage;
    private final int StateRank;

    public County(String FIPS, long Pop, int Year, double GrowthRate, String CTYNAME, double StatePercentage, int StateRank) {
        this.FIPS = FIPS;
        this.Pop = Pop;
        this.Year = Year;
        this.GrowthRate = GrowthRate;
        this.CTYNAME = CTYNAME;
        this.StatePercentage = StatePercentage;
        this.StateRank = StateRank;
    }

    public String getFIPS() {
        return FIPS;
    }

    public long getPop() {
        return Pop;
    }

    public int getYear() {
        return Year;
    }

    public double getGrowthRate() {
        return GrowthRate;
    }

    public String getCTYNAME() {
        return CTYNAME;
    }

    public double getStatePercentage() {
        return StatePercentage;
    }

    public int getStateRank() {
        return StateRank;
    }

    @Override
    public String toString() {
        return "County{" + "F=" + FIPS + ", P=" + Pop + ", Y=" + Year + ", G=" + GrowthRate + ", N=" + CTYNAME + ", P=" + StatePercentage + ", R=" + StateRank + '}';
    }

}
