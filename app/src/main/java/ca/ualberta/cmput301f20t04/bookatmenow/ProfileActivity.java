package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.regex.Pattern;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText emailEditText;
    // optional ones:
    private EditText phoneEditText;
    private EditText addressEditText;

    // buttons
    private Button saveProfileButton;
    private Button logoutButton;
    private Button cancelButton;
    private Button addressButton;

    private User newUser;

    // https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    static final Pattern EMAIL_REGEX  = Pattern.compile(
            "^[\\w+&*-]+" +
                    "(?:\\.[\\w+&*-]+)*" +
                    "@(?:[\\p{Alnum}-]+\\.)+" +
                    "[a-zA-Z]{2,7}$"
    );

    /**
     *
     * @param email
     * @return true if email is valid, false if it is not
     */
    public static boolean validEmail(String email) {
        if (email == null)
            return false;
        return EMAIL_REGEX.matcher(email).matches();
    }

    static final Pattern PW_REGEX  = Pattern.compile(
            "^[\\w+&*-]+"
    );

    public static boolean validPassword(String password) {
        if (password == null)
            return false;
        return PW_REGEX.matcher(password).matches();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){//confirm button was pressed
            String lat = data.getStringExtra("lat");
            String lng = data.getStringExtra("lng");
            Log.i("AppInfo", "they are: " + lat + " " + lng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_edit_profile);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordConfirmEditText = findViewById(R.id.password_confirm);
        final EditText emailEditText = findViewById(R.id.email);
        // optional ones:
        final EditText phoneEditText = findViewById(R.id.phone);
        final EditText addressEditText = findViewById(R.id.address);

        // buttons
        Button saveProfileButton = findViewById(R.id.profile_save);
        Button logoutButton = findViewById(R.id.logout);
        Button cancelButton = findViewById(R.id.profile_cancel);
        Button addressButton = findViewById(R.id.address_button);

        final DBHandler db = new DBHandler();

        // need login to enable profile editing
        // get user info to populate edit texts

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, GeoLocation.class);
                intent.putExtra("purpose", "select");
                startActivityForResult(intent, 1);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmEditText.getText().toString();
                String email = emailEditText.getText().toString();
                // optional
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();

                // error messages
                TextView confirmPwTextView = findViewById(R.id.confirm_pw);
                TextView invalidEmailTextView = findViewById(R.id.invalidEmail);

                HashMap<String, String> data = new HashMap<>();

                if (!password.equals(passwordConfirm)) {
                    confirmPwTextView.setText("Passwords don't match.");
                } else if (!validPassword(password)) {
                    confirmPwTextView.setText("Invalid Password.");
                } else {
                    confirmPwTextView.setText("");
                }

                if (!validEmail(email)) {
                    invalidEmailTextView.setText("Invalid Email.");
                } else {
                    invalidEmailTextView.setText("");
                }

                if (username.length() > 0 && password.length() > 0
                        && password.equals(passwordConfirm)
                        && validEmail(email) && validPassword(password)
                        && phone.length() > 0 && address.length() > 0) {

                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.setPhone(phone);
                    newUser.setAddress(address);
                    User myUser = new User(username, password, phone, email, address);
                    // also doesn't work for newUser
                    db.addUser(myUser, new OnSuccessListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (aBoolean) {
                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                    } else {
                                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                                    }

                                }
                            }, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("switcherTest", e.toString());

                                }
                            });
                }
            }
        });
    }
}