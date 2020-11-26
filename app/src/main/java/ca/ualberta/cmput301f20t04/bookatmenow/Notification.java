package ca.ualberta.cmput301f20t04.bookatmenow;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Notification {
    public enum NotificationType {
        Request,
        Approve,
        Reject,
        Return
    }

    private String selfUUID;
    private String receiveUUID;
    private List<String> sender;
    private String type;
    private List<String> book;
    private String timestamp;

    public Notification(String receiveUUID, List<String> sender, String type, List<String> book, String timestamp) {
        this.receiveUUID = receiveUUID;
        this.sender = sender;
        this.type = type;
        for (NotificationType t : NotificationType.values()) {
            if (t.name().equals(type)) {
                this.type = type;
            }
        }
        if(this.type != null) {
            Log.e(ProgramTags.NOTIFICATION_ERROR, String.format("%s is not a valid notification type.", type));
        }
        this.book = book;
        this.timestamp = timestamp;
    }

    public Notification() {
        Date currentTime = Calendar.getInstance().getTime();
        this.timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(currentTime);
    }

    public String getReceiveUUID() {
        return receiveUUID;
    }

    public void setReceiveUUID(String receiveUUID) {
        this.receiveUUID = receiveUUID;
    }

    public List<String> getSender() {
        return sender;
    }

    public void setSender(List<String> sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        for (NotificationType t : NotificationType.values()) {
            if (t.name().equals(type)) {
                this.type = type;
            }
        }
        if(this.type != null) {
            Log.e(ProgramTags.NOTIFICATION_ERROR, String.format("%s is not a valid notification type.", type));
        }
    }

    public List<String> getBook() {
        return book;
    }

    public void setBook(List<String> book) {
        this.book = book;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSelfUUID(String uuid) {
        this.selfUUID = uuid;
    }

    public String getSelfUUID() {
        return this.selfUUID;
    }
}
