package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class MyBookActivity extends AppCompatActivity {

    private String init_isbn;

    private Button takePic;
    private Button save_image;
    private Button to_scan_btn;
    private Button save_changes;
    private ImageView myImg;
    private EditText titleEditText;
    private EditText authorEditText;

    private int REQUEST_IMAGE_CAPTURE = 1;
    private Uri myUri;
    private String currentPhotoPath;
    private File photoFile;
    private Boolean pictureTaken;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    StorageReference storageReference;
    private DBHandler db;

    public void takePicture(View view){
        if(getCameraPermissions() == true){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                photoFile = null;
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
        currentPhotoPath = image.getAbsolutePath();
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {//if user took photo, set it in imageview

            myImg = (ImageView) findViewById(R.id.MBA_imageView_picDisplay);//need to redefine it before changing it
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            myImg.setImageBitmap(myBitmap);
            myImg.setRotation(90);
            pictureTaken = true;
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

    /*
        We should have a formula, I was thinking <activity_type_name> so as an example: <setNewUser_button_saveAndExit>
        */
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
                takePic = findViewById(R.id.MBA_button_takePic);
                myImg = (ImageView) findViewById(R.id.MBA_imageView_picDisplay);
                save_image = findViewById(R.id.MBA_button_savePic);
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
                        db.addBook(book, new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Intent main = new Intent();
                                main.putExtra(ProgramTags.BOOK_CHANGED, true);
                                main.putExtra(ProgramTags.PASSED_ISBN, init_isbn);

                                setResult(RESULT_OK, main);
                                finish();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
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
    }
}