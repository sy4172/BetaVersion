package com.example.betaversion;

import android.media.MediaPlayer;

import java.io.File;
import java.util.Date;

/**
 * The type Reminder for the notes that related to the business management.
 * @version  	2.0
 */
public class Reminder {
    private String title;
    private boolean isText;
    private String textContent;
    private String audioContent;
    private String lastDateToRemind;

    /**
     * Instantiates a new Reminder for the RT Firebase Database.
     */
    public Reminder() {}

    /**
     * Instantiates a new Reminder.
     *
     * @param title            the title
     * @param isText           the is text
     * @param textContent      the text content
     * @param audioContent     the audio content
     * @param lastDateToRemind the last date to remind
     */
    public Reminder(String title, boolean isText, String textContent, String audioContent, String lastDateToRemind){
        this.title = title;
        this.isText = isText;
        this.textContent = textContent;
        this.audioContent = audioContent;
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
    public String getAudioContent() {
        return audioContent;
    }

    /**
     * Sets audio content.
     *
     * @param audioContent the audio content
     */
    public void setAudioContent(String audioContent) {
        this.audioContent = audioContent;
    }

    /**
     * Gets last date to remind.
     *
     * @return the last date to remind
     */
    public String getLastDateToRemind() {
        return lastDateToRemind;
    }

    /**
     * Sets last date to remind.
     *
     * @param lastDateToRemind the last date to remind
     */
    public void setLastDateToRemind(String lastDateToRemind) {
        this.lastDateToRemind = lastDateToRemind;
    }
}

