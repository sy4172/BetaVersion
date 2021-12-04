package com.example.betaversion;

import java.util.Date;

/**
 * The type Event for creating new event in the system of the business.
 * @version  	1.0
 */
public class Event {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Date dateOfEvent;
    private Date dateOfCreation;
    private String eventName;
    private int eventCost;
    private String eventInformation;
    private String eventContent;
    private char eventCharacterize;
    private String eventPayment;
    private int eventEmployees;
    private String [] eventEquipments;

    /**
     * Instantiates a new Event for the RT Firebase Database.
     */
    public Event (){}

    /**
     * Instantiates a new Event.
     *
     * @param customerName      the customer name
     * @param customerPhone     the customer phone
     * @param customerEmail     the customer email
     * @param dateOfEvent       the date of event
     * @param dateOfCreation    the date of creation
     * @param eventName         the event name
     * @param eventCost         the event cost
     * @param eventInformation  the event information
     * @param eventContent      the event content
     * @param eventCharacterize the event characterize
     * @param eventPayment      the event payment
     * @param eventEmployees    the event employees
     * @param eventEquipments   the event equipments
     */
    public Event(String customerName, String customerPhone, String customerEmail, Date dateOfEvent, Date dateOfCreation, String eventName, int eventCost, String eventInformation, String eventContent, char eventCharacterize, String eventPayment, int eventEmployees, String [] eventEquipments){
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.dateOfEvent = dateOfEvent;
        this.dateOfCreation = dateOfCreation;
        this.eventName = eventName;
        this.eventCost = eventCost;
        this.eventInformation = eventInformation;
        this.eventContent = eventContent;
        this.eventCharacterize = eventCharacterize;
        this.eventPayment = eventPayment;
        this.eventEmployees = eventEmployees;
        this.eventEquipments = eventEquipments;
    }

    /**
     * Gets customer name.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets customer name.
     *
     * @param customerName the customer name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets customer phone.
     *
     * @return the customer phone
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Sets customer phone.
     *
     * @param customerPhone the customer phone
     */
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * Gets customer email.
     *
     * @return the customer email
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Sets customer email.
     *
     * @param customerEmail the customer email
     */
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    /**
     * Gets date of event.
     *
     * @return the date of event
     */
    public Date getDateOfEvent() {
        return dateOfEvent;
    }

    /**
     * Sets date of event.
     *
     * @param dateOfEvent the date of event
     */
    public void setDateOfEvent(Date dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    /**
     * Gets date of creation.
     *
     * @return the date of creation
     */
    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Sets date of creation.
     *
     * @param dateOfCreation the date of creation
     */
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    /**
     * Gets event name.
     *
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets event name.
     *
     * @param eventName the event name
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets event cost.
     *
     * @return the event cost
     */
    public int getEventCost() {
        return eventCost;
    }

    /**
     * Sets event cost.
     *
     * @param eventCost the event cost
     */
    public void setEventCost(int eventCost) {
        this.eventCost = eventCost;
    }

    /**
     * Gets event information.
     *
     * @return the event information
     */
    public String getEventInformation() {
        return eventInformation;
    }

    /**
     * Sets event information.
     *
     * @param eventInformation the event information
     */
    public void setEventInformation(String eventInformation) {
        this.eventInformation = eventInformation;
    }

    /**
     * Gets event content.
     *
     * @return the event content
     */
    public String getEventContent() {
        return eventContent;
    }

    /**
     * Sets event content.
     *
     * @param eventContent the event content
     */
    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    /**
     * Gets event characterize.
     *
     * @return the event characterize
     */
    public char getEventCharacterize() {
        return eventCharacterize;
    }

    /**
     * Sets event characterize.
     *
     * @param eventCharacterize the event characterize
     */
    public void setEventCharacterize(char eventCharacterize) {
        this.eventCharacterize = eventCharacterize;
    }

    /**
     * Gets event payment.
     *
     * @return the event payment
     */
    public String getEventPayment() {
        return eventPayment;
    }

    /**
     * Sets event payment.
     *
     * @param eventPayment the event payment
     */
    public void setEventPayment(String eventPayment) {
        this.eventPayment = eventPayment;
    }

    /**
     * Gets event employees.
     *
     * @return the event employees
     */
    public int getEventEmployees() {
        return eventEmployees;
    }

    /**
     * Sets event employees.
     *
     * @param eventEmployees the event employees
     */
    public void setEventEmployees(int eventEmployees) {
        this.eventEmployees = eventEmployees;
    }

    /**
     * Get event equipments string [ ].
     *
     * @return the string [ ]
     */
    public String[] getEventEquipments() {
        return eventEquipments;
    }

    /**
     * Sets event equipments.
     *
     * @param eventEquipments the event equipments
     */
    public void setEventEquipments(String[] eventEquipments) {
        this.eventEquipments = eventEquipments;
    }
}
