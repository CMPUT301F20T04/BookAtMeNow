package ca.ualberta.cmput301f20t04.bookatmenow;

import android.media.Image;
import android.util.Log;

import java.util.List;

import javax.net.ssl.SSLEngineResult;

/**
 * Represents a book in the app.
 * Is owned by one user, has title, author, status (Available, Requested, Accepted, Borrowed), has a unique ISBN.
 * Can be borrowed by one user, have multiple requests on it.
 * Can have an image.
 * @author Warren Stix, Jeanne Coleongco
 */
public class Book {
    // In normal case for easier conversion to and from String
    public enum StatusEnum {
        Available,
        Requested,
        Accepted,
        Borrowed,
        Unavailable,
    }

    private String title;
    private String author;
    private String isbn;
    private String status;
    private String borrower;
    private String owner;
    private List<String> requests;
    private Image image;

    /**
     * constructs basic Book
     * @param title
     * @param author
     * @param isbn
     * @param status
     * @param owner
     */
    public Book(String title, String author, String isbn, String status, String owner) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        for (StatusEnum s : StatusEnum.values()) {
            if (s.name().equals(status)) {
                this.status = status;
            } else {
                this.status = "Available"; // acceptable default according to client
            }
        }
        this.owner = owner;
    }

    /**
     * constructs Book with optional borrower
     * @param title
     * @param author
     * @param isbn
     * @param status
     * @param owner
     * @param borrower
     */
    public Book(String title, String author, String isbn, String status, String owner, String borrower) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        for (StatusEnum s : StatusEnum.values()) {
            if (s.name().equals(status)) {
                this.status = status;
            } else {
                this.status = "Accepted"; // default due to the existence of a borrower
            }
        }
        this.owner = owner;
        this.borrower = borrower;
    }

    /**
     * Constructor for DB Handler
     */
    public Book() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getRequests() {
        return requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    /**
     * Add a request to the requests list.   If the requests list contains the string "EMPTY", then
     * this is the first request being added (so the uuid of the requester will be put at the start
     * of the list and the book status will be set to "requested").
     * @param uuid of requesting user.
     */
    public void addRequest(String uuid) {
        if(this.checkForRequest("EMPTY"))  {
            requests.set(0, uuid);
            this.setStatus(ProgramTags.STATUS_REQUESTED);
        } else {
            this.requests.add(uuid);
        }
    }

    /**
     * Check if a user uuid is present in the requests list.
     * @param uuid of user being checked.
     * @return boolean of whether user uuid was in request list.
     */
    public boolean checkForRequest(String uuid) {
        return requests.contains(uuid);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
