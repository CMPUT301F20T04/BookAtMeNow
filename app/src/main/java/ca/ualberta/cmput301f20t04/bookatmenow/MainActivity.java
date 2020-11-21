package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * List all books, list logged in user's books.
 * TODO: list all borrowed and requested books, search functionality
 */
public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addBookButton;
    private FloatingActionButton editProfileButton;
    private ImageButton filterButton;
    private Button searchButton;

    private Animation slideUp;
    private Animation slideDown;

    private EditText searchEditText;

    private TabLayout filterTabs;

    private String uuid;
    private String username;

    private enum MainActivityViews{
        ALL_BOOKS,
        MY_BOOKS,
        BORROWED,
        REQUESTED;

        /**
         * Allow this enum to be used like a C enum.
         *
         * @param i
         *      The equivalent integer to the MainActivityView
         * @return
         *      The corresponding enum value
         */
        public static MainActivityViews fromInt(int i) {
            switch (i) {
                default:
                case 0:
                    return ALL_BOOKS;
                case 1:
                    return MY_BOOKS;
                case 2:
                    return BORROWED;
                case 3:
                    return REQUESTED;
            }
        }

        public int toInt() {
            switch (this) {
                default:
                case ALL_BOOKS:
                    return 0;
                case MY_BOOKS:
                    return 1;
                case BORROWED:
                    return 2;
                case REQUESTED:
                    return 3;
            }
        }
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

        Log.d(ProgramTags.TEST_TAG, String.valueOf(MainActivityViews.MY_BOOKS));

        db = new DBHandler();

        bookList = findViewById(R.id.book_list);
        filteredBooks = new ArrayList<>();
        allBooksAdapter = new BookAdapter(MainActivity.this, filteredBooks);
        bookList.setAdapter(allBooksAdapter);
        uuid = getIntent().getStringExtra(FireStoreMapping.USER_FIELDS_ID);
        username = getIntent().getStringExtra(FireStoreMapping.USER_FIELDS_USERNAME);

        addBookButton = findViewById(R.id.floating_add);
        addBookButton.setVisibility(View.INVISIBLE);

        // animation adapted from https://stackoverflow.com/a/44145485
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_left);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_right);

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
        editProfileButton = findViewById(R.id.floating_edit_profile);
        searchButton = findViewById(R.id.search_btn);
        searchEditText = findViewById(R.id.search_bar);

        currentView = MainActivityViews.ALL_BOOKS;

        filterTabs = findViewById(R.id.filterTabs);

        filterButton = findViewById(R.id.filter);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = searchEditText.getText().toString().trim();
                if (search.length() > 0 && currentView == MainActivityViews.ALL_BOOKS) {
                    List<String> searchTerms = Arrays.asList(search.split(" "));
                    db.searchBooks(searchTerms,
                            new OnSuccessListener<List<Book>>() {
                                @Override
                                public void onSuccess(List<Book> books) {
                                    setViewMode(BookAdapter.ViewMode.ALL, books);
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

        filterTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentView = MainActivityViews.fromInt(tab.getPosition());
                Log.i("INDEX->", "Selected TAB Index - "+ tab.getPosition());

                switch (currentView) {
                    default:
                    case ALL_BOOKS:
                        db.getAllBooks(
                                new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {
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
                        break;

                    case MY_BOOKS:
                        db.getAllBooks(
                                new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {
                                        addBookButton.setVisibility(View.VISIBLE);
                                        addBookButton.startAnimation(slideUp);
                                        addBookButton.setEnabled(true);
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
                       break;

                    case BORROWED:
                        db.getAllBooks(
                                new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {
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
                       break;

                    case REQUESTED:
                        db.userRequests(
                                uuid,
                                new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {
                                        setViewMode(BookAdapter.ViewMode.REQUESTED, books);
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

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    addBookButton.setEnabled(false);
                    addBookButton.startAnimation(slideDown);
                    addBookButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                Log.d(ProgramTags.TEST_TAG, uuid);
                if (filteredBooks.get(pos).getOwner().get(0).equals(uuid)) {
                    Intent i = new Intent(MainActivity.this, MyBookActivity.class);
                    i.putExtra(ProgramTags.PASSED_ISBN, filteredBooks.get(pos).getIsbn());
                    i.putExtra(ProgramTags.PASSED_UUID, uuid);
                    if (currentView == MainActivityViews.MY_BOOKS) {
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
                                addBookButton.setEnabled(false);
                                addBookButton.startAnimation(slideDown);
                                addBookButton.setVisibility(View.INVISIBLE);
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