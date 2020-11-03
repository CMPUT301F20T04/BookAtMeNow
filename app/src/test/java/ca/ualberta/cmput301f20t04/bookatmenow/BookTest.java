package ca.ualberta.cmput301f20t04.bookatmenow;

import org.junit.Test;
import static org.junit.Assert.*;

public class BookTest {
    @Test
    public void testBookBasic() {
        Book book = new Book("Best Book Ever", "Best Author Ever", "1234567891011", "Available", "bestownerever");
        assertEquals("Best Book Ever", book.getTitle());
        assertEquals("Best Author Ever", book.getAuthor());
        assertEquals("1234567891011", book.getIsbn());
        assertEquals("Available", book.getStatus());
        assertEquals("bestownerever", book.getOwner());
    }

    @Test
    public void testBookComplete() {
        Book book = new Book("Best Book Ever", "Best Author Ever", "1234567891011", "Available", "bestownerever", "bestborrowerever");
        assertEquals("Best Book Ever", book.getTitle());
        assertEquals("Best Author Ever", book.getAuthor());
        assertEquals("1234567891011", book.getIsbn());
        assertEquals("Available", book.getStatus());
        assertEquals("bestownerever", book.getOwner());
        assertEquals("bestborrowerever", book.getBorrower());
    }

    @Test
    public void testInvalidStatus() {
        Book book = new Book("Best Book Ever", "Best Author Ever", "1234567891011", "Invalid", "bestownerever", "bestborrowerever");
        assertEquals("Best Book Ever", book.getTitle());
        assertEquals("Best Author Ever", book.getAuthor());
        assertEquals("1234567891011", book.getIsbn());
        assertEquals("Available", book.getStatus());
        assertEquals("bestownerever", book.getOwner());
        assertEquals("bestborrowerever", book.getBorrower());
    }

    @Test
    public void testBookChange() {
        Book book = new Book("Best Book Ever", "Best Author Ever", "1234567891011", "Available", "bestownerever", "bestborrowerever");
        assertEquals("Best Book Ever", book.getTitle());
        assertEquals("Best Author Ever", book.getAuthor());
        assertEquals("1234567891011", book.getIsbn());
        assertEquals("Available", book.getStatus());
        assertEquals("bestownerever", book.getOwner());
        assertEquals("bestborrowerever", book.getBorrower());

        book.setTitle("Better Title");
        book.setAuthor("Better Author");
        book.setIsbn("1234567891012");
        book.setStatus("Requested");
        book.setOwner("betterowner");
        book.setBorrower("betterborrower");

        assertEquals("Better Title", book.getTitle());
        assertEquals("Better Author", book.getAuthor());
        assertEquals("1234567891012", book.getIsbn());
        assertEquals("Requested", book.getStatus());
        assertEquals("betterowner", book.getOwner());
        assertEquals("betterborrower", book.getBorrower());
    }

}
