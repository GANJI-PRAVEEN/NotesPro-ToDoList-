package com.example.notespro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NotesActivity extends AppCompatActivity {
    EditText title, content;
    FloatingActionButton savenote;
    TextView pageTitleTextView;
    MaterialTimePicker timePicker;
    String NoteContent;
    String NoteTitle;
    Calendar calendar;
    boolean isEditMode = false;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    List<NotesModalClass>notesList;
    String docId;
    private Button btnDeleteNote;
    Toolbar toolbar2;
    String DBMAIL;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button btnSetAlarm;
    boolean rightImageClicked=false;
    TextView txtAlarm;
    public Context getContext(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        notesList=new ArrayList<>();
        setContentView(R.layout.activity_notes);
        title = findViewById(R.id.note_title);
        content = findViewById(R.id.content_note);
        savenote = findViewById(R.id.fabAdd);
        btnSetAlarm=findViewById(R.id.btnSetAlarm);
        pageTitleTextView = findViewById(R.id.pageTitleTextView);
        sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtAlarm = findViewById(R.id.txtAlarmSet);
        savenote.setOnClickListener((v)->{
            rightImageClicked=true;
            saveNote(true);
        });
        docId = getIntent().getStringExtra("docId");
        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }



        title.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Right drawable index
                if(checkTheWidgets()) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (title.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                            // Calculate touch area for right drawable
                            int drawableRightStart = title.getRight() - title.getPaddingRight() - title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                            if (event.getRawX() >= drawableRightStart) {
                                // Handle drawable right click
                                askPermissionForNotifications();
//                                makeNotificationChannel();
                                openTimePicker();
                                return true; // Consume the event
                            }
                        }
                    }
                }
                return false; // Let the event pass through for other interactions
            }
        });
        btnSetAlarm.setOnClickListener((v)->{
            if(timePicker==null){
                showAlertDialog("please Set The Alarm");
                return;
            }
            else{
                setAlarm();
            }
        });
    }
    public boolean checkTheWidgets(){
        if(title.getText().toString().isEmpty())return false;
        if(content.getText().toString().isEmpty())return false;
        return true;
    }
    public void askPermissionForNotifications(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(NotesActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NotesActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
    public void makeNotificationChannel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name="myChannel";
            int imp= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel=new NotificationChannel("PraveenChannel",name,imp);
            notificationChannel.setDescription("This is Used to Remember the Tasks");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
    public void openTimePicker(){
        timePicker= new MaterialTimePicker.Builder()
                .setTitleText("Please Set The Alarm Reminder")
                .setHour(0)
                .setMinute(0)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .build();
        timePicker.show(getSupportFragmentManager(),"channel_id");
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtAlarm.setVisibility(View.INVISIBLE);
                if(timePicker.getHour()>12){
                    txtAlarm.setText("Alarm Set To - "+
                            String.format("%02d",timePicker.getHour()-12)+":"+String.format("%02d",timePicker.getMinute())+"PM"
                    );
                }
                else{
                    txtAlarm.setText("Alarm Set To -"+
                        String.format("%02d",timePicker.getHour())+":"+String.format("%02d",timePicker.getMinute())+"AM"
                    );
                }

            }
        });

    }
    public void setAlarm(){
        txtAlarm.setVisibility(View.VISIBLE);
        Calendar now=Calendar.getInstance();


        calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        calendar.set(Calendar.MINUTE,timePicker.getMinute());
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        // If the alarm time is before the current time, set it for the next day
        //Remember this if Logic Man Damn
        if (calendar.before(now)) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent intent=new Intent(NotesActivity.this,AlarmReceiver.class);
        intent.putExtra("title",title.getText().toString());
        intent.putExtra("content",content.getText().toString());
        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        intent.setAction("android.intent.action.NOTIFY");

        int uniqueID = generateStrongUniqueId();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }
        try {
            pendingIntent = PendingIntent.getBroadcast(this, uniqueID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            long time=System.currentTimeMillis();
            getDate(time);
            getDate(calendar.getTimeInMillis());
            if (Build.VERSION.SDK_INT >= 23){
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            }
            else{
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }           Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Exception "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void getDate(long timeInMillis){
        Date date=new Date(timeInMillis);
        Log.e("Date ",date.toString());
    }

    public void saveNote(boolean flagToAlarm) {
        if(timePicker==null){
            showAlertDialog("Please Set The Alarm Before Proceeding..!");
            return;
        }
        try {
            NoteTitle = title.getText().toString();
            NoteContent = content.getText().toString();
            NoteContent.trim();
            if (NoteTitle.isEmpty() || NoteTitle == null) {
                Toast.makeText(this, "Title Shouldn't Be Null", Toast.LENGTH_SHORT).show();
                return;
            }
            NotesModalClass note;
            int day=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            int month = Calendar.getInstance().get(Calendar.MONTH)+1;
            note = new NotesModalClass(NoteTitle, NoteContent, calendar.getTime(), day, month);
            //to update the Note not to create one more just give docId in saveNote Method
            saveNoteInFirebase(note, new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    try {
                        if (task.isSuccessful()) {
                            String docuID = task.getResult().getId();
                            note.setId(docuID);
                            Toast.makeText(NotesActivity.this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(NotesActivity.this, "Error While Adding", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        Log.e("ExceptionAT",e.toString());
                    }
                }
            });
        }catch(Exception e){
            Toast.makeText(this, "Please Click On Set Alarm...!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveNoteInFirebase(NotesModalClass note, OnCompleteListener<DocumentReference> onCompleteListener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DBMAIL = sharedPreferences.getString("Email", "");

        FirebaseFirestore.getInstance().collection("notes")
                .document("IkxTtjXxJdXK0bPjdq3FI5FjqEM2")
                .collection(DBMAIL)
                .add(note)
                .addOnCompleteListener(onCompleteListener);
    }

    public void DeleteNoteFromFirebase(String docId) {
        try{
            DocumentReference documentReference;
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DBMAIL = sharedPreferences.getString("Email", "");
            documentReference = FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection(DBMAIL).document(docId);
            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(NotesActivity.this, "Succesfully Note Deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e) {
            Toast.makeText(this, "Error While Deleting", Toast.LENGTH_SHORT).show();
        }

    }
    private int generateStrongUniqueId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
    public String getDayAsString(int day){
        switch(day){
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tue";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thu";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            case Calendar.SUNDAY:
                return "Sun";
        }
        return "Invaid";
    }
    public void showAlertDialog(String msg) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("Ok",null)
                .setNegativeButton("ThankYou",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


}
