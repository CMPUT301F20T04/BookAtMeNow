/**
 * Scan in ISBN using phone camera
 */

package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanBook extends AppCompatActivity {
    private String bookISBN;
    private SurfaceView cameraView;
    private CameraSource cameraSource;
    private static final int PERMISSIONS_REQUEST_ACCESS_CAMERA = 1;
    private TextView isbnText;

     // If the USE BARCODE button is clicked.
     // If no ISBN has been scanned, tell user to scan one.  Otherwise put the scanned ISBN in an
     // intent object and finish the activity.
    public void setBookISBN(View view) {
        if (bookISBN == null){
            Toast toast = Toast.makeText(this, "Please scan an ISBN barcode.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            final Intent main = getIntent();
            String passedIsbn = main.getStringExtra("ISBN");

            if (passedIsbn != null) {
                // if book barcode matches scanned barcode
                // allow barcode to be used to check out the book
                if (passedIsbn.equals(bookISBN)) {
                    this.finish();
                } else {
                    Toast toast = Toast.makeText(this, "Please scan a matching ISBN barcode.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                // new book
                Intent returnData = new Intent();
                returnData.putExtra("isbn", bookISBN);
                setResult(Activity.RESULT_OK, returnData);
                this.finish();
            }
        }
    }

    //If the CANCEL button is clicked, finish the activity.
    public void cancel(View view) {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_book);
        cameraView = findViewById(R.id.camera_view);
        isbnText = findViewById(R.id.isbn_number);
        initialize();
    }


    private void initialize(){

        //Build barcode detector object.
        BarcodeDetector detector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        //Build camera source object.
        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        //Get camera permissions and add callback to the SurfaceHolder of the camera preview.
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanBook.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanBook.this, new
                                String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_CAMERA);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //Set the processor to scan barcodes as they appear.
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                //If a barcode exists and is 13 digits long (length of ISBN-13), set TextView and bookISBN string.
                if (barcodes.size() > 0 && (barcodes.valueAt(0).displayValue).length() == 13) {
                    isbnText.post(new Runnable() {
                        @Override
                        public void run() {
                            bookISBN = barcodes.valueAt(0).displayValue;
                            isbnText.setText(bookISBN);
                        }
                    });
                }
            }
        });
    }
}