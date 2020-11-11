package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import static ca.ualberta.cmput301f20t04.bookatmenow.R.id.editTextAuthor;

public class MyBookActivity extends AppCompatActivity {

    private Button scanButton;
    private Button cancelButton;
    private Button saveChangesButton;
    private Button removeButton;
    private Button pendingRequestButton;
    private Button takeImageButton;
    private RadioGroup statusButtons;
    private RadioButton selectedStatusButton;
    private EditText titleEditText;
    private EditText authorEditText;
    private EditText isbnEditText;
    private TextView currentBorrower;

    private ImageView bookImage;

    private String initIsbn;

    public static final int CHANGE_BOOK_FROM_MAIN = 2;
    public static final int CHANGE_BOOK_FROM_MYBOOKS = 3;
    public static final int ADD_BOOK = 1;

    private DBHandler db;

    final private static int REQUEST_IMAGE_CAPTURE = 1;
    final private static int REQUEST_ISBN_SCAN = 0;


    private Uri myUri;
    private Boolean pictureTaken;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;

    StorageReference storageReference;
/**
    private EditText title;
    private EditText author;
    private EditText isbn;
    private Button deleteButton;
    private Button submitButton;
    private int REQUEST_SCAN_ISBN = 2;
    private String stringIsbn;
    private Boolean isbnTaken;
**/

