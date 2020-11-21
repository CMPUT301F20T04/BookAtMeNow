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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static ca.ualberta.cmput301f20t04.bookatmenow.ProfileActivity.validEmail;

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

        Task<Void> uploadTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .document(randomID)
                .set(userData);

        uploadTask.continueWith(new Continuation<Void, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<Void> task) throws Exception {
                Log.d(ProgramTags.DB_MESSAGE, "User added successfully");
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
                Log.d(ProgramTags.DB_MESSAGE, "User removed successfully");
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

                if (!userData.exists()) {
                    return null;
                }

                Log.d(ProgramTags.DB_MESSAGE, "User retrieved successfully");
                return convertToUser(userData);
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Conversion handler, takes user hash and converts to user object
     * @param data
     *      Data to convert
     * @return
     *      Returns completed user object
     */
    private User convertToUser(DocumentSnapshot data) {
        User finalUser = new User();

        String username = data.getString(FireStoreMapping.USER_FIELDS_USERNAME);
        String phone = data.getString(FireStoreMapping.USER_FIELDS_PHONE);
        String email = data.getString(FireStoreMapping.USER_FIELDS_EMAIL);
        String address = data.getString(FireStoreMapping.USER_FIELDS_ADDRESS);

        finalUser.setUserID(data.getId());
        finalUser.setUsername(username);
        finalUser.setPhone(phone);
        finalUser.setEmail(email);
        finalUser.setAddress(address);

        return finalUser;
    }

    /**
     * Updates the data of a given user
     * When updating user data DO NOT CHANGE UUID
     * If UUID is missing, method will end without making changes and log the error
     * If method does not return True, it failed, terminate immediately
     * @param userToAdd
     *      User object with all updated info
     * @param successListener
     *      Listener to act on successful update
     * @param failureListener
     *      Listener to act on failed update
     */
    public void updateUser(final User userToAdd, final OnSuccessListener<Boolean> successListener, final OnFailureListener failureListener) {
        if (userToAdd.getUserId() == null) {
            Log.d(ProgramTags.DB_ERROR, "Received empty UUID string, failed to add user, terminating operation.");
            return;
        }

        getUser(
                userToAdd.getUserId(),
                new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user != null) {
                            HashMap<String, Object> userData = new HashMap<>();

                            userData.put(FireStoreMapping.USER_FIELDS_ID, userToAdd.getUserId());

                            if (!userToAdd.getUsername().equals(user.getUsername())) {
                                userData.put(FireStoreMapping.USER_FIELDS_USERNAME, userToAdd.getUsername());
                            } else {
                                userData.put(FireStoreMapping.USER_FIELDS_USERNAME, user.getUsername());
                            }

                            if (!userToAdd.getPassword().equals(user.getPassword()) && userToAdd.getPassword().trim().length() > 0) {
                                userData.put(FireStoreMapping.USER_FIELDS_PASSWORD, userToAdd.getPassword());
                            } else {
                                userData.put(FireStoreMapping.USER_FIELDS_PASSWORD, user.getPassword());
                                Log.d(ProgramTags.DB_MESSAGE, "User password not changed.");
                            }

                            if (!userToAdd.getPhone().equals(user.getPhone())) {
                                userData.put(FireStoreMapping.USER_FIELDS_PHONE, userToAdd.getPhone());
                            } else {
                                userData.put(FireStoreMapping.USER_FIELDS_PHONE, user.getPhone());
                            }

                            if (!userToAdd.getEmail().equals(user.getEmail())) {
                                userData.put(FireStoreMapping.USER_FIELDS_EMAIL, userToAdd.getEmail().toLowerCase());
                            } else {
                                userData.put(FireStoreMapping.USER_FIELDS_EMAIL, user.getEmail());
                            }

                            if (!userToAdd.getAddress().equals(user.getAddress())) {
                                userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, userToAdd.getAddress());
                            } else {
                                userData.put(FireStoreMapping.USER_FIELDS_ADDRESS, user.getAddress());
                            }

                            Task<Void> updateTask = db
                                    .collection(FireStoreMapping.COLLECTIONS_USER)
                                    .document(userToAdd.getUserId())
                                    .set(userData);

                            updateTask.continueWith(new Continuation<Void, Boolean>() {
                                @Override
                                public Boolean then(@NonNull Task<Void> task) throws Exception {
                                    Log.d(ProgramTags.DB_MESSAGE, "User added successfully");
                                    return true;
                                }
                            })
                                    .addOnSuccessListener(successListener)
                                    .addOnFailureListener(failureListener);
                        } else {
                            Log.d(ProgramTags.DB_ERROR, "Unable to update user. Original user does no exist.");
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ProgramTags.DB_ERROR, "Failed to retrieve existing profile, terminating.");
                    }
                });

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
     * Login handler, sends user data into app given login criteria is met
     * @param user
     *      Username/email string
     * @param password
     *      Password string
     * @param successListener
     *      Listener for successful retrieval
     * @param failureListener
     *      Listener for failed retrieval
     */
    public void loginHandler(String user, final String password, OnSuccessListener<List<String>> successListener, OnFailureListener failureListener) {
        String type;
        if (validEmail(user)) {
            type = FireStoreMapping.USER_FIELDS_EMAIL;
        } else {
            type = FireStoreMapping.USER_FIELDS_USERNAME;
        }

        Task<QuerySnapshot> loginTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .whereEqualTo(type, user)
                .get();

        loginTask.continueWith(new Continuation<QuerySnapshot, List<String>>() {
            @Override
            public List<String> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> loginData = task.getResult().getDocuments();
                if (loginData.size() > 0) {
                    if (loginData.get(0).getString(FireStoreMapping.USER_FIELDS_PASSWORD).equals(password)) {
                        List<String> loginSuccess = new ArrayList<>();
                        loginSuccess.add(loginData.get(0).getId());
                        loginSuccess.add(loginData.get(0).getString(FireStoreMapping.USER_FIELDS_USERNAME));
                        return loginSuccess;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
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
    public void addBook(Book bookToAdd, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) throws Exception {
        // <Field, Data>
        HashMap<String, Object> bookData = new HashMap<String, Object>();
        List<String> tags = new ArrayList<>();

        if(bookToAdd.getTitle() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_TITLE, bookToAdd.getTitle());
            tags.addAll(Arrays.asList(bookToAdd.getTitle().toLowerCase().split(" ")));
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_TITLE, "");
        }

        if(bookToAdd.getAuthor() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_AUTHOR, bookToAdd.getAuthor());
            tags.addAll(Arrays.asList(bookToAdd.getAuthor().toLowerCase().split(" ")));
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_AUTHOR, "");
        }

        if(bookToAdd.getIsbn() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_ISBN, bookToAdd.getIsbn());
            tags.add(bookToAdd.getIsbn());
        } else {
            throw new Exception("Cannot add book without ISBN.");
        }

        bookData.put(FireStoreMapping.BOOK_FIELDS_DESCRIPTION, tags);

        if(bookToAdd.getStatus() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_STATUS, bookToAdd.getStatus());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_STATUS, "");
        }

        if(bookToAdd.getLocation() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_LOCATION, bookToAdd.getLocation());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_LOCATION, "");
        }

        if(bookToAdd.getBorrower() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_BORROWER, bookToAdd.getBorrower());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_BORROWER, new ArrayList<String>());
        }

        if(bookToAdd.getOwner() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_OWNER, bookToAdd.getOwner());
        } else {
            throw new Exception("Cannot add free-floating book. Assign to existing user.");
        }

        if(bookToAdd.getRequests() != null) {
            bookData.put(FireStoreMapping.BOOK_FIELDS_REQUESTS, bookToAdd.getRequests());
        } else {
            bookData.put(FireStoreMapping.BOOK_FIELDS_REQUESTS, Collections.singletonList("EMPTY"));
        }

        Task<Void> addTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .document(bookToAdd.getIsbn())
                .set(bookData);

        addTask.continueWith(new Continuation<Void, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<Void> task) throws Exception {
                Log.d(ProgramTags.DB_MESSAGE, "Book added successfully");
                return true;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Converter method to convert a book hash to a book object
     * @param data
     *      Takes in the hash data of a book
     * @return
     *      Returns a completed book object
     */
    private Book convertToBook(DocumentSnapshot data) {
        Book finalBook = new Book();

        String title = data.getString(FireStoreMapping.BOOK_FIELDS_TITLE);
        String author = data.getString(FireStoreMapping.BOOK_FIELDS_AUTHOR);
        String status = data.getString(FireStoreMapping.BOOK_FIELDS_STATUS);
        String location = data.getString(FireStoreMapping.BOOK_FIELDS_LOCATION);
        List<String> borrower = (List<String>) data.get(FireStoreMapping.BOOK_FIELDS_BORROWER);
        List<String> owner = (List<String>) data.get(FireStoreMapping.BOOK_FIELDS_OWNER);
        List<String> requests = (List<String>) data.get(FireStoreMapping.BOOK_FIELDS_REQUESTS);

        finalBook.setTitle(title);
        finalBook.setAuthor(author);
        finalBook.setIsbn(data.getId());
        finalBook.setStatus(status);
        finalBook.setBorrower(borrower);
        finalBook.setOwner(owner);
        finalBook.setRequests(requests);
        finalBook.setLocation(location);

        return finalBook;
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

                if (!bookData.exists()) {
                    return null;
                }

                Log.d(ProgramTags.DB_MESSAGE, "Book retrieved successfully");
                return convertToBook(bookData);
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
                List<Book> books = new ArrayList<>();

                for (DocumentSnapshot doc: bookData) {
                    if (doc.exists()) {
                        books.add(convertToBook(doc));
                    }
                }

                Log.d(ProgramTags.DB_MESSAGE, String.format("Retrieved %s books.", books.size()));
                return books;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Returns all books requested by specific UUID
     * @param uuid
     *      User ID of the user to check for
     * @param successListener
     *      Listener to act when data is successfully retrieved
     * @param failureListener
     *      Listener to act if data retrieval fails
     */
    public void userRequests(String uuid, OnSuccessListener<List<Book>> successListener, OnFailureListener failureListener) {
        Task<QuerySnapshot> requestTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .whereArrayContains(FireStoreMapping.BOOK_FIELDS_REQUESTS, uuid)
                .get();

        requestTask.continueWith(new Continuation<QuerySnapshot, List<Book>>() {
            @Override
            public List<Book> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> requestData = task.getResult().getDocuments();
                List<Book> books = new ArrayList<>();

                for (DocumentSnapshot doc: requestData) {
                    if (doc.exists()) {
                        books.add(convertToBook(doc));
                    }
                }

                Log.d(ProgramTags.DB_MESSAGE, String.format("Retrieved %s books.", books.size()));
                return books;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Basic book removal method.
     * @param isbn
     *      ISBN of a book to remove, a string
     * @param successListener
     *      Listener returning true if book is successfuly removed
     * @param failureListener
     *      Listener to act on DB failure
     */
    public void removeBook(String isbn, OnSuccessListener<Boolean> successListener, OnFailureListener failureListener) {
        Task<Void> removeTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .document(isbn)
                .delete();

        removeTask.continueWith(new Continuation<Void, Boolean>() {
            @Override
            public Boolean then(@NonNull Task<Void> task) throws Exception {
                Log.d(ProgramTags.DB_MESSAGE, "Book removed successfully");
                return true;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Special retriever, returns a list of all the users requesting a specific book
     * @param uuids
     *      List of UUIDs to be retrieved
     * @param successListener
     *      Success listener to act on retrieved list of user objects
     * @param failureListener
     *      Failure listener
     */
    public void bookRequests(List<String> uuids, OnSuccessListener<List<User>> successListener, OnFailureListener failureListener) {
        Task<QuerySnapshot> requestTask = db
                .collection(FireStoreMapping.COLLECTIONS_USER)
                .whereIn(FireStoreMapping.USER_FIELDS_ID, uuids)
                .get();

        requestTask.continueWith(new Continuation<QuerySnapshot, List<User>>() {
            @Override
            public List<User> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<DocumentSnapshot> requestData = task.getResult().getDocuments();

                List<User> users = new ArrayList<>();

                for (DocumentSnapshot doc: requestData) {
                    if (doc.exists()) {
                        users.add(convertToUser(doc));
                    }
                }

                Log.d(ProgramTags.DB_MESSAGE, String.format("Retrieved %s users.", users.size()));
                return users;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Text based search function, returns result based on search terms
     * @param terms
     *      String array of terms to search by
     * @param successListener
     *      Listener for successful retrieval
     * @param failureListener
     *      Listener for failed retrieval
     */
    public void searchBooks(List<String> terms, OnSuccessListener<List<Book>> successListener, OnFailureListener failureListener) {
        for (String i: terms) {
            terms.set(terms.indexOf(i), i.toLowerCase());
        }
        Task<QuerySnapshot> searchTask = db
                .collection(FireStoreMapping.COLLECTIONS_BOOK)
                .whereArrayContainsAny(FireStoreMapping.BOOK_FIELDS_DESCRIPTION, terms)
                .get();

        searchTask.continueWith(new Continuation<QuerySnapshot, List<Book>>() {
            @Override
            public List<Book> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                List<Book> searchResult = new ArrayList<>();
                for (DocumentSnapshot doc: task.getResult().getDocuments()) {
                    searchResult.add(convertToBook(doc));
                }
                return searchResult;
            }
        })
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
