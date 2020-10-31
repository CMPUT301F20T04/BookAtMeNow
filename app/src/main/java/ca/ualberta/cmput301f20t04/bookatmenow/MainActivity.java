package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button editProfileButton;
    private Button myBooksButton;
    private Button borrowedButton;
    private Button requestedButton;

    private Button addBookButton;
    private Button homeButton;

    private String uuid;

    ListView bookList;
    BorrowList allBooksAdapter;
    ArrayList<Book> filteredBooks;
    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHandler();

        bookList = findViewById(R.id.book_list);
        filteredBooks = new ArrayList<>();
        allBooksAdapter = new BorrowList(MainActivity.this, filteredBooks);
        bookList.setAdapter(allBooksAdapter);

        db.getAllBooks(new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                       filteredBooks.addAll(books);
                       allBooksAdapter.notifyDataSetChanged();
                       Log.d(ProgramTags.DB_ALL_FOUND, "All books in database successfully found");
                       setUi(books, filteredBooks);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ProgramTags.DB_ERROR, "Not all books could be found!" + e.toString());
                    }
                });
    }

    private void setUi(final List<Book> books, final ArrayList<Book> filteredBooks) {
        uuid = getIntent().getStringExtra("uuid");

        // menu buttons
        Button editProfileButton = findViewById(R.id.edit_profile);
        Button myBooksButton = findViewById(R.id.my_books);
        Button borrowedButton = findViewById(R.id.borrowed);
        Button requestedButton = findViewById(R.id.requested);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

        final Button addBookButton = findViewById(R.id.add);
        addBookButton.setVisibility(View.GONE);
        final Button homeButton = findViewById(R.id.home);
        homeButton.setVisibility(View.GONE);

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.VISIBLE);
                homeButton.setVisibility(View.VISIBLE);

                if (uuid != null) {
                    setViewMode(BorrowList.ViewMode.OWNED, books, filteredBooks, uuid);

                    bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
                            Intent i = new Intent(MainActivity.this, MyBookActivity.class);
                            i.putExtra(ProgramTags.PASSED_ISBN, filteredBooks.get(pos).getIsbn());
                            startActivityForResult(i, 0);
                        }
                    });
                }
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.GONE);
                homeButton.setVisibility(View.GONE);

                setViewMode(BorrowList.ViewMode.ALL, books, filteredBooks, uuid);
                bookList.setOnItemClickListener(null);
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyBookActivity.class));
            }
        });

        final Button filterButton = findViewById(R.id.filter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterDialog().show(getSupportFragmentManager(), "Filter Books");
            }
        });

        borrowedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewMode(BorrowList.ViewMode.BORROWED, books, filteredBooks, uuid);
            }
        });

        requestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewMode(BorrowList.ViewMode.REQUESTED, books, filteredBooks, uuid);
            }
        });
    }

    private void setViewMode(BorrowList.ViewMode viewMode, List<Book> allBooks,
                             ArrayList<Book> filteredBooks, String uuid)
    {
        filteredBooks.clear();

        if (viewMode == BorrowList.ViewMode.ALL) {
            filteredBooks.addAll(allBooks);
        } else {
            for (Book book : allBooks) {
                if (BorrowList.checkUser(book, uuid, viewMode)) {
                    filteredBooks.add(book);
                }
            }
        }
        allBooksAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (i.getBooleanExtra(ProgramTags.BOOK_CHANGED, false)) {
                final String isbn = i.getStringExtra(ProgramTags.PASSED_ISBN);
                final int book_pos = i.getIntExtra(ProgramTags.BOOK_POS, -1);

                db.getBook(isbn, new OnSuccessListener<Book>() {
                    @Override
                    public void onSuccess(Book book) {
                        if (book_pos != -1) {
                            filteredBooks.set(book_pos, book);
                            allBooksAdapter.notifyDataSetChanged();
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }
}