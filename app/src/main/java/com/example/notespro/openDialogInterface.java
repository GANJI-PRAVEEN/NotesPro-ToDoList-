package com.example.notespro;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class openDialogInterface extends AppCompatActivity {
    TextView NoteDetails;
    Button copyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.alarmclicked_layout);
        copyButton=findViewById(R.id.copyButton);
        NoteDetails=findViewById(R.id.taskDetails);
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        String content=intent.getStringExtra("content");
        content.trim();
        NoteDetails.setText(title+"\n"+content);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(openDialogInterface.this, "You Are Now On..!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(openDialogInterface.this,MainActivity.class));
                finish();
            }
        });
    }
}