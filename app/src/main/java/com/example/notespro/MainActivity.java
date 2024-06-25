package com.example.notespro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final int REQUEST_CODE = 1;
    TextView txtDataNotFound;
    ImageView searchIcon;
    MyAdapter myAdapter;
    TextView txtHeadeName;
    String DBMAIL;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView sideNavigationView;
    SearchView searchView;
    List<NotesModalClass> notesList;



    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if(itemId==R.id.menu_logOut){
            editor.putBoolean("isLogged",false);
            editor.putString("Email","");
            editor.putString("Password","");
            editor.apply();
            AlertDialog.Builder builder=new AlertDialog.Builder(this)
                    .setMessage("Are You Sure To LogOut..?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this,LoginActivity.class));

                                }
                            }).setNegativeButton("Cancel",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        if(itemId==R.id.menu_aboutUs){
            AlertDialog.Builder builder=new AlertDialog.Builder(this).setMessage("This is A ToDoList App Developed By Ganji Praveen having Good UI Design and Able to Add new task Every Second and Update On Time And Safely Stored on Your credentials")
                    .setPositiveButton("Nice",null)
                    .setNegativeButton("Cool",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("Storage",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        sideNavigationView=findViewById(R.id.sideNavigationDrawer);
        floatingActionButton = findViewById(R.id.add_note);
        floatingActionButton.setOnClickListener((v) -> {
        Intent intent = new Intent(MainActivity.this, NotesActivity.class);
        startActivity(intent);

//            startActivityForResult(intent, REQUEST_CODE);

        });
        searchView=findViewById(R.id.searchView);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.GONE);
                searchIcon.setVisibility(View.VISIBLE);
                txtDataNotFound.setVisibility(View.GONE);
                return true;
            }
        });
        searchIcon=findViewById(R.id.searchIcon);
        toolbar=findViewById(R.id.toolbar);
        View HeaderaView = sideNavigationView.getHeaderView(0);
        txtHeadeName=HeaderaView.findViewById(R.id.txtHeaderName);
        String Name=sharedPreferences.getString("Name","");
        txtHeadeName.setText(Name);
        txtDataNotFound=findViewById(R.id.dataNotFound);
        //Navigation View SetUp

//         BroadcastReceiver dataChangeReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(context, "SetUpRecyler View()", Toast.LENGTH_SHORT).show();
//                setUpRecyclerView();
//            }
//        };

            searchIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSearchView();
                }
            });
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
            });





        sideNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId=menuItem.getItemId();
                if(itemId==R.id.myLogout){
                    showAlertDialog("Are You Sure To LogOut..?");

                }
                if(itemId==R.id.myAccount){
                    startActivity(new Intent(MainActivity.this,AccountActivity.class));
                    finish();
                }
                return true;
            }
        });


        drawerLayout=findViewById(R.id.drawerLayout);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        recyclerView = findViewById(R.id.recyclerView);
        setUpRecyclerView();
    }
    public void callDeleteMethod(String docId){
        NotesActivity notesActivity=new NotesActivity();
        notesActivity.DeleteNoteFromFirebase(docId);
    }

    public void setUpRecyclerView() {
        try {
            DBMAIL = sharedPreferences.getString("Email", "");
            CollectionReference collectionReference = FirebaseFirestore.getInstance()
                    .collection("notes")
                    .document("IkxTtjXxJdXK0bPjdq3FI5FjqEM2")
                    .collection(DBMAIL);
            notesList = new ArrayList<>();
            collectionReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NotesModalClass note = document.toObject(NotesModalClass.class);
                        note.setId(document.getId());  // Assuming NotesModalClass has a method setId()
                        notesList.add(note);
                    }
                    notesList=getUniqueList(notesList);
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Error while loading data", Toast.LENGTH_SHORT).show();
                }
            });
            myAdapter = new MyAdapter(notesList, this, new NotesActivity());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(myAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error while loading data", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (myAdapter != null) {
            setUpRecyclerView();
        }
    }
    public void showAlertDialog(String msg) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
                        editor.putString("Email","");
                        editor.putString("Password","");
                        editor.apply();
                        finish();
                    }
                })
                .setNegativeButton("Cancel",null);
        AlertDialog alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    private void filter(String query) {
        List<NotesModalClass> filteredList = new ArrayList<>();
        if (TextUtils.isEmpty(query)) {
            myAdapter.filterList(notesList);
        }
        else {
            for (NotesModalClass note : notesList) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        note.getContent().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(note);
                }
            }
            if (filteredList.isEmpty()) {
                txtDataNotFound.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                txtDataNotFound.setVisibility(View.GONE);
                myAdapter.filterList(filteredList);
            }
        }
    }
    private void toggleSearchView() {
        searchIcon.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }
    public List<NotesModalClass>getUniqueList(List<NotesModalClass>notes){
        Set<String> keys = new HashSet<>();
        List<NotesModalClass> uniqueList = new ArrayList<>();
        for (NotesModalClass note : notesList) {
            String key = note.getTitle() + "-" + note.getContent(); // Combining title and content as the key
            if (keys.add(key)) {
                uniqueList.add(note);
            }
        }
        return uniqueList;
    }

}
