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

        final User newUser = new User();

        addressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ProfileActivity.this, GeoLocation.class));
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
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String passwordConfirm = passwordConfirmEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                // optional
                final String phone = phoneEditText.getText().toString();
                final String address = addressEditText.getText().toString();

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
                    db.addUser(newUser);

                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
            }
        });
    }
}