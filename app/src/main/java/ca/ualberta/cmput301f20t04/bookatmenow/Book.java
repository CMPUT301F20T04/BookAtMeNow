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
        AcceptBorrow,
        AcceptReturn
    }

    private String title;
    private String author;
    private String isbn;
    private String status;
    private String location;
    private List<String> borrower;
    private List<String> owner;
    private List<String> requests;

    /**
     * constructs basic Book
     * @param title
     * @param author
     * @param isbn
     * @param status
     * @param owner
     */
    public Book(String title, String author, String isbn, String status, List<String> owner) {
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
    public Book(String title, String author, String isbn, String status, List<String> owner, List<String> borrower) {
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

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public List<String> getBorrower() {
        return borrower;
    }

    public void setBorrower(List<String> borrower) {
        this.borrower = borrower;
    }

    public List<String> getOwner() {
        return owner;
    }

    public void setOwner(List<String> owner) {
        this.owner = owner;
    }

    public List<String> getRequests() {
        return requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    /**
     * Add a request to the requests list. If this is the first request then set the book status
     * to "requested".
     * @param uuid of requesting user.
     */
    public void addRequest(String uuid) {
        if(this.noRequests()) {
            this.setStatus(ProgramTags.STATUS_REQUESTED);
        }
        this.requests.add(uuid);
    }

    /**
     * Check if a user uuid is present in the requests list.
     * @param uuid of user being checked.
     * @return boolean of whether user uuid was in request list.
     */
    public boolean checkForRequest(String uuid) {
        return requests.contains(uuid);
    }

    public boolean noRequests() {return (requests.size() == 1); }
}
