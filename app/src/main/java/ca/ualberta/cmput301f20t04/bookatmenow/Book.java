package ca.ualberta.cmput301f20t04.bookatmenow;

import android.media.Image;

import java.util.List;

public class Book {

    private String title;
    private String author;
    private String ISBN;
    private String status;
    private String borrower;
    private String owner;
    private List<String> requests;
    private Image image;

    /**
     * constructs basic Book
     * @param title
     * @param author
     * @param ISBN
     */
    public Book(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
    }

    /**
     * constructs Book with optional borrower
     * @param title
     * @param author
     * @param ISBN
     * @param borrower
     */
    public Book(String title, String author, String ISBN, String borrower) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.borrower = borrower;
    }

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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
