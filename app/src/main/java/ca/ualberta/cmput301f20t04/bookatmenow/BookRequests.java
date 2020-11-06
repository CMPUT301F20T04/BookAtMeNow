package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class BookRequests extends AppCompatActivity {

    private ListView myBookReqsList;
    private Button backButton;

    private BookAdapter booksAdapter;
    private ArrayList<Book> myBookRequests;

    private DBHandler db;

    private String isbn;

    private Book myBook;


    public void back(View view){
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests);

        myBookReqsList = findViewById(R.id.myBookReqs_listView_BookRequests);
        backButton = findViewById(R.id.back_button_BookRequests);

        myBookRequests = new ArrayList<>();
        booksAdapter = new BookAdapter(BookRequests.this, myBookRequests);
        myBookReqsList.setAdapter(booksAdapter);

        db = new DBHandler();

        Intent intent = getIntent();
        isbn = intent.getStringExtra("ISBN");

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {//got bool with isbn. Now get users requesting book

                if(book.getRequests().size() > 0){//there are users requesting this book
                    db.bookRequests(book.getRequests(), new OnSuccessListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> users) {//got list of users who requested book

                            Log.i("AppInfo", "all users are: " + String.valueOf(users));

                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("AppInfo", "Failed to get book requesters");
                        }
                    });
                } else {//no one is requesting this book

                }

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("AppInfo", "Failed to get book with given isbn");
            }
        });

    }
}