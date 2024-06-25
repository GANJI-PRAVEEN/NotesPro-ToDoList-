package com.example.notespro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingUpActivity extends AppCompatActivity {
    private TextView txtName, txtGender, txtEmail, txtPassword;
    private Button btnSignUp;
    private TextView oldUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    String DataBaseHead = "UserData";
    String password, name;
    String email;
    String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // Initializing TextViews
        txtName = findViewById(R.id.editTxtName);
        txtEmail = findViewById(R.id.editTxtEmail);
        txtPassword = findViewById(R.id.editTxtPassword);
        btnSignUp = findViewById(R.id.btnSingUp);
        oldUser = findViewById(R.id.Olduser);
        progressBar = findViewById(R.id.progreeBarAtSingUp);

        // Firebase Database Connection Initialization
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(DataBaseHead);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences=getSharedPreferences("Storage",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        if(sharedPreferences.getBoolean("isLogged",false)){
            startActivity(new Intent(SingUpActivity.this,MainActivity.class));
            finish();
            return;
        }

        // Olduser Redirecting to LoginPage
        oldUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        addTextWatchers();
        // Spinner for Gender
        Spinner spinnerGender = findViewById(R.id.spinnerGender);

        String[] genders = {"Male", "Female", "Other"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
        spinnerGender.setSelection(0);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = genders[position];
                // Handle the selected gender
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
        // Close Spinner
    }

    private void addTextWatchers() {
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validateEmail(s.toString())) {
                    txtEmail.setError("Invalid Email");
                } else {
                    txtEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!validatePassword(s.toString())) {
                    txtPassword.setError("Password should be at least 5 characters long");
                } else {
                    txtPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }

    private void handleSignUp() {
        changeInProgreeBar(true);

        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        name = txtName.getText().toString();

        // Validate fields before proceeding
        if (!validateEmail(email)) {
            txtEmail.setError("Invalid Email");
            changeInProgreeBar(false);
            return;
        }

        if (!validatePassword(password)) {
            txtPassword.setError("Password should be at least 5 characters long");
            changeInProgreeBar(false);
            return;
        }

        // Email Verification Purpose Optional Field
        // firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
        //     @Override
        //     public void onComplete(@NonNull Task<AuthResult> task) {
        //         changeInProgreeBar(false);
        //         if (task.isSuccessful()) {
        //             Toast.makeText(SingUpActivity.this, "Account Has Created Verify Email", Toast.LENGTH_SHORT).show();
        //             firebaseAuth.getCurrentUser().sendEmailVerification();
        //             firebaseAuth.signOut();
        //             finish();
        //         } else {
        //             Toast.makeText(SingUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        //         }
        //     }
        // });

        // Important Field Email Checking Duplicate redundancy Check
        changeInProgreeBar(true);
        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference1 = firebaseDatabase1.getReference(DataBaseHead).child("IkxTtjXxJdXK0bPjdq3FI5FjqEM2");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isEmailExist = false;
                boolean isPasswordExist = false;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String existingEmail = userSnapshot.child("email").getValue(String.class);
                    String existingPassword = userSnapshot.child("password").getValue(String.class);

                    if (existingEmail != null && existingEmail.equals(email)) {
                        isEmailExist = true;
                        break;
                    }
                    else if (existingPassword != null && existingPassword.equals(password)) {
                        isPasswordExist = true;
                        break;
                    }
                }

                changeInProgreeBar(false);
                Toast.makeText(SingUpActivity.this, "EmailExist: " + isEmailExist, Toast.LENGTH_SHORT).show();
                Toast.makeText(SingUpActivity.this, "PasswordExist: " + isPasswordExist, Toast.LENGTH_SHORT).show();

                if(isEmailExist && isPasswordExist){
                    ShowAlertDialogPlease("Please Change Email and Password...!");
                    return;
                }
                else if (isPasswordExist) {
                    ShowAlertDialogPlease("Warning : Please Change the Password For Security Purpose!");
                    return;
                } else if (isEmailExist) {
                    ShowAlertDialogPlease("Email Already Exists!");
                    return;
                } else {
                    registerUser("IkxTtjXxJdXK0bPjdq3FI5FjqEM2");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                changeInProgreeBar(false);
                ShowAlertDialogPlease("Failed to access database. Please try again later.");
            }
        });
    }


    public void registerUser(String uid) {
        // Attempt to get the current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("notes").child(uid);
        Helper helper = new Helper(name, email, password, selectedGender);
        databaseReference.child(uid).child(password).setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Successfully saved user data
                    Toast.makeText(SingUpActivity.this, "Successfully Signed Up!", Toast.LENGTH_SHORT).show();

                    // Save user details in SharedPreferences
                    editor.putString("Email", email);
                    editor.putString("Name", name);
                    editor.putString("Password",password);
                    editor.putBoolean("isLogged", true);
                    editor.apply();

                    // Redirect to the LoginActivity
                    Intent intent = new Intent(SingUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Failed to save user data
                    Toast.makeText(SingUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void changeInProgreeBar(boolean show) {
        if (show) {
            btnSignUp.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateEmail(String userEmail) {
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() && !userEmail.isEmpty();
    }

    private boolean validatePassword(String userPassword) {
        return userPassword.length() >= 5 && !userPassword.isEmpty();
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
