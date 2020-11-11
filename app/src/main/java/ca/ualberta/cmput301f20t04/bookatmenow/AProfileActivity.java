package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * View A Profile that's not the logged in user's.
 * TODO: pretty-up UI, add profile picture.
 */
public class AProfileActivity extends AppCompatActivity {

    private TextView aUsername;
    private TextView anEmail;
    private TextView aPhone;
    private TextView anAddress;

    DBHandler db;

    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_profile);

        db = new DBHandler();

        aUsername = findViewById(R.id.a_username);
        anEmail = findViewById(R.id.an_email);
        aPhone = findViewById(R.id.a_phone);
        anAddress = findViewById(R.id.an_address);

        uuid = getIntent().getStringExtra(ProgramTags.PASSED_UUID);

        db.getUser(uuid, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User user) {
                aUsername.setText(user.getUsername());
                anEmail.setText(user.getEmail());
                aPhone.setText(user.getPhone());
                anAddress.setText(user.getAddress());
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}