package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // should login first, then come back

        // menu buttons
        Button editProfileButton = findViewById(R.id.edit_profile);
        Button myBooksButton = findViewById(R.id.my_books);
        Button borrowedButton = findViewById(R.id.borrowed);
        Button requestedButton = findViewById(R.id.requested);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        final Button addBookButton = findViewById(R.id.add);
        addBookButton.setVisibility(View.GONE);
        final Button homeButton = findViewById(R.id.home);
        homeButton.setVisibility(View.GONE);

        myBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.VISIBLE);
                homeButton.setVisibility(View.VISIBLE);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBookButton.setVisibility(View.GONE);
                homeButton.setVisibility(View.GONE);
            }
        });

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, AddBookActivity.class));
            }
        });

        final Button filterButton = findViewById(R.id.filter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FilterDialog().show(getSupportFragmentManager(), "Filter Books");
            }
        });

    }
}