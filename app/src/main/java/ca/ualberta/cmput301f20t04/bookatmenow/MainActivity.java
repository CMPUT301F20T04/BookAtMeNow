package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List all books, list logged in user's books.
 * TODO: list all borrowed and requested books, search functionality
 */
public class MainActivity extends AppCompatActivity {

    private Button addBookButton;
    private Button homeButton;

    private Button editProfileButton;
    private Button myBooksButton;
    private Button borrowedButton;
    private Button requestedButton;

    private Button filterButton;
    private Button searchButton;

    private EditText searchEditText;

    private String uuid;

    private enum MainActivityViews{
        HOME,
        MY_BOOKS,
        BORROWED,
        REQUESTED,
    }
    private MainActivityViews currentView;

    ListView bookList;
    BookAdapter allBooksAdapter;
    ArrayList<Book> filteredBooks;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler();

        bookList = findViewById(R.id.book_list);
        filteredBooks = new ArrayList<>();
        allBooksAdapter = new BookAdapter(MainActivity.this, filteredBooks);
        bookList.setAdapter(allBooksAdapter);
        uuid = getIntent().getStringExtra("uuid");

        db.getAllBooks(new OnSuccessListener<List<Book>>() {
                           @Override
                           public void onSuccess(List<Book> books) {
                               filteredBooks.clear();
                               filteredBooks.addAll(books);
                               allBooksAdapter.notifyDataSetChanged();
                               Log.d(ProgramTags.DB_ALL_FOUND, "All books in database successfully found");
                               setUi(filteredBooks);
                           }
                       },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ProgramTags.DB_ERROR, "Not all books could be found!" + e.toString());
                    }
                });
    }

    private void setUi(final ArrayList<Book> filteredBooks) {
        // menu buttons
        editProfileButton = findViewById(R.id.edit_profile);
        myBooksButton = findViewById(R.id.my_books);
        borrowedButton = findViewById(R.id.borrowed);
        requestedButton = findViewById(R.id.requested);
        filterButton = findViewById(R.id.filter);
        searchButton = findViewById(R.id.search_btn);

        searchEditText = findViewById(R.id.search_bar);

        currentView = MainActivityViews.HOME;

        addBookButton = findViewById(R.id.add);
        addBookButton.setVisibility(View.INVISIBLE);
        homeButton = findViewById(R.id.home);
        homeButton.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = searchEditText.getText().toString().trim();
                if (search.length() > 0 && currentView == MainActivityViews.HOME) {
                    List<String> searchTerms = Arrays.asList(search.split(" "));
                    db.searchBooks(searchTerms,
                            new OnSuccessListener<List<Book>>() {
                                @Override
                                public void onSuccess(List<Book> books) {
                                    setViewMode(BookAdapter.ViewMode.ALL, books);
                                    homeButton.setVisibility(View.VISIBLE);
                                    searchEditText.setText("");
                                }
                            },
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

        final Button addBookButton = findViewById(R.id.add);
        addBookButton.setVisibility(View.INVISIBLE);
        final Button homeButton = findViewById(R.id.home);
        homeButton.setVisibility(View.INVISIBLE);

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getAllBooks(
                        new OnSuccessListener<List<Book>>() {
                            @Override
                            public void onSuccess(List<Book> books) {
                                enableMenuButtons();
                                addBookButton.setVisibility(View.VISIBLE);
                                homeButton.setVisibility(View.VISIBLE);
                                myBooksButton.setEnabled(false);
                                setViewMode(BookAdapter.ViewMode.OWNED, books);
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                Log.d(ProgramTags.TEST_TAG, uuid);
                if (filteredBooks.get(pos).getOwner().equals(uuid)) {
                    Intent i = new Intent(MainActivity.this, MyBookActivity.class);
                    i.putExtra(ProgramTags.PASSED_ISBN, filteredBooks.get(pos).getIsbn());
                    i.putExtra(ProgramTags.PASSED_UUID, uuid);
                    if (!myBooksButton.isEnabled()) {
                        startActivityForResult(i, MyBookActivity.CHANGE_BOOK_FROM_MYBOOKS);
                    } else {
                        startActivityForResult(i, MyBookActivity.CHANGE_BOOK_FROM_MAIN);
                    }
                } else {
                    Intent i = new Intent(MainActivity.this, ABookActivity.class);
                    i.putExtra(ProgramTags.PASSED_ISBN, filteredBooks.get(pos).getIsbn());
                    i.putExtra(ProgramTags.PASSED_UUID, uuid);
                    startActivity(i);
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getAllBooks(
                        new OnSuccessListener<List<Book>>() {
                            @Override
                            public void onSuccess(List<Book> books) {
                                addBookButton.setVisibility(View.INVISIBLE);
                                homeButton.setVisibility(View.INVISIBLE);
                                enableMenuButtons();
                                setViewMode(BookAdapter.ViewMode.ALL, books);
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyBookActivity.class);
                i.putExtra(ProgramTags.PASSED_UUID, uuid);
                startActivityForResult(i, MyBookActivity.ADD_BOOK);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterDialog().show(getSupportFragmentManager(), "Filter Books");
            }
        });

        borrowedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getAllBooks(
                        new OnSuccessListener<List<Book>>() {
                            @Override
                            public void onSuccess(List<Book> books) {
                                enableMenuButtons();
                                borrowedButton.setEnabled(false);
                                setViewMode(BookAdapter.ViewMode.BORROWED, books);
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        });

        requestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MyRequests.class);
                intent.putExtra("uuid", uuid);
                startActivityForResult(intent, MyRequests.REQUEST_ACTIVITY);
            }
        });
    }

    /**
     *
     * @param viewMode
     * @param allBooks
     */
    private void setViewMode(BookAdapter.ViewMode viewMode, List<Book> allBooks)    {
        filteredBooks.clear();

        if (viewMode == BookAdapter.ViewMode.ALL) {
            filteredBooks.addAll(allBooks);
        } else {
            for (Book book : allBooks) {
                if (BookAdapter.checkUser(book, uuid, viewMode)) {
                    filteredBooks.add(book);
                }
            }
        }
        allBooksAdapter.notifyDataSetChanged();
    }

    private void enableMenuButtons() {
        editProfileButton.setEnabled(true);
        myBooksButton.setEnabled(true);
        borrowedButton.setEnabled(true);
        requestedButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        db.getAllBooks(
                new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                        try {
                            if (requestCode == MyBookActivity.ADD_BOOK || requestCode == MyBookActivity.CHANGE_BOOK_FROM_MYBOOKS) {
                                setViewMode(BookAdapter.ViewMode.OWNED, books);
                            } else {
                                setViewMode(BookAdapter.ViewMode.ALL, books);
                                enableMenuButtons();
                                addBookButton.setVisibility(View.INVISIBLE);
                                homeButton.setVisibility(View.INVISIBLE);
                            }
                            Log.d(ProgramTags.GENERAL_SUCCESS, "Book list updated.");
                        } catch (Exception e) {
                            Log.d(ProgramTags.GENERAL_ERROR, String.format("Failed to update book list with error %s", e));
                            Toast.makeText(getApplicationContext(), "Failed to update book list.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}