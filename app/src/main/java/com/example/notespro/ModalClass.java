package com.example.notespro;

public class ModalClass {
    int count;
    NotesModalClass notesModalClass;

    public ModalClass(int count, NotesModalClass notesModalClass) {
        this.count = count;
        this.notesModalClass = notesModalClass;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public NotesModalClass getNotesModalClass() {
        return notesModalClass;
    }

    public void setNotesModalClass(NotesModalClass notesModalClass) {
        this.notesModalClass = notesModalClass;
    }
}
