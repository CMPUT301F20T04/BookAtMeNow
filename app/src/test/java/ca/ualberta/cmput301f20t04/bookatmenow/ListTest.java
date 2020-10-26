package ca.ualberta.cmput301f20t04.bookatmenow;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class ListTest {
    private ArrayList<Book> mockDatabase() {
        User testBorrower = new User("test_borrower", "345def", "test@testing.com");
        User testRequester = new User("test_requester", "678ghi", "test@testing.net");

        ArrayList<Book> database = new ArrayList<>(Arrays.asList(
                new Book("A Tale of Two Cities", "Charles Dickens", "9781788280587"),
                new Book("Neuromancer", "William Gibson", "9780441569595", testBorrower.getUsername()),
                new Book("Dune", "Frank Herbert", "9780441172719"),
                new Book("C Programming: A Modern Approach", "K.N. King", "9780393979503"),
                new Book("The Rust Programming Language", "Steve Klabnik", "9781718500440"),
                new Book("Introduction to Algorithms", "CLRS", "9780262033848")
        ));

        database.get(0).setStatus(Book.StatusEnum.Pending.toString());
        database.get(0).setOwner(mockOwner().getUsername());
        database.get(0).setRequests(new ArrayList<>(Arrays.asList(testBorrower.getUsername(),
                testRequester.getUsername())));

        database.get(1).setStatus(Book.StatusEnum.Borrowed.toString());
        database.get(1).setOwner(mockOwner().getUsername());
        database.get(1).setBorrower(testBorrower.getUsername());

        database.get(2).setStatus(Book.StatusEnum.Available.toString());
        database.get(2).setOwner(mockOwner().getUsername());

        database.get(3).setStatus(Book.StatusEnum.Pending.toString());
        database.get(3).setOwner(mockOwner().getUsername());
        database.get(3).setRequests(new ArrayList<>(Arrays.asList(testRequester.getUsername())));

        database.get(4).setStatus(Book.StatusEnum.Pending.toString());
        database.get(4).setOwner(mockOwner().getUsername());
        database.get(4).setRequests(new ArrayList<>(Arrays.asList(testBorrower.getUsername())));

        database.get(5).setStatus(Book.StatusEnum.Borrowed.toString());
        database.get(5).setOwner(mockOwner().getUsername());
        database.get(5).setBorrower(testBorrower.getUsername());

        return database;
    }

    private User mockOwner() {
        return new User("test_owner", "123abc", "test@testing.ca");
    }

    @Test
    public void sortTest() {
        ArrayList<Book> database = mockDatabase();
        for (Book.StatusEnum status : Book.StatusEnum.values()) {
            Collections.sort(database, new BorrowList.CompareByStatus(status));

            // if a book with the current status exists in the database, assert it comes first
            for (Book book : database) {
                if (Book.StatusEnum.valueOf(book.getStatus()) == status) {
                    assertEquals(status, Book.StatusEnum.valueOf(database.get(0).getStatus()));
                    break;
                }
            }

            // ensure all books with the current status are located before those without
            int booksWithStatus = 0;
            while (Book.StatusEnum.valueOf(database.get(booksWithStatus).getStatus()) == status) {
                ++booksWithStatus;
            }

            for (int i = booksWithStatus; i < database.size(); ++i) {
                 assertNotEquals(status, Book.StatusEnum.valueOf(database.get(i).getStatus()));
            }
        }
    }
}
