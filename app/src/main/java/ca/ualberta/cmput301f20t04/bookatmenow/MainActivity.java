package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // can move this to occur after login screen later
        Button saveProfileButton = findViewById(R.id.profile_save);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        EditText email = findViewById(R.id.email);
        // optional ones:
        EditText phone = findViewById(R.id.phone);
        EditText address = findViewById(R.id.phone);
    }
}