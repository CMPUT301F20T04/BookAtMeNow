package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View A Book that's not owned by the logged in user.
 * TODO: add viewing pickup location, optional book image, request / return / borrow, pretty-up UI.
 */
public class ABookActivity extends AppCompatActivity {

    private TextView aTitle;
    private TextView anAuthor;
    private TextView anIsbn;
    private TextView aStatus;
    private Button ownerButton;
    private Button requestButton;
    private Context context;

    private String isbn;
    private String owner_uuid;
    private String uuid;

    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_book);
        context = this;

        isbn = getIntent().getStringExtra(ProgramTags.PASSED_ISBN);
        uuid = getIntent().getStringExtra(ProgramTags.PASSED_UUID);

        aTitle = findViewById(R.id.abook_title_textview);
        anAuthor = findViewById(R.id.abook_author_textview);
        anIsbn = findViewById(R.id.abook_isbn_textview);
        aStatus = findViewById(R.id.abook_status_textview);
        ownerButton = findViewById(R.id.abook_owner_button);
        requestButton = findViewById(R.id.abook_request_button);
        requestButton.setEnabled(false);

        db = new DBHandler();

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {
                owner_uuid = book.getOwner();

                String title = "Title: ";
                SpannableString titleString = new SpannableString(title + book.getTitle());
                titleString.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                aTitle.setText(titleString);

                String author = "Author: ";
                SpannableString authorString = new SpannableString(author + book.getAuthor());
                authorString.setSpan(new StyleSpan(Typeface.BOLD), 0, author.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                anAuthor.setText(authorString);

                String isbn = "ISBN: ";
                SpannableString isbnString = new SpannableString(isbn + book.getIsbn());
                isbnString.setSpan(new StyleSpan(Typeface.BOLD), 0, isbn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                anIsbn.setText(isbnString);

                String status = "Status: ";
                String bookStatus = book.getStatus();
                SpannableString statusString = new SpannableString(status + bookStatus);
                statusString.setSpan(new StyleSpan(Typeface.BOLD), 0, status.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                aStatus.setText(statusString);

                //If user hasn't requested the book yet and the book is available or requested,
                //enable the request button.
                if(!book.checkForRequest(uuid) &&
                        (bookStatus.equals(ProgramTags.STATUS_AVAILABLE) ||
                        bookStatus.equals(ProgramTags.STATUS_REQUESTED))) requestButton.setEnabled(true);



                db.getUser(owner_uuid, new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        ownerButton.setText(user.getUsername());

                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        ownerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ABookActivity.this, AProfileActivity.class);
                i.putExtra(ProgramTags.PASSED_UUID, owner_uuid);
                startActivity(i);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRequest();
            }
        });

    }

    /**
     * If user requests a book, get the book from the db, add the request to the books requested list,
     * then re-add the book to the db.
     */
    private void handleRequest() {
        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {
                book.addRequest(uuid);
                try {
                    db.addBook(book, new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Toast toast = Toast.makeText(context, "You have requested this book.", Toast.LENGTH_SHORT);
                            toast.show();
                            requestButton.setEnabled(false);
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(ProgramTags.DB_ERROR, "Requested book could not be re-added to database!");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}