package com.example.notespro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceHelper {
        private static final String PREF_NAME = "MyPrefs";
        private static final String KEY_NOTES_LIST = "notes_list";
        private List<Pair<Integer,NotesModalClass>>listOfNotes;

        // Method to save a list of objects to SharedPreferences
        public  void saveNotesList(Context context, List<NotesModalClass> notesList) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(listOfNotes);
            editor.putString(KEY_NOTES_LIST, json);
            editor.apply();
        }

        // Method to retrieve a list of objects from SharedPreferences
        public List<NotesModalClass> getNotesList(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String json = sharedPreferences.getString(KEY_NOTES_LIST, null);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<NotesModalClass>>() {}.getType();
            return gson.fromJson(json, type);
        }
}
