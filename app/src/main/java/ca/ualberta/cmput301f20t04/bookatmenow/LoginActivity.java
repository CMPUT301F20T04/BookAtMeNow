package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
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

public class LoginActivity extends AppCompatActivity {

    private EditText logInUser;
    private EditText logInPW;
    private Button loginBtn;
    private Button createAccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText logInUser = findViewById(R.id.login_user);
        final EditText logInPW = findViewById(R.id.login_pw);

        final DBHandler db = new DBHandler();

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameOrEmail = logInUser.getText().toString();
                // if username exists
                db.usernameExists(usernameOrEmail, new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                final String uuid = s;
                                db.getUser(uuid, new OnSuccessListener<User>() {
                                    @Override
                                    public void onSuccess(final User user) {
                                        db.checkPassword(uuid, logInPW.getText().toString(), new OnSuccessListener<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                if (aBoolean) {
                                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                                                    i.putExtra("uuid", uuid);
                                                    startActivity(i);
                                                } else {
                                                    Log.v("error","password fail");
                                                }
                                            }
                                        }, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                    }
                                }, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });

        Button createAccBtn = findViewById(R.id.create_acc_btn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            }
        });
    }
}