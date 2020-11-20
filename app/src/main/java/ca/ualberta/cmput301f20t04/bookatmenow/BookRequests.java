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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;
import java.util.List;

public class BookRequests extends AppCompatActivity {

    private ListView requesterList;
    private TextView noRequests;
    private TextView bookRequestsTitle;
    private Context context;

    private RequestAdapter requestAdapter;
    private LinkedList<User> bookRequests;

    private DBHandler db;

    private String isbn;


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_requests);
        context = this;

        requesterList = findViewById(R.id.myBookReqs_listView_BookRequests);
        noRequests = findViewById(R.id.noRequested_TextView_BookRequests);
        bookRequestsTitle = findViewById(R.id.myBookReqs_BookTitle);

        Intent intent = getIntent();
        isbn = intent.getStringExtra(ProgramTags.PASSED_ISBN);

        bookRequests = new LinkedList<>();
        requestAdapter = new RequestAdapter(BookRequests.this, bookRequests);
        requesterList.setAdapter(requestAdapter);
        noRequests.setVisibility(View.INVISIBLE);
        bookRequestsTitle.setVisibility(View.INVISIBLE);

        db = new DBHandler();

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {//got bool with isbn. Now get users requesting book

                String requestsFor = "Requests for: ";
                SpannableString requestTitleString = new SpannableString(requestsFor + book.getTitle());
                requestTitleString.setSpan(new StyleSpan(Typeface.ITALIC), requestsFor.length() -1, requestsFor.length() + book.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bookRequestsTitle.setText(requestTitleString);

                if(book.noRequests()){//there are users requesting this book
                    requesterList.setVisibility(View.GONE);
                    noRequests.setVisibility(View.VISIBLE);
                } else {//no one is requesting this book
                    bookRequestsTitle.setVisibility(View.VISIBLE);
                    db.bookRequests(book.getRequests(), new OnSuccessListener<List<User>>() {
                        @Override
                        public void onSuccess(List<User> users) {//got list of users who requested book
                            Log.e("AppInfo", "all users are: " + String.valueOf(users));
                            bookRequests.addAll(users);
                            requestAdapter.notifyDataSetChanged();
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("AppInfo", "Failed to get book requesters");
                        }
                    });
                }

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("AppInfo", "Failed to get book with given isbn");
            }
        });

    }

    public void acceptRequest(int position) {
        Toast.makeText(context, "Accept button clicked for row position=" + position, Toast.LENGTH_SHORT).show();
    }

    public void removeRequest(int position) {
        final String currentUuid = bookRequests.get(position).getUserId();
        Toast.makeText(context, "Remove button clicked for row position=" + position, Toast.LENGTH_SHORT).show();
        bookRequests.remove(position);
        requestAdapter.notifyDataSetChanged();

        db.getBook(isbn, new OnSuccessListener<Book>() {
                @Override
                public void onSuccess(Book book) {
                    book.deleteRequest(currentUuid);

                    if(book.noRequests()){//there are users requesting this book
                        requesterList.setVisibility(View.GONE);
                        bookRequestsTitle.setVisibility(View.GONE);
                        noRequests.setVisibility(View.VISIBLE);
                    }

                    try {
                        db.addBook(book, new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Toast toast = Toast.makeText(context, "You have requested this book.", Toast.LENGTH_SHORT);
                                toast.show();
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