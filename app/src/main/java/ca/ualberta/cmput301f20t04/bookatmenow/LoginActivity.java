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
    private TextView loginError;
    private Button loginBtn;
    private Button createAccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        final EditText logInUser = findViewById(R.id.login_user);
        final EditText logInPW = findViewById(R.id.login_pw);
        final TextView loginError = findViewById(R.id.login_error);

        final DBHandler db = new DBHandler();

        Button loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameOrEmail = logInUser.getText().toString();
                //loginError.setText(usernameOrEmail);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                // if email login exists
//                Log.v("usernameOrEamil",usernameOrEmail);
//                db.emailExists(usernameOrEmail, new OnSuccessListener<String>() {
//                            @Override
//                            public void onSuccess(String s) {
//                                final String uuid = s;
//                                Log.v("uuid",s);
//                                db.getUser(uuid, new OnSuccessListener<User>() {
//                                    @Override
//                                    public void onSuccess(User user) {
//                                        Log.v("uuid user",user.getUserId());
//                                        Log.v("password",logInPW.getText().toString());
//                                        db.checkPassword(user.getUserId(), logInPW.getText().toString(), new OnSuccessListener<Boolean>() {
//                                            @Override
//                                            public void onSuccess(Boolean aBoolean) {
//                                                if (aBoolean) {
//                                                    Log.v("no error","password match");
//                                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                                } else {
//                                                    Log.v("error","password fail");
//                                                }
//                                            }
//                                        }, new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                            }
//                                        });
//                                    }
//                                }, new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                    }
//                                });
//                            }
//                        }, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
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