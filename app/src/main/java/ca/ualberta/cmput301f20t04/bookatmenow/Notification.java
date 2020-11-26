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

    private String uuid;
    private List<String> owner;
    private String type;
    private List<String> book;
    private List<String> borrower;
    private String timestamp;

    public Notification(String uuid, List<String> owner, String type, List<String> book, List<String> borrower, String timestamp) {
        this.uuid = uuid;
        this.owner = owner;
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
        this.borrower = borrower;
        this.timestamp = timestamp;
    }

    public Notification() {
        Date currentTime = Calendar.getInstance().getTime();
        this.timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(currentTime);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getOwner() {
        return owner;
    }

    public void setOwner(List<String> owner) {
        this.owner = owner;
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

    public List<String> getBorrower() {
        return borrower;
    }

    public void setBorrower(List<String> borrower) {
        this.borrower = borrower;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
