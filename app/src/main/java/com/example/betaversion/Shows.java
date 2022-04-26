package com.example.betaversion;

/**
 * The type Shows that available in the business.
 * @version  	1.0
 */
public class Shows {
    private String showTitle;
    private String description;
    private int cost;
    private int employees;

    /**
     * Instantiates a new Shows for the RT Firebase Database.
     */
    public Shows() { }

    /**
     * Instantiates a new Shows.
     *
     * @param showTitle   the show title
     * @param description the description
     * @param cost        the cost
     */
    public Shows(String showTitle, String description, int cost, int employees) {
        this.showTitle = showTitle;
        this.description = description;
        this.cost = cost;
        this.employees = employees;
    }

    /**
     * Gets show title.
     *
     * @return the show title
     */
    public String getShowTitle() {
        return showTitle;
    }

    /**
     * Sets show title.
     *
     * @param showTitle the show title
     */
    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets cost.
     *
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets cost.
     *
     * @param cost the cost
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }
}
