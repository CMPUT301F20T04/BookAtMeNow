package ca.ualberta.cmput301f20t04.bookatmenow;

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
    private NotificationType type;
    private List<String> book;
    private List<String> borrower;
    private String timestamp;

    public Notification(String uuid, List<String> owner, NotificationType type, List<String> book, List<String> borrower, String timestamp) {
        this.uuid = uuid;
        this.owner = owner;
        this.type = type;
        this.book = book;
        this.borrower = borrower;
        this.timestamp = timestamp;
    }

    public Notification() {}

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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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
