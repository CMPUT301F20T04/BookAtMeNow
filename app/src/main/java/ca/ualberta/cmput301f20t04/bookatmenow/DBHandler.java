package ca.ualberta.cmput301f20t04.bookatmenow;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Handler class
 * Deals with all DB interaction
 * Contains Transactions, Getters and Setters for DB data
 * All Getters return listeners to allow for activity on result
 */
public class DBHandler {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String TAG = "DBCoreHandler";

    /**
     * Basic handler to create a DB instance
     */
    public DBHandler() {}

    /**
     * Adds a user to the database, is not currently implemented with proper handlers, use at risk
     * @param userToAdd
     *      User object, can come in any flavour and will simply autofill data as null
     */
    public void addUser(final User userToAdd) {
        // <Field, data>
        HashMap<String, String> userData = new HashMap<String, String>();

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
            userData.put(FireStoreMapping.USER_FIELDS_EMAIL, userToAdd.getEmail());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_EMAIL, "");
        }

        if(userToAdd.getAddress() != null) {
            userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, userToAdd.getAddress());
        } else {
            userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, "");
        }

        db.collection(FireStoreMapping.COLLECTIONS_USER)
                .document(userToAdd.getEmail())
                .set(userData);

    }

    /**
     * Getter for user data, returns user object minus password
     * @param email
     *      User's email
     * @param successListener
     *      Listener to act on retrieved data
     * @param failureListener
     *      Listener to act when data not retrieved
     */
    public void getUser(String email, OnSuccessListener<User> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(email)
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
                String address = userData.getString(FireStoreMapping.USER_FIELDS_ADDRESS);

                finalUser.setUsername(username);
                finalUser.setPhone(phone);
                finalUser.setEmail(userData.getId());
                finalUser.setAddress(address);

                return finalUser;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }


    /**
     * User existence checker, takes in a user and assumes success.fail activities
     * @param email
     *      User's email to be checked on DB
     * @param successListener
     *      Listener to handle if search succeeds
     * @param failureListener
     *      Listener to handle if search fails
     */
    public void userExist(String email, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(email)
                .get();

        userTask.continueWith(new Continuation<DocumentSnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                DocumentSnapshot userData = task.getResult();
                return userData.exists();
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Username checker method, checks if a given username exists in the DB; usernames are NOT case
     * sensitive
     * @param username
     *      User's username, a string
     * @param onSuccessListener
     *      Listener for the query succeeding, returns a bool
     * @param onFailureListener
     *      Listener for the query failing
     */
    public void usernameExists(String username, OnSuccessListener<Boolean> onSuccessListener, OnFailureListener onFailureListener) {
        Task<QuerySnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .whereEqualTo(FireStoreMapping.USER_FIELDS_USERNAME, username)
                .get();

        userTask.continueWith(new Continuation<QuerySnapshot, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> userData = task.getResult().getDocuments();
                return userData.size() > 0;
            }
        })
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Password checker, assumes user exists and has a password, returns null if account does not
     * exist or account has no password
     * @param email
     *      User's email. a string
     * @param password
     *      User's password, a string
     * @param successListener
     *      Listener to handle when data is pulled
     * @param failureListener
     *      Listener to handle the failure of data pulling
     */
    public void checkPassword(String email, final String password, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        Task<DocumentSnapshot> userTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(email)
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
