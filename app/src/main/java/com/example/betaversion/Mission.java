package com.example.betaversion;

import android.media.MediaPlayer;

import java.util.Date;

/**
 * The type Mission for creating mission for preparing to each event.
 * @version  	2.0
 */
public class Mission {
    private String title;
    private boolean isText;
    private String textContent;
    private MediaPlayer audioContent;
    private Date dateOfChange;
    private int frequency; // According to a Spinner object.
    private Date lastDateToRemind;

    /**
     * Instantiates a new Mission for the RT Firebase Database.
     */
    public Mission() {}

    /**
     * Instantiates a new Mission.
     *
     * @param title            the title
     * @param isText           the is text
     * @param textContent      the text content
     * @param audioContent     the audio content
     * @param dateOfChange     the date of change
     * @param frequency        the frequency
     * @param lastDateToRemind the last date to remind
     */
    public Mission(String title, boolean isText, String textContent, MediaPlayer audioContent, Date dateOfChange, int frequency, Date lastDateToRemind){
        this.title = title;
        this.isText = isText;
        this.textContent = textContent;
        this.audioContent = audioContent;
        this.dateOfChange = dateOfChange;
        this.frequency = frequency;
        this.lastDateToRemind = lastDateToRemind;
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
     * Is text boolean.
     *
     * @return the boolean
     */
    public boolean isText() {
        return isText;
    }

    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(boolean text) {
        isText = text;
    }

    /**
     * Gets text content.
     *
     * @return the text content
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets text content.
     *
     * @param textContent the text content
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    /**
     * Gets audio content.
     *
     * @return the audio content
     */
    public MediaPlayer getAudioContent() {
        return audioContent;
    }

    /**
     * Sets audio content.
     *
     * @param audioContent the audio content
     */
    public void setAudioContent(MediaPlayer audioContent) {
        this.audioContent = audioContent;
    }

    /**
     * Gets date of change.
     *
     * @return the date of change
     */
    public Date getDateOfChange() {
        return dateOfChange;
    }

    /**
     * Sets date of change.
     *
     * @param dateOfChange the date of change
     */
    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets last date to remind.
     *
     * @return the last date to remind
     */
    public Date getLastDateToRemind() {
        return lastDateToRemind;
    }

    /**
     * Sets last date to remind.
     *
     * @param lastDateToRemind the last date to remind
     */
    public void setLastDateToRemind(Date lastDateToRemind) {
        this.lastDateToRemind = lastDateToRemind;
    }
}
