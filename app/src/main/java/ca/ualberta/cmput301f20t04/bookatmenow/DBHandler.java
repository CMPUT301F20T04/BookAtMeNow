package ca.ualberta.cmput301f20t04.bookatmenow;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Handler class
 * Deals with all DB interaction
 * Contains Transactions, Getters and Setters for DB data
 * All Getters return listeners to allow for activity on result
 */
public class DBHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Basic handler to create a DB instance
     */
    public DBHandler() {}

    /**
     * Adds a user to the database, expects user to not exist in DB, going forward with this will
     * override ALL previous user data, use carefully
     * DATA IS NOT MERGED
     * @param userToAdd
     *      User object, can come in any flavour and will simply autofill data as null
     * @param successListener
     *      Listener for success, returns True bool
     * @param failureListener
     *      Listener for failure
     */
    public void addUser(final User userToAdd, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        // <Field, data>
        HashMap<String, String> userData = new HashMap<String, String>();

        String randomID = String.valueOf(UUID.randomUUID());

        userData.put(FireStoreMapping.USER_FIELDS_ID, randomID);

        if(userToAdd.getUsername() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_USERNAME, userToAdd.getUsername());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_USERNAME, "");
        }

        if(userToAdd.getPassword() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_PASSWORD, userToAdd.getPassword());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_PASSWORD, "");
        }

        if(userToAdd.getPhone() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_PHONE, userToAdd.getPhone());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_PHONE, "");
        }

        if(userToAdd.getEmail() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_EMAIL, userToAdd.getEmail().toLowerCase());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_EMAIL, "");
        }

        if(userToAdd.getAddress() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, userToAdd.getAddress());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, "");
        }

        Task<Void> uploadTask = db.collection(FireStoreMapping.COLLECTIONS_USER)
                .document(randomID)
                .set(userData);

        uploadTask.continueWith(new Continuation<Void, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<Void> task) throws Exception {
                return true;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Removes a given user
     * @param uuid
     *      User's uuid, a string
     * @param successListener
     *      Listener that simply returns a True boolean when the task succeeds, a way for you to
     *      know when/if the task succeeded
     * @param failureListener
     *      Listener for when task fails
     */
    public void removeUser(String uuid, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        Task<Void> removeTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(uuid)
                .delete();

        removeTask.continueWith(new Continuation<Void, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<Void> task) throws Exception {
                return true;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Getter for user data, returns user object minus password
     * @param uuid
     *      User's uuid
     * @param successListener
     *      Listener to act on retrieved data
     * @param failureListener
     *      Listener to act when data not retrieved
     */
    public void getUser(String uuid, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(uuid)
                .get();

        userTask.continueWith(new Continuation<DocumentSnapshot, User>() {
            @Override
            public User then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot userData = task.getResult();
                User finalUser = new User();

                if (!userData.exists()) {
                    return null;
                }

                String username = userData.getString(FireStoreMapping.USER_FIELDS_USERNAME);
                String phone = userData.getString(FireStoreMapping.USER_FIELDS_PHONE);
                String email = userData.getString(FireStoreMapping.USER_FIELDS_EMAIL);
                String address = userData.getString(FireStoreMapping.USER_FIELDS_ADDRESS);

                finalUser.setUserID(userData.getId());
                finalUser.setUsername(username);
                finalUser.setPhone(phone);
                finalUser.setEmail(email);
                finalUser.setAddress(address);

                return finalUser;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Username checker method, checks if a given username exists in the DB; usernames ARE case
     * sensitive, returns UUID or null
     * @param username
     *      User's username, a string
     * @param onSuccessListener
     *      Listener for the query succeeding, returns a string
     * @param onFailureListener
     *      Listener for the query failing
     */
    public void usernameExists(String username, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        Task<QuerySnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .whereEqualTo(FireStoreMapping.USER_FIELDS_USERNAME, username)
                .get();

        userTask.continueWith(new Continuation<QuerySnapshot, String>() {
            @Override
            public String then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> userData = task.getResult().getDocuments();
                if (userData.size() > 0) {
                    return userData.get(0).getString(FireStoreMapping.USER_FIELDS_ID);
                } else {
                    return null;
                }
            }
        })
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Works identical to usernameExists, but checks for email; NOT case sensitive, returns UUID or
     * Null
     * @param email
     *      Email to check, a string
     * @param onSuccessListener
     *      Listener for success, returns string
     * @param onFailureListener
     *      Listener for failure
     */
    public void emailExists(String email, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        Task<QuerySnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .whereEqualTo(FireStoreMapping.USER_FIELDS_USERNAME, email.toLowerCase())
                .get();

        userTask.continueWith(new Continuation<QuerySnapshot, String>() {
            @Override
            public String then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> userData = task.getResult().getDocuments();
                if (userData.size() > 0) {
                    return userData.get(0).getString(FireStoreMapping.USER_FIELDS_ID);
                } else {
                    return null;
                }
            }
        })
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Password checker, assumes user exists and has a password, returns null if account does not
     * exist or account has no password
     * @param uuid
     *      User's email. a string
     * @param password
     *      User's password, a string
     * @param successListener
     *      Listener to handle when data is pulled
     * @param failureListener
     *      Listener to handle the failure of data pulling
     */
    public void checkPassword(String uuid, final String password, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(uuid)
                .get();

        userTask.continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot userData = task.getResult();

                if (!userData.exists()) {
                    Log.d(ProgramTags.DB_ERROR, "User does not exist, please verify user's existence prior to calling this method.");
                    return null;
                }

                if (userData.getString(FireStoreMapping.USER_FIELDS_PASSWORD) == null) {
                    Log.d(ProgramTags.DB_ERROR, "User does not have a password set. Is this a test account?");
                    return null;
                }

                return userData.getString(FireStoreMapping.USER_FIELDS_PASSWORD).equals(password);
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Rudimentary add book handler, does not do any checks and fills fields as empty when no data provided.
     * @param bookToAdd
     *      Book object containing complete or incomplete book data, handler will fill everything else with an empty string
     */
    public void addBook(Book bookToAdd) {
        // <Field, Data>
        HashMap<String, Object> bookData = new HashMap<String, Object>();
        if(bookToAdd.getTitle() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_TITLE, bookToAdd.getTitle());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_TITLE, "");
        }

        if(bookToAdd.getAuthor() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_AUTHOR, bookToAdd.getAuthor());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_AUTHOR, "");
        }

        if(bookToAdd.getIsbn() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_ISBN, bookToAdd.getIsbn());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_ISBN, "");
        }

        if(bookToAdd.getStatus() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_STATUS, bookToAdd.getStatus());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_STATUS, "");
        }

        if(bookToAdd.getBorrower() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_BORROWER, bookToAdd.getBorrower());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_BORROWER, "");
        }

        if(bookToAdd.getOwner() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_OWNER, bookToAdd.getOwner());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_OWNER, "");
        }

        if(bookToAdd.getRequests() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_REQUESTS, bookToAdd.getRequests());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_REQUESTS, Collections.singletonList("EMPTY"));
        }

        if(bookToAdd.getImage() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_IMAGE, bookToAdd.getImage());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_IMAGE, "");
        }

        db.collection(FireStoreMapping.COLLECTIONS_BOOK)
                .document(bookToAdd.getIsbn())
                .set(bookData);
    }

    /**
     * Get book handler, assumes book exists and tries to retrieve it
     * @param isbn
     *      String with ISBN for desired book
     * @param successListener
     *      Listener to act on success
     * @param failureListener
     *      Listener to act on failure
     */
    public void getBook(String isbn, OnSuccessListener<Book> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> bookTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .document(isbn)
                .get();

        bookTask.continueWith(new Continuation<DocumentSnapshot, Book>() {
            @Override
            public Book then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot bookData = task.getResult();
                Book finalBook = new Book();

                if (!bookData.exists()) {
                    return null;
                }

                String title = bookData.getString(FireStoreMapping.BOOK_FIELDS_TITLE);
                String author = bookData.getString(FireStoreMapping.BOOK_FIELDS_AUTHOR);
                String status = bookData.getString(FireStoreMapping.BOOK_FIELDS_STATUS);
                String borrower = bookData.getString(FireStoreMapping.BOOK_FIELDS_BORROWER);
                String owner = bookData.getString(FireStoreMapping.BOOK_FIELDS_OWNER);
                List<String> requests = bookData.toObject(ListAssist.class).requests;
                String image = bookData.getString(FireStoreMapping.BOOK_FIELDS_IMAGE);


                finalBook.setTitle(title);
                finalBook.setAuthor(author);
                finalBook.setIsbn(bookData.getId());
                finalBook.setStatus(status);
                finalBook.setBorrower(borrower);
                finalBook.setOwner(owner);
                finalBook.setRequests(requests);

                return finalBook;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Gives the provided success listener an array list of book objects
     * @param successListener
     *      Listener to act on the success of the query
     * @param failureListener
     *      Listener to act on the failure of a query
     */
    public void getAllBooks(OnSuccessListener<List<Book>> successListener, OnFailureListener failureListener) {
        Task<QuerySnapshot> bookTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .get();

        bookTask.continueWith(new Continuation<QuerySnapshot, List<Book>>() {
            @Override
            public List<Book> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> bookData = task.getResult().getDocuments();
                List<Book> books = new ArrayList<Book>();

                for (DocumentSnapshot doc: bookData) {
                    if (doc.exists()) {
                        Book finalBook = new Book();

                        String title = doc.getString(FireStoreMapping.BOOK_FIELDS_TITLE);
                        String author = doc.getString(FireStoreMapping.BOOK_FIELDS_AUTHOR);
                        String status = doc.getString(FireStoreMapping.BOOK_FIELDS_STATUS);
                        String borrower = doc.getString(FireStoreMapping.BOOK_FIELDS_BORROWER);
                        String owner = doc.getString(FireStoreMapping.BOOK_FIELDS_OWNER);
                        List<String> requests = doc.toObject(ListAssist.class).requests;
                        String image = doc.getString(FireStoreMapping.BOOK_FIELDS_IMAGE);

                        finalBook.setTitle(title);
                        finalBook.setAuthor(author);
                        finalBook.setIsbn(doc.getId());
                        finalBook.setStatus(status);
                        finalBook.setBorrower(borrower);
                        finalBook.setOwner(owner);
                        finalBook.setRequests(requests);

                        books.add(finalBook);
                    }
                }

                return books;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

}
