package com.example.betaversion;

import java.util.ArrayList;

/**
 * The type Business equ for all the properties of the business.
 * @version  	1.0
 */
public class BusinessEqu {
    private ArrayList<Material> materials;
    private int totalEmployees;
    private int availableEmployees;
    private double efficiency; // (1-100)%

    /**
     * Instantiates a new Business equ for the RT Firebase Database.
     *
     * @param materials          the materials
     * @param totalEmployees     the total employees
     * @param availableEmployees the available employees
     * @param efficiency         the efficiency of using the materiels
     */
    public BusinessEqu(ArrayList<Material> materials, int totalEmployees, int availableEmployees, double efficiency){
        this.materials = materials;
        this.totalEmployees = totalEmployees;
        this.availableEmployees = availableEmployees;
        this.efficiency = efficiency;
    }

    /**
     * Gets materials array.
     *
     * @return the materials
     */
    public ArrayList<Material> getMaterials() {
        return materials;
    }

    /**
     * Sets materials array.
     *
     * @param materials the materials
     */
    public void setMaterials(ArrayList<Material> materials) {
        this.materials = materials;
    }

    /**
     * Gets total employees.
     *
     * @return the total employees
     */
    public int getTotalEmployees() {
        return totalEmployees;
    }

    /**
     * Sets total employees.
     *
     * @param totalEmployees the total employees
     */
    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    /**
     * Gets available employees.
     *
     * @return the available employees
     */
    public int getAvailableEmployees() {
        return availableEmployees;
    }

    /**
     * Sets available employees.
     *
     * @param availableEmployees the available employees
     */
    public void setAvailableEmployees(int availableEmployees) {
        this.availableEmployees = availableEmployees;
    }

    /**
     * Gets efficiency.
     *
     * @return the efficiency
     */
    public double getEfficiency() {
        return efficiency;
    }

    /**
     * Sets efficiency.
     *
     * @param efficiency the efficiency
     */
    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }
}
