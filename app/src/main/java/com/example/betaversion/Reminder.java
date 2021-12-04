package com.example.betaversion;

import java.util.Date;

/**
 * The type Reminder for the events system.
 * @version  	1.0
 */
public class Reminder {
    private String title;
    private Date lastDateReminder;
    private String frequency; // need to create it: based on the user choice form a spinner

    /**
     * Instantiates a new Reminder for the RT Firebase Database.
     */
    public Reminder (){}

    /**
     * Instantiates a new Reminder.
     *
     * @param title            the title
     * @param lastDateReminder the last date reminder
     * @param frequency        the frequency
     */
    public Reminder(String title, Date lastDateReminder, String frequency){
        this.title = title;
        this.lastDateReminder = lastDateReminder;
        this.frequency = frequency;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets last date reminder.
     *
     * @return the last date reminder
     */
    public Date getLastDateReminder() {
        return lastDateReminder;
    }

    /**
     * Sets last date reminder.
     *
     * @param lastDateReminder the last date reminder
     */
    public void setLastDateReminder(Date lastDateReminder) {
        this.lastDateReminder = lastDateReminder;
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}