    private View.OnClickListener cancelButtonAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setResult(RESULT_CANCELED);
            Log.d(ProgramTags.GENERAL_SUCCESS, "Cancelled book edit activity.");
            finish();
        }
    };

    public void takePicture(View view){
        if(getCameraPermissions() == true){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    myUri = FileProvider.getUriForFile(this,
                            "ca.ualberta.cmput301f20t04.bookatmenow",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void saveImg(View view) {

        if(pictureTaken == false){
            Toast toast = Toast.makeText(this, "Take a picture first", Toast.LENGTH_SHORT);
            toast.show();

        } else {
            Toast toast1 = Toast.makeText(this, "Uploading image", Toast.LENGTH_SHORT);
            toast1.show();

            Uri file = myUri;
            StorageReference riversRef = storageReference.child("images/myImage.jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            Log.i("AppInfo", "Uri is; " + downloadUrl.toString());
                            Toast toast = Toast.makeText(MyBookActivity.this, "Upload successful", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast toast = Toast.makeText(MyBookActivity.this, "Upload failed", Toast.LENGTH_SHORT);
                            toast.show();
                            Log.i("AppInfo", "FAIL: " + exception.toString());
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // feature not included in current version
//                case REQUEST_IMAGE_CAPTURE: // if user took photo, set it in imageview
//                    myImg = (ImageView) findViewById(R.id.MBA_imageView_picDisplay); //need to redefine it before changing it
//                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                    myImg.setImageBitmap(myBitmap);
//                    myImg.setRotation(90);
//                    pictureTaken = true;
//                    break;

                case REQUEST_ISBN_SCAN:
                    String newIsbn = data.getStringExtra("isbn");

                    if (!newIsbn.equals(initIsbn)) {
                        isbnEditText.setText(newIsbn);
                    }
                    break;
            }
        }
    }

    private boolean getCameraPermissions() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_ACCESS_CAMERA);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);//-1 means deny. 0 means allow
        if(grantResults[0] == -1){//permissions were denied
            Toast toast = Toast.makeText(this, "Camera permissions are needed to use the camera", Toast.LENGTH_LONG);
            toast.show();
        } else if(grantResults[0] == 0){//permissions were granted
            takePicture(null);
        } else{
            new Exception("grantResult returned incorrect value");
        }
    }

    private boolean checkFields() {
        return (!(titleEditText.getText().length() < 1) &&
                !(authorEditText.getText().length() < 1) &&
                !(isbnEditText.getText().length() < 13 && isbnEditText.getText().length() > 13) &&
                !(statusButtons.getCheckedRadioButtonId() == -1));
    }

    private void toggleAllFields(int mode) {
        if (saveChangesButton.isEnabled()) {
            if (mode == 0) {
                scanButton.setEnabled(false);
                saveChangesButton.setEnabled(false);
                cancelButton.setEnabled(false);
                statusButtons.setEnabled(false);
                takeImageButton.setEnabled(false);
                titleEditText.setEnabled(false);
                authorEditText.setEnabled(false);
                isbnEditText.setEnabled(false);
            } else if (mode == 1) {
                saveChangesButton.setEnabled(false);
                cancelButton.setEnabled(false);
                statusButtons.setEnabled(false);
                removeButton.setEnabled(false);
                pendingRequestButton.setEnabled(false);
                takeImageButton.setEnabled(false);
                titleEditText.setEnabled(false);
                authorEditText.setEnabled(false);
            }
        } else if (!saveChangesButton.isEnabled()) {
            if (mode == 0) {
                scanButton.setEnabled(true);
                saveChangesButton.setEnabled(true);
                cancelButton.setEnabled(true);
                statusButtons.setEnabled(true);
                takeImageButton.setEnabled(true);
                titleEditText.setEnabled(true);
                authorEditText.setEnabled(true);
                isbnEditText.setEnabled(true);
            } else if (mode == 1) {
                saveChangesButton.setEnabled(true);
                cancelButton.setEnabled(true);
                statusButtons.setEnabled(true);
                removeButton.setEnabled(true);
                pendingRequestButton.setEnabled(true);
                takeImageButton.setEnabled(true);
                titleEditText.setEnabled(true);
                authorEditText.setEnabled(true);
            }
        }
    }

    /*
        We should have a formula, I was thinking <activity_type_name> so as an example: <setNewUser_button_saveAndExit>
        */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);

        final Intent main = getIntent();
        db = new DBHandler();

        scanButton = findViewById(R.id.myBook_scan_button);
        saveChangesButton = findViewById(R.id.myBook_save_change_button);
        cancelButton = findViewById(R.id.myBook_cancel_button);
        statusButtons = findViewById(R.id.myBook_status_radiogroup);
        removeButton = findViewById(R.id.myBook_remove_button);
        pendingRequestButton = findViewById(R.id.myBook_pending_request_button);
        takeImageButton = findViewById(R.id.myBook_take_picture_button);

        titleEditText = findViewById(R.id.myBook_title_edittext);
        authorEditText = findViewById(R.id.myBook_author_edittext);
        isbnEditText = findViewById(R.id.myBook_isbn_edittext);
        currentBorrower = findViewById(R.id.myBook_current_borrower_textview);

        bookImage = findViewById(R.id.myBook_imageview);

        cancelButton.setOnClickListener(cancelButtonAction);

        if (main.hasExtra(ProgramTags.PASSED_ISBN)) {
            initIsbn = main.getStringExtra(ProgramTags.PASSED_ISBN);
            isbnEditText.setVisibility(View.INVISIBLE);
            scanButton.setVisibility(View.INVISIBLE);

            //takePic = findViewById(R.id.MBA_button_takePic);
            //myImg = (ImageView) findViewById(R.id.MBA_imageView_picDisplay);
            //save = findViewById(R.id.MBA_button_savePic);

            db.getBook(initIsbn, new OnSuccessListener<Book>() {
                @Override
                public void onSuccess(final Book book) {
                    titleEditText.setText(book.getTitle());
                    authorEditText.setText(book.getAuthor());
                    isbnEditText.setText(book.getIsbn());

                    String status = book.getStatus();
                    switch (status) {
                        case FireStoreMapping.BOOK_STATUS_AVAILABLE:
                            statusButtons.check(R.id.myBook_available_radiobutton);
                            break;
                        case FireStoreMapping.BOOK_STATUS_ACCEPTED:
                            statusButtons.check(R.id.myBook_accepted_radiobutton);
                            break;
                        case FireStoreMapping.BOOK_STATUS_BORROWED:
                            statusButtons.check(R.id.myBook_borrowed_radiobutton);
                            break;
                        case FireStoreMapping.BOOK_STATUS_UNAVAILABLE:
                            statusButtons.check(R.id.myBook_unavailable_radiobutton);
                            break;
                        default:
                    }

                    removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog
                                    .Builder(MyBookActivity.this)
                                    .setTitle("Remove Book")
                                    .setMessage("Are you sure you want to permanently delete this book?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            db.removeBook(initIsbn,
                                                    new OnSuccessListener<Boolean>() {
                                                        @Override
                                                        public void onSuccess(Boolean aBoolean) {
                                                            setResult(RESULT_OK, main);
                                                            finish();
                                                        }
                                                    },
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    });
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        }
                    });

                    statusButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            selectedStatusButton = radioGroup.findViewById(i);
                            if (selectedStatusButton.isChecked()) {
                                String status = selectedStatusButton.getText().toString();
                                Log.d(ProgramTags.BOOK_DATA, String.format("Book status set to %s", book.getStatus()));
                                if (status.equals("Accepted")) {
                                    Intent intent = new Intent(MyBookActivity.this, BookRequests.class);
                                    intent.putExtra("ISBN", initIsbn);
                                    startActivity(intent);
                                } else if (status.equals("Borrowed")) {
                                    Intent intent = new Intent(MyBookActivity.this, ScanBook.class);
                                    intent.putExtra("ISBN", initIsbn);
                                    startActivity(intent);
                                } else {
                                    book.setStatus(status);
                                }
                            }
                        }
                    });

                    saveChangesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleAllFields(1);

                            if (checkFields()) {
                                book.setTitle(titleEditText.getText().toString().trim());
                                book.setAuthor(authorEditText.getText().toString().trim());
                                try {
                                    db.addBook(book, new OnSuccessListener<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            // send data back to main

                                            setResult(RESULT_OK, main);
                                            finish();
                                        }
                                    }, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(ProgramTags.DB_ERROR, "Book could not be added to database!");
                                            setResult(RESULT_CANCELED, main);
                                            finish();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                new AlertDialog.Builder(MyBookActivity.this)
                                        .setTitle("Error!")
                                        .setMessage("Please fill in all the required fields!")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                toggleAllFields(1);
                                            }
                                        }).show();
                            }
                        }
                    });

                    pendingRequestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MyBookActivity.this, BookRequests.class);
                            i.putExtra("ISBN", initIsbn);
                            startActivity(i);
                        }
                    });

                    titleEditText.setText(book.getTitle());

                    authorEditText.setText(book.getAuthor());

                    storageReference = FirebaseStorage.getInstance().getReference();

                    pictureTaken = false;
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(ProgramTags.DB_ERROR, "Book could not be found!" + e.toString());
                }
            });
        } else {
            final String uuid = main.getStringExtra(ProgramTags.PASSED_UUID);
            final Book newBook = new Book();

            pendingRequestButton.setVisibility(View.INVISIBLE);
            removeButton.setVisibility(View.INVISIBLE);
            currentBorrower.setVisibility(View.INVISIBLE);

            statusButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    selectedStatusButton = radioGroup.findViewById(i);
                    if (selectedStatusButton.isChecked()) {
                        String status = selectedStatusButton.getText().toString();
                        newBook.setStatus(status);
                        Log.d(ProgramTags.BOOK_DATA, String.format("Book status set to %s", newBook.getStatus()));
                    }

                }
            });

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MyBookActivity.this, ScanBook.class);
                    startActivityForResult(i, REQUEST_ISBN_SCAN);
                }
            });

            saveChangesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleAllFields(0);
                    if (checkFields()) {

                        newBook.setTitle(titleEditText.getText().toString().trim());
                        Log.d(ProgramTags.BOOK_DATA, String.format("Book title set to %s", newBook.getTitle()));

                        newBook.setAuthor(authorEditText.getText().toString().trim());
                        Log.d(ProgramTags.BOOK_DATA, String.format("Book author set to %s", newBook.getAuthor()));

                        newBook.setIsbn(isbnEditText.getText().toString().trim());
                        Log.d(ProgramTags.BOOK_DATA, String.format("Book isbn set to %s", newBook.getIsbn()));

                        newBook.setOwner(uuid);
                        Log.d(ProgramTags.BOOK_DATA, String.format("Book owner set to %s", newBook.getOwner()));


                        try {
                            db.addBook(newBook,
                                    new OnSuccessListener<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            Toast.makeText(getApplicationContext(), "Book added.", Toast.LENGTH_LONG).show();
                                            setResult(RESULT_OK, main);
                                            finish();
                                        }
                                    },
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed to save data, please try again.", Toast.LENGTH_LONG).show();
                                            Log.d(ProgramTags.DB_ERROR, "Failed to add new book from MyBookActivity.");
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        new AlertDialog.Builder(MyBookActivity.this)
                                .setTitle("Error!")
                                .setMessage("Please fill in all the required fields!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        toggleAllFields(0);
                                    }
                                }).show();
                    }
                }
            });

        }
    }
}
/**
 old oncreate
 @Override
 protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_my_book);

 Intent main = getIntent();

 init_isbn = main.getStringExtra(ProgramTags.PASSED_ISBN);
 db = new DBHandler();

 db.getBook(init_isbn, new OnSuccessListener<Book>() {
 @Override
 public void onSuccess(final Book book) {
 //takePic = findViewById(R.id.MBA_button_takePic);
 //myImg = (ImageView) findViewById(R.id.MBA_imageView_picDisplay);
 //save = findViewById(R.id.MBA_button_savePic);
 to_scan_btn = findViewById(R.id.to_scan_btn);
 to_scan_btn.setOnClickListener(new View.OnClickListener() {
 @Override
 public void onClick(View v) {
 startActivity(new Intent(MyBookActivity.this, ScanBook.class));
 }
 });

 save_changes = findViewById(R.id.save_change_button);
 save_changes.setOnClickListener(new View.OnClickListener() {
 @Override
 public void onClick(View v) {
 book.setTitle("Title");

 final Intent main = new Intent();
 db.addBook(book, new OnSuccessListener<Boolean>() {
 @Override
 public void onSuccess(Boolean aBoolean) {
 // send data back to main

 setResult(RESULT_OK, main);
 finish();
 }
 }, new OnFailureListener() {
 @Override
 public void onFailure(@NonNull Exception e) {
 Log.d(ProgramTags.DB_ERROR, "Book could not be added to database!");
 setResult(RESULT_CANCELED, main);
 finish();
 }
 });
 }
 });

 titleEditText = findViewById(R.id.editTextTitle);
 titleEditText.setText(book.getTitle());

 authorEditText = findViewById(R.id.editTextAuthor);
 authorEditText.setText(book.getAuthor());

 storageReference = FirebaseStorage.getInstance().getReference();

 pictureTaken = false;
 }
 }, new OnFailureListener() {
 @Override
 public void onFailure(@NonNull Exception e) {
 Log.d(ProgramTags.DB_ERROR, "Book could not be found!" + e.toString());
 }
 });
 title = findViewById(R.id.editTextTitle);
 author = findViewById(R.id.editTextAuthor);
 deleteButton = findViewById(R.id.delete_button);
 submitButton = findViewById(R.id.submit_button);
 submitButton.setOnClickListener(new View.OnClickListener() {
 @Override
 public void onClick(View v) {
 if (isbnTaken) {
 String bookTitle = title.getText().toString();
 String bookAuthor = author.getText().toString();
 String bookIsbn = isbn.getText().toString();
 String owner = "";
 Book newBook = new Book(bookTitle, bookAuthor, bookIsbn, owner);
 //final DBHandler db = new DBHandler();
 //db.addBook();
 }
 }
 });
 **/