/**
 * View A Book that's not owned by the logged in user
 * TODO: add viewing pickup location, optional book image, request / return / borrow
 */

package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ABookActivity extends AppCompatActivity {

    private TextView aTitle;
    private TextView anAuthor;
    private TextView anIsbn;
    private TextView aStatus;
    private Button anOwner;

    private String isbn;
    private String uuid;

    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_book);

        isbn = getIntent().getStringExtra("ISBN");

        aTitle = findViewById(R.id.a_title);
        anAuthor = findViewById(R.id.an_author);
        anIsbn = findViewById(R.id.an_isbn);
        aStatus = findViewById(R.id.a_status);
        anOwner = findViewById(R.id.an_owner);

        db = new DBHandler();

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {
                aTitle.setText(book.getTitle());
                anAuthor.setText(book.getAuthor());
                anIsbn.setText(book.getIsbn());
                aStatus.setText(book.getStatus());
                uuid = book.getOwner();
                db.getUser(book.getOwner(), new OnSuccessListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        anOwner.setText(user.getUsername());
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

        anOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ABookActivity.this, AProfileActivity.class);
                i.putExtra("uuid", uuid);
                startActivity(i);
            }
        });

    }
}