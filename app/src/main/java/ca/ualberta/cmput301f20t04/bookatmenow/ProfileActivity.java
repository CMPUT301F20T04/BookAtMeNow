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

    // https://www.geeksforgeeks.org/check-email-address-valid-not-java/
    static final Pattern EMAIL_REGEX  = Pattern.compile(
            "^[\\w+&*-]+" +
                    "(?:\\.[\\w+&*-]+)*" +
                    "@(?:[\\p{Alnum}-]+\\.)+" +
                    "[a-zA-Z]{2,7}$"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_edit_profile);

        // test DB initialisation
        FirebaseFirestore testDB = FirebaseFirestore.getInstance();

        final String TAG = "Sample";


        // can move this to occur after login screen later
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText passwordConfirmEditText = findViewById(R.id.password_confirm);
        final EditText emailEditText = findViewById(R.id.email);
        // optional ones:
        final EditText phoneEditText = findViewById(R.id.phone);
        final EditText addressEditText = findViewById(R.id.phone);

        final CollectionReference userRef = testDB.collection("Users");

        // buttons
        Button saveProfileButton = findViewById(R.id.profile_save);
        Button logoutButton = findViewById(R.id.logout);
        Button cancelButton = findViewById(R.id.profile_cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        // temporary
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
                passwordEditText.setText("");
                emailEditText.setText("");
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
                        && validEmail(email)) {

                    data.put("Password", password);
                    data.put("Email", email);

                    if (phone.length() > 0) {
                        data.put("Phone", phone);
                    }
                    if (address.length() > 0) {
                        data.put("Address", address);
                    }
                    userRef
                            .document(username)
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Data has been added successfully!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Data could not be added!" + e.toString());
                                }
                            });
                    usernameEditText.setText(username);
                    passwordEditText.setText(password);
                    emailEditText.setText(email);

                    if (phone.length() > 0) {
                        phoneEditText.setText(phone);
                    }
                    if (address.length() > 0) {
                        addressEditText.setText(address);
                    }

                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
            }
        });

        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(QueryDocumentSnapshot doc: value) {
                    Log.d(TAG, String.valueOf(doc.getData().get("Username")));
                    String usernameDB = doc.getId();
                    String passwordDB = (String) doc.getData().get("Password");
                    String emailDB = (String) doc.getData().get("Email");
                    usernameEditText.setText(usernameDB);
                    passwordEditText.setText(passwordDB);
                    emailEditText.setText(emailDB);
                }
            }
        });
    }

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
}