package ca.ualberta.cmput301f20t04.bookatmenow;

/**
 * A temporary book class to be used for testing until Corbin can finish the real one
 */
public class Book {
    String status;
    String isbn;
    String title;
    String author;
    String owner;

    // Labelled as it would appear from the toString() method for easier conversion
    public enum Status {
        Borrowed,
        Available,
        Pending,
    }

    String getStatus() { return status; }
    String getIsbn() { return isbn; }
    String getTitle() { return title; }
    String getAuthor() { return author; }
    String getOwner() { return owner; }
}
