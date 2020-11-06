package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Login using username and password that exist in the database.
 * TODO: login using email
 */
public class LoginActivity extends AppCompatActivity {

    private EditText logInUser;
    private EditText logInPW;
    private Button loginBtn;
    private Button createAccBtn;

    /**
     * clears the fields of the login screen
     */
    private void clearField() {
        logInUser.setText("");
        logInPW.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        logInUser = findViewById(R.id.login_user);
        logInPW = findViewById(R.id.login_pw);

        final DBHandler db = new DBHandler();

        // Dialog for username/password error
        final AlertDialog.Builder invalidLoginDialog = new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage("Invalid Username or Password")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginBtn.setEnabled(true);
                        createAccBtn.setEnabled(true);
                    }
                });

        // Dialog for database error
        final AlertDialog.Builder databaseErrorDialog = new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage("Invalid Username or Password")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loginBtn.setEnabled(true);
                        createAccBtn.setEnabled(true);
                    }
                });

        // This needs to be reworked into one authentication method
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameOrEmail = logInUser.getText().toString();
                // if username exists
                loginBtn.setEnabled(false);
                createAccBtn.setEnabled(false);
                db.usernameExists(usernameOrEmail, new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                if (s != null) {
                                    final String uuid = s;
                                    db.getUser(uuid, new OnSuccessListener<User>() {
                                        @Override
                                        public void onSuccess(final User user) {
                                            db.checkPassword(uuid, logInPW.getText().toString(), new OnSuccessListener<Boolean>() {
                                                @Override
                                                public void onSuccess(Boolean aBoolean) {
                                                    if (aBoolean) {
                                                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                                        i.putExtra("uuid", uuid);
                                                        startActivity(i);
                                                        finish();
                                                    } else {
                                                        clearField();
                                                        invalidLoginDialog.show();
                                                    }
                                                }
                                            }, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // DB retrieval failed
                                                    databaseErrorDialog.show();
                                                }
                                            }); // end of checkPassword
                                        }
                                    }, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // DB retrieval failed
                                            databaseErrorDialog.show();
                                        }
                                    }); // end of getUser
                                } else { // end of not null
                                    clearField();
                                    invalidLoginDialog.show();
                                }
                            }
                        }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // username doesn't exist, assume account doesn't exist
                        databaseErrorDialog.show();
                    }
                }); // end of usernameExists
            }
        }); // end of setOnClickListener

        createAccBtn = findViewById(R.id.create_acc_btn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            }
        });
    }
}