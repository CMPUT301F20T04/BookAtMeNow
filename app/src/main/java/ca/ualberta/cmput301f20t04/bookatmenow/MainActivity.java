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
import android.widget.TextView;
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
    private Button filterButton;
    private Button searchButton;

    private EditText searchEditText;

    private TabLayout filterTabs;

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
        editProfileButton = findViewById(R.id.floating_edit_profile);
        searchButton = findViewById(R.id.search_btn);
        searchEditText = findViewById(R.id.search_bar);

        currentView = MainActivityViews.HOME;

        filterTabs = findViewById(R.id.filterTabs);

        addBookButton = findViewById(R.id.floating_add);
        addBookButton.setVisibility(View.INVISIBLE);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

        final FloatingActionButton addBookButton = findViewById(R.id.floating_add);
        addBookButton.setVisibility(View.INVISIBLE);

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
                switch (tab.getPosition()) {
                    case 0:
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

                    case 1:
                        db.getAllBooks(
                                new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {
                                        addBookButton.setVisibility(View.VISIBLE);
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

                    case 2:
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

                    case 3:
                        Intent intent = new Intent(MainActivity.this, MyRequests.class);
                        intent.putExtra("uuid", uuid);
                        startActivityForResult(intent, MyRequests.REQUEST_ACTIVITY);

                    default:
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
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
                if (filteredBooks.get(pos).getOwner().equals(uuid)) {
                    Intent i = new Intent(MainActivity.this, MyBookActivity.class);
                    i.putExtra(ProgramTags.PASSED_ISBN, filteredBooks.get(pos).getIsbn());
                    i.putExtra(ProgramTags.PASSED_UUID, uuid);
                    if (!filterTabs.getChildAt(1).isSelected()) {
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

        final Button filterButton = findViewById(R.id.filter);

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