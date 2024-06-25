package com.example.notespro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginTxtEmail, loginTxtPass, loginTxtName;
    private TextView NewUser;
    private Button btnLogin;
    FirebaseDatabase firebaseDatabase;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String DataBaseHead = "UserData";
    DatabaseReference databaseReference;
    private ProgressBar progressBar;
    String Password, Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        loginTxtEmail = findViewById(R.id.editTxtEmail);
        loginTxtPass = findViewById(R.id.editTxtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        NewUser = findViewById(R.id.NewUser);
        loginTxtName = findViewById(R.id.editTxtName);
        progressBar = findViewById(R.id.progreeBarAtLogin);
        sharedPreferences=getSharedPreferences("Storage",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        // if NewUser Redirecting to SignUp Page
        NewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SingUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginTxtEmail.getText().toString();
                String password = loginTxtPass.getText().toString();

                if (!validateInput(email, password)) {
                    return;
                }

                changeInProgressbar(true);
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference(DataBaseHead);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean []userFound={false};
                        if(snapshot.exists()){
                            int totalChildren=(int)snapshot.getChildrenCount();
                            int childrenCount[]={0};
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String uid = userSnapshot.getKey();
                                DatabaseReference userRef = firebaseDatabase.getReference(DataBaseHead).child(uid).child(password);
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String emailFromDB = dataSnapshot.child("email").getValue(String.class);
                                        String passFromDB = dataSnapshot.child("password").getValue(String.class);
                                        String nameFromDB=dataSnapshot.child("name").getValue(String.class);
                                        if (Objects.equals(emailFromDB, email) && Objects.equals(passFromDB, password)) {
                                            userFound[0]=true;
                                            editor.putBoolean("isLogged",true);
                                            editor.putString("Email",email);
                                            editor.putString("Password",password);
                                            editor.putString("Name",nameFromDB);
                                            editor.apply();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        childrenCount[0]++;
                                        if(childrenCount[0]==totalChildren && !userFound[0]) {
                                            ShowAlertDialogPlease("Wrong Email/Password..!");
                                            changeInProgressbar(false);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle error
                                        childrenCount[0]++;
                                        if (childrenCount[0] == totalChildren && !userFound[0]) {
                                            changeInProgressbar(false);
                                            Toast.makeText(LoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                        else{
                            ShowAlertDialogPlease("User Does Not Exist..!");
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        changeInProgressbar(false);
                        Toast.makeText(LoginActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            loginTxtEmail.setError("Email cannot be empty");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginTxtEmail.setError("Invalid Email");
            return false;
        }
        if (password.isEmpty()) {
            loginTxtPass.setError("Password cannot be empty");
            return false;
        }
        return true;
    }

    private void changeInProgressbar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    public void ShowAlertDialogPlease(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
