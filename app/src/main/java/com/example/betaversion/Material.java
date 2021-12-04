package com.example.betaversion;

/**
 * The type Material for creating a constructor for any material that in used.
 * @version  	1.0
 */
public class Material {
    private String typeOfMaterial;
    private int totalAmount;
    private int usedAmount;

    /**
     * Instantiates a new Material for the RT Firebase Database.
     */
    public Material(){}

    /**
     * Instantiates a new Material.
     *
     * @param typeOfMaterial the type of material
     * @param totalAmount    the total amount
     * @param usedAmount     the used amount
     */
    public Material(String typeOfMaterial, int totalAmount, int usedAmount) {
        this.typeOfMaterial = typeOfMaterial;
        this.totalAmount = totalAmount;
        this.usedAmount = usedAmount;
    }


    /**
     * Gets type of material.
     *
     * @return the type of material
     */
    public String getTypeOfMaterial() {
        return typeOfMaterial;
    }

    /**
     * Sets type of material.
     *
     * @param typeOfMaterial the type of material
     */
    public void setTypeOfMaterial(String typeOfMaterial) {
        this.typeOfMaterial = typeOfMaterial;
    }

    /**
     * Gets total amount.
     *
     * @return the total amount
     */
    public int getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets total amount.
     *
     * @param totalAmount the total amount
     */
    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets used amount.
     *
     * @return the used amount
     */
    public int getUsedAmount() {
        return usedAmount;
    }

    /**
     * Sets used amount.
     *
     * @param usedAmount the used amount
     */
    public void setUsedAmount(int usedAmount) {
        this.usedAmount = usedAmount;
    }
}
