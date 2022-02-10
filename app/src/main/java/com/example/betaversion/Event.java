package com.example.betaversion;

import java.util.ArrayList;

/**
 * The type Event for creating new event in the system of the business.
 * * @version  	3.0
 */
public class Event {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String dateOfEvent;
    private String dateOfCreation;
    private String eventName;
    private String eventLocation;
    private int eventCost;
    private String eventInformation;
    private String eventContent;
    private String eventCharacterize;
    private String eventPayment;
    private int eventEmployees;
    private ArrayList<Material> eventEquipments;
    private ArrayList<Mission> eventMissions;
    private ArrayList<Shows> eventShows;

    /**
     * Instantiates a new Event.
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
     * @param eventLocation     the event location
     * @param eventCost         the event cost
     * @param eventInformation  the event information
     * @param eventContent      the event content
     * @param eventCharacterize the event characterize
     * @param eventPayment      the event payment
     * @param eventEmployees    the event employees
     * @param eventEquipments   the event equipments
     * @param eventMissions     the event missions
     * @param eventShows        the event shows
     */
    public Event(String customerName, String customerPhone, String customerEmail, String dateOfEvent, String dateOfCreation, String eventName, String eventLocation, int eventCost, String eventInformation, String eventContent, String eventCharacterize, String eventPayment, int eventEmployees, ArrayList<Material> eventEquipments, ArrayList<Mission> eventMissions, ArrayList<Shows> eventShows){
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.dateOfEvent = dateOfEvent;
        this.dateOfCreation = dateOfCreation;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventCost = eventCost;
        this.eventInformation = eventInformation;
        this.eventContent = eventContent;
        this.eventCharacterize = eventCharacterize;
        this.eventPayment = eventPayment;
        this.eventEmployees = eventEmployees;
        this.eventEquipments = eventEquipments;
        this.eventMissions = eventMissions;
        this.eventShows = eventShows;
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
    public String getDateOfEvent() {
        return dateOfEvent;
    }

    /**
     * Sets date of event.
     *
     * @param dateOfEvent the date of event
     */
    public void setDateOfEvent(String dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }

    /**
     * Gets date of creation.
     *
     * @return the date of creation
     */
    public String getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Sets date of creation.
     *
     * @param dateOfCreation the date of creation
     */
    public void setDateOfCreation(String dateOfCreation) {
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
    public String getEventCharacterize() {
        return eventCharacterize;
    }

    /**
     * Sets event characterize.
     *
     * @param eventCharacterize the event characterize
     */
    public void setEventCharacterize(String eventCharacterize) {
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
     * Gets event equipments.
     *
     * @return the event equipments
     */
    public ArrayList<Material> getEventEquipments() {
        return eventEquipments;
    }

    /**
     * Sets event equipments.
     *
     * @param eventEquipments the event equipments
     */
    public void setEventEquipments(ArrayList<Material> eventEquipments) {
        this.eventEquipments = eventEquipments;
    }

    /**
     * Gets event missions.
     *
     * @return the event missions
     */
    public ArrayList<Mission> getEventMissions() {
        return eventMissions;
    }

    /**
     * Sets event missions.
     *
     * @param eventMissions the event missions
     */
    public void setEventMissions(ArrayList<Mission> eventMissions) {
        this.eventMissions = eventMissions;
    }

    /**
     * Gets event location.
     *
     * @return the event location
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets event location.
     *
     * @param eventLocation the event location
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * Gets event shows.
     *
     * @return the event shows
     */
    public ArrayList<Shows> getEventShows() {
        return eventShows;
    }

    /**
     * Sets event shows.
     *
     * @param eventShows the event shows
     */
    public void setEventShows(ArrayList<Shows> eventShows) {
        this.eventShows = eventShows;
    }
}
