package com.example.betaversion;

import java.util.Date;

/**
 * The type Mission for creating mission for preparing to each event.
 * @version  	1.0
 */
public class Mission {
    private String title;
    private Date dateOfChange;
    private boolean isText;
    private String textContent;
    private String audioContent;

    /**
     * Instantiates a new Mission for the RT Firebase Database.
     */
    public Mission(){}

    /**
     * Instantiates a new Mission.
     *
     * @param title        the title
     * @param dateOfChange the date of change
     * @param isText       the is text
     * @param textContent  the text content
     * @param audioContent the audio content
     */
    public Mission(String title, Date dateOfChange, boolean isText, String textContent, String audioContent){
        this.title = title;
        this.dateOfChange = dateOfChange;
        this.isText = isText;
        this.textContent = textContent;
        this.audioContent = audioContent;
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
     * @param isTextUsed the is text used
     */
    public void setText(boolean isTextUsed) {
        isText = isTextUsed;
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
}
