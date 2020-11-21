package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BookRequests extends AppCompatActivity {

    private ListView requesterList;
    private TextView noRequests;
    private TextView bookRequestsTitle;
    private Context context;
    private boolean gotIsbn;
    private boolean gotLocation;

    private RequestAdapter requestAdapter;
    private LinkedList<User> bookRequests;

    private DBHandler db;

    private String isbn;

    final private static int REQUEST_ISBN_SCAN = 0;
    final private static int REQUEST_LOCATION = 1;

    Geocoder geocoder;
    List<Address> addresses;
    List<String> location;
    int acceptPosition;


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
                requestTitleString.setSpan(new StyleSpan(Typeface.ITALIC), requestsFor.length() - 1, requestsFor.length() + book.getTitle().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                bookRequestsTitle.setText(requestTitleString);

                if (book.noRequests()) {//there are users requesting this book
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

    public void clickedAccept(int position) {
        acceptPosition = position;
        checkIsbn();
        getLocation();
    }

    public void removeRequest(int position) {
        final String requestUuid = bookRequests.get(position).getUserId();
        final String requestUsername = bookRequests.get(position).getUsername();
        Toast.makeText(context, "Removed request by " + requestUsername, Toast.LENGTH_SHORT).show();
        bookRequests.remove(position);
        requestAdapter.notifyDataSetChanged();

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {
                book.deleteRequest(requestUuid);

                if (book.noRequests()) {
                    requesterList.setVisibility(View.GONE);
                    bookRequestsTitle.setVisibility(View.GONE);
                    noRequests.setVisibility(View.VISIBLE);
                    book.setStatus(ProgramTags.STATUS_AVAILABLE);
                }

                try {
                    db.addBook(book, new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
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

    public void acceptRequest(int position) {
        final String borrowerUuid = bookRequests.get(position).getUserId();
        final String borrowerUsername = bookRequests.get(position).getUsername();
        final List<String> borrower = Arrays.asList(borrowerUuid, borrowerUsername);
        bookRequests.clear();
        requestAdapter.notifyDataSetChanged();
        requesterList.setVisibility(View.GONE);
        bookRequestsTitle.setVisibility(View.GONE);
        noRequests.setVisibility(View.VISIBLE);

        db.getBook(isbn, new OnSuccessListener<Book>() {
            @Override
            public void onSuccess(Book book) {

                book.setBorrower(borrower);
                book.setStatus(ProgramTags.STATUS_ACCEPTED);
                book.clearRequests();

                try {
                    db.addBook(book, new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Log.e(ProgramTags.DB_ERROR, "Requested book could be re-added to database!");
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(ProgramTags.DB_ERROR, "Requested book could not be re-added to database!");
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


    public void checkIsbn() {
        Intent i = new Intent(BookRequests.this, ScanBook.class);
        i.putExtra(ProgramTags.PASSED_ISBN, isbn);
        startActivityForResult(i, REQUEST_ISBN_SCAN);
    }

    public void getLocation() {
        Intent i = new Intent(BookRequests.this, GeoLocation.class);
        i.putExtra(ProgramTags.LOCATION_PURPOSE, "getLocation");
        startActivityForResult(i, REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOCATION:
                    String lat = data.getStringExtra("lat");
                    String lng = data.getStringExtra("lng");
                    location = Arrays.asList(lat, lng);

                    break;

                case REQUEST_ISBN_SCAN:
                    acceptRequest(acceptPosition);
                    break;
            }
        }
    }
}