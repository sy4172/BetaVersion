package com.example.betaversion;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

/**
 * The type Mission for creating mission for preparing to each event.
 * @version  	2.0
 */
public class Mission implements Parcelable {
    private String title;
    private boolean isText;
    private String textContent;
    private String audioContentPath;
    private String dateForNotification;
    private int frequency; // According to a Spinner object.
    private String lastDateToRemind;

    /**
     * Instantiates a new Mission for the RT Firebase Database.
     */
    public Mission() {}

    /**
     * Instantiates a new Mission.
     *  @param title            the title
     * @param isText           the is text
     * @param textContent      the text content
     * @param audioContentPath     the audio content path
     * @param dateForNotification  the date of the notification
     * @param frequency        the frequency
     * @param lastDateToRemind the last date to remind
     */
    public Mission(String title, boolean isText, String textContent, String audioContentPath,
                   String dateForNotification, int frequency, String lastDateToRemind){
        this.title = title;
        this.isText = isText;
        this.textContent = textContent;
        this.audioContentPath = audioContentPath;
        this.dateForNotification = dateForNotification;
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
    public String getAudioContent() {
        return audioContentPath;
    }

    /**
     * Sets audio content.
     *
     * @param audioContent the audio content
     */
    public void setAudioContent(String audioContent) {
        this.audioContentPath = audioContent;
    }

    /**
     * Gets date of change.
     *
     * @return the date of change
     */
    public String getDateForNotification() {
        return dateForNotification;
    }

    /**
     * Sets date of change.
     *
     * @param dateForNotification the date of change
     */
    public void setdateForNotification(String dateForNotification) {
        this.dateForNotification = dateForNotification;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Mission(Parcel in) {
        title = in.readString();
        isText = in.readBoolean();
        textContent = in.readString();
        audioContentPath = in.readString();
        dateForNotification = in.readString();
        frequency = in.readInt();
        lastDateToRemind = in.readString();
    }

    public static final Creator<Mission> CREATOR = new Creator<Mission>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Mission createFromParcel(Parcel in) {
            return new Mission(in);
        }

        @Override
        public Mission[] newArray(int size) {
            return new Mission[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelOut, int i) {
        parcelOut.writeString(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcelOut.writeBoolean(isText);
        }
        parcelOut.writeString(textContent);
        parcelOut.writeString(audioContentPath);
        parcelOut.writeString(dateForNotification);
        parcelOut.writeInt(frequency);
        parcelOut.writeString(lastDateToRemind);
    }
}
