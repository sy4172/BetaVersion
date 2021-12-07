package com.example.betaversion;

import java.util.ArrayList;

/**
 * The type Business equ for all the properties of the business.
 *
 * @version 2.0
 */
public class BusinessEqu {
    private ArrayList<Material> materials;
    private int totalEmployees;
    private int availableEmployees;
    private double efficiency; // (1-100)%
    private ArrayList<Shows> showsList;
    private ArrayList<Material> materialList;

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
     * @param materialList       the material list
     */
    public BusinessEqu(ArrayList<Material> materials, int totalEmployees, int availableEmployees, double efficiency, ArrayList<Shows> showsList, ArrayList<Material> materialList) {
        this.materials = materials;
        this.totalEmployees = totalEmployees;
        this.availableEmployees = availableEmployees;
        this.efficiency = efficiency;
        this.showsList = showsList;
        this.materialList = materialList;
    }

    /**
     * Gets materials.
     *
     * @return the materials
     */
    public ArrayList<Material> getMaterials() {
        return materials;
    }

    /**
     * Sets materials.
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

    /**
     * Gets shows list.
     *
     * @return the shows list
     */
    public ArrayList<Shows> getShowsList() {
        return showsList;
    }

    /**
     * Sets shows list.
     *
     * @param showsList the shows list
     */
    public void setShowsList(ArrayList<Shows> showsList) {
        this.showsList = showsList;
    }

    /**
     * Gets material list.
     *
     * @return the material list
     */
    public ArrayList<Material> getMaterialList() {
        return materialList;
    }

    /**
     * Sets material list.
     *
     * @param materialList the material list
     */
    public void setMaterialList(ArrayList<Material> materialList) {
        this.materialList = materialList;
    }
}
