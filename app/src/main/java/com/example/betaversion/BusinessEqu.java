package com.example.betaversion;

import java.util.HashMap;

/**
 * The type Business equ for all the properties of the business.
 *
 * @version 5.0
 */
public class BusinessEqu {
    private HashMap<String, Material> materials;
    private int totalEmployees;
    private int availableEmployees;
    private int efficiency; // (1-100)%
    private HashMap<String, Shows> showsList;

    /**
     * Instantiates a new Business equ for the RT Firebase Database.
     */
    public BusinessEqu(){}

    /**
     * Instantiates a new Business equ.
     *
     * @param materials          the materials
     * @param totalEmployees     the total employees
     * @param availableEmployees the available employees
     * @param efficiency         the efficiency
     * @param showsList          the shows list
     */
    public BusinessEqu(HashMap<String, Material> materials, int totalEmployees, int availableEmployees, int efficiency, HashMap<String, Shows> showsList) {
        this.materials = materials;
        this.totalEmployees = totalEmployees;
        this.availableEmployees = availableEmployees;
        this.efficiency = efficiency;
        this.showsList = showsList;
    }

    /**
     * Gets materials.
     *
     * @return the materials
     */
    public HashMap<String, Material> getMaterials() {
        return materials;
    }

    /**
     * Sets materials.
     *
     * @param materials the materials
     */
    public void setMaterials(HashMap<String, Material> materials) {
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
    public int getEfficiency() {
        return efficiency;
    }

    /**
     * Sets efficiency.
     *
     * @param efficiency the efficiency
     */
    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    /**
     * Gets shows list.
     *
     * @return the shows list
     */
    public HashMap<String, Shows> getShowsList() {
        return showsList;
    }

    /**
     * Sets shows list.
     *
     * @param showsList the shows list
     */
    public void setShowsList(HashMap<String, Shows> showsList) {
        this.showsList = showsList;
    }
}
