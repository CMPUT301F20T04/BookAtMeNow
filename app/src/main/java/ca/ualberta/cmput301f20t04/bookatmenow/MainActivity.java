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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHandler db = new DBHandler();

        bookList = findViewById(R.id.book_list);
        final ArrayList<Book> filteredBooks = new ArrayList<>();
        allBooksAdapter = new BorrowList(MainActivity.this, filteredBooks);
        bookList.setAdapter(allBooksAdapter);

        db.getAllBooks(new OnSuccessListener<List<Book>>() {
                    @Override
                    public void onSuccess(List<Book> books) {
                       filteredBooks.addAll(books);
                       allBooksAdapter.notifyDataSetChanged();
                       Log.d(ProgramTags.DB_ALL_FOUND, "All books in database successfully found");
                       setUi();
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(ProgramTags.DB_ERROR, "Not all books could be found!" + e.toString());
                    }
                });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // A book
            }
        });
    }

    private void setUi() {
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

        // temporary for access to the MyBook activity
        final Button myBookButton = findViewById(R.id.temp_book);
        myBookButton.setVisibility(View.GONE);

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.VISIBLE);
                homeButton.setVisibility(View.VISIBLE);
                myBookButton.setVisibility(View.VISIBLE);

                if (uuid != null) {
                    // filter all books but only for one uuid
                }
            }
        });

        myBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyBookActivity.class);
//              i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.GONE);
                homeButton.setVisibility(View.GONE);
                myBookButton.setVisibility(View.GONE);
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
    }
}