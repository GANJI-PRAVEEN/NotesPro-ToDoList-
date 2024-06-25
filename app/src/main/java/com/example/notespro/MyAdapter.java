package com.example.notespro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    String DBMAIL;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String docIdforMenu;
    NotesActivity notesActivity;
    List<NotesModalClass> notesList;
    List<NotesModalClass>filteredNotesList;
    public MyAdapter(List<NotesModalClass> notesList, Context context, NotesActivity notesActivity) {
        this.notesList = notesList;
        this.context = context;
        this.notesActivity = notesActivity;
        sharedPreferences = context.getSharedPreferences("Storage", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.newlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        NotesModalClass notesModalClass = notesList.get(position);
        myViewHolder.day.setText(getDayAsString(notesModalClass.getDay()));
        myViewHolder.date.setText(getSingleDate(notesModalClass.getDate()));
        myViewHolder.month.setText(getMonthAsString(notesModalClass.getMonth()));
        myViewHolder.title.setText(notesModalClass.getTitle());
        myViewHolder.content.setText(notesModalClass.getContent());
        myViewHolder.time.setText(convert(notesModalClass.getDate()));

        myViewHolder.options.setOnClickListener((view) -> {
            int pos = myViewHolder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                showPopUpMenu(view, pos);
            }
        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        try {
            return notesList.size();
        } catch (Exception e) {
        }
        return 0;
    }

    public void showPopUpMenu(View view, int position) {
        try {
            docIdforMenu = notesList.get(position).getId();  // Assuming NotesModalClass has a method getId()
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int itemId = item.getItemId();
                    if (itemId == R.id.updateNote) {
                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                    }
                    if (itemId == R.id.deleteNote) {
                        showDeleteDialog(position);
                    }
                    if (itemId == R.id.completeNote) {
                        showAlertDialog("Are You Sure The Task is Successfully Completed..?",position);
                    }
                    return true;
                }
            });
            popupMenu.show();
        } catch (Exception e) {
            Log.e("Exception ", e.toString());
            Toast.makeText(context, "Please Wait..! Loading", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("Are You Sure To Delete Note..?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notesList.remove(position);
                        notifyItemRemoved(position);
                        DeleteNoteFromDB();
                        Toast.makeText(context, "Successfully Note Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public String convert(Date date) {
        try {
            String Format = date.toString();
            String trucateString = Format.substring(0, 19);
            return trucateString;
        } catch (Exception e) {
            Log.e("Exception Here", e.toString());
        }
        return "TimeStamp";
    }

    public String getSingleDate(Date date) {
        try {
            String dateFormat = date.toString();
            String singleDate = dateFormat.substring(8, 10);
            return singleDate;
        } catch (Exception e) {
            return "Err";
        }
    }

    public String getDayAsString(int day) {
        switch (day) {
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
        return "Inv";
    }

    public String getMonthAsString(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
        }
        return "Inv";
    }

    public void showCompleteDialog(int position) {
        try {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.complete_theme);
            Button close = dialog.findViewById(R.id.closeButton);
            close.setOnClickListener(view -> {
                notesList.remove(position);
                notifyItemRemoved(position);
                DeleteNoteFromDB();
                dialog.dismiss();
                DeleteNoteFromDB();
                Toast.makeText(context, "Successfully Completed", Toast.LENGTH_SHORT).show();
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(context, "Exception While Opening", Toast.LENGTH_SHORT).show();
        }
    }

    public void DeleteNoteFromDB() {
        try {
            notifyDataSetChanged();
            SharedPreferences sharedPreferences1 = context.getSharedPreferences("Storage", Context.MODE_PRIVATE);
            String DBMAIL = sharedPreferences1.getString("Email", "");
            DocumentReference dref = FirebaseFirestore.getInstance().collection("notes").document("IkxTtjXxJdXK0bPjdq3FI5FjqEM2").collection(DBMAIL).document(docIdforMenu);
            dref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "SuccessFully Deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Errow While Deleting", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Exception While Deleting", e.toString());
        }
    }
    public void filterList(List<NotesModalClass>filteredNotesList){
        this.notesList=filteredNotesList;
        notifyDataSetChanged();
    }
    public void showAlertDialog(String msg,int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showCompleteDialog(position);
                    }
                }).setNegativeButton("No",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
