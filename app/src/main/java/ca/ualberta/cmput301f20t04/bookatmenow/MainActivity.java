package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_edit_profile); // formerly activity_main

        // test DB initialisation
        FirebaseFirestore testDB = FirebaseFirestore.getInstance();

        final String TAG = "Sample";

        // can move this to occur after login screen later
        Button saveProfileButton = findViewById(R.id.profile_save);
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText emailEditText = findViewById(R.id.email);
        // optional ones:
        EditText phoneEditText = findViewById(R.id.phone);
        EditText addressEditText = findViewById(R.id.phone);

        final CollectionReference collectionReference = testDB.collection("Users");

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String email = emailEditText.getText().toString();

                HashMap<String, String> data = new HashMap<>();

                if (username.length() > 0 && password.length() > 0 && email.length() > 0) {
                    // need to check if password = confirm_password
                    // need to check if email contains @ and chars on either end
                    data.put("Password", password);
                    data.put("Email", email);
                    collectionReference
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
                }
            }
        });
//
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
}