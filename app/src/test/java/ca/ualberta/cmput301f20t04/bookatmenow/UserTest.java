package ca.ualberta.cmput301f20t04.bookatmenow;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testUserBasic() {
        User user = new User("name", "password", "email@email.com");
        assertEquals(user.getUsername(), "name");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getEmail(), "email@email.com");
    }

    @Test
    public void testUserWithPhone() {
        User user = new User("name", "password", "1234567", "email@email.com");
        assertEquals(user.getUsername(), "name");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getPhone(), "1234567");
        assertEquals(user.getEmail(), "email@email.com");
    }

    @Test
    public void testUserWithAddress() {
        User user = new User("name", "password", "1234 House", "email@email.com");
        assertEquals(user.getUsername(), "name");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getAddress(), "1234 House");
        assertEquals(user.getEmail(), "email@email.com");
    }

    @Test
    public void testUserComplete() {
        User user = new User("name", "password", "1234567","email@email.com", "1234 House");
        assertEquals(user.getUsername(), "name");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getPhone(), "1234567");
        assertEquals(user.getAddress(), "1234 House");
        assertEquals(user.getEmail(), "email@email.com");
    }

    @Test
    public void testUserChange() {
        User user = new User("name", "password", "1234567", "email@email.com", "1234 House");
        assertEquals(user.getUsername(), "name");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getPhone(), "1234567");
        assertEquals(user.getAddress(), "1234 House");
        assertEquals(user.getEmail(), "email@email.com");

        user.setUsername("new name");
        user.setPassword("newpw");
        user.setPhone("5678910");
        user.setAddress("5678 Apartment");
        user.setEmail("newemail@email.com");

        assertEquals(user.getUsername(), "new name");
        assertEquals(user.getPassword(), "newpw");
        assertEquals(user.getPhone(), "5678910");
        assertEquals(user.getAddress(), "5678 Apartment");
        assertEquals(user.getEmail(), "newemail@email.com");
    }
}
