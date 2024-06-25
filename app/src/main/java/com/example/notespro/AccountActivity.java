package com.example.notespro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {
    TextView accountName,accountEmail,accountGender,accountPassword;
    String name,password,email,gender;
    Button moveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_view);
        accountName=findViewById(R.id.accountantName);
        accountEmail=findViewById(R.id.accountantEmail);
        accountPassword=findViewById(R.id.accountantPassword);
        accountGender=findViewById(R.id.accountantGender);
        moveButton=findViewById(R.id.moveButton);
        SharedPreferences sharedPreferences=getSharedPreferences("Storage",MODE_PRIVATE);
        String DBPASSWORD=sharedPreferences.getString("Password","");
        Toast.makeText(this, "Password"+DBPASSWORD, Toast.LENGTH_SHORT).show();
        DatabaseReference userData = FirebaseDatabase.getInstance().getReference("UserData").child("IkxTtjXxJdXK0bPjdq3FI5FjqEM2").child(DBPASSWORD);
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name=snapshot.child("name").getValue(String.class);
                    email=snapshot.child("email").getValue(String.class);
                    password=snapshot.child("password").getValue(String.class);
                    gender=snapshot.child("gender").getValue(String.class);
                    setCredentials();
                }
                else{
                    Toast.makeText(AccountActivity.this, "Error While Loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountActivity.this,MainActivity.class));
                finish();
            }
        });



    }
    public void setCredentials(){
        accountName.setText(name);
        accountEmail.setText(email);
        accountGender.setText(gender);
        accountPassword.setText(password);
    }
}