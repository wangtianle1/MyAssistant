package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.adapter.NoteAdapter;
import com.example.myapplication.bean.Note;
import com.example.myapplication.databaseHelper.NotepadSqliteOpenHelper;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Notepad_Main extends AppCompatActivity {

    RecyclerView rlv_note;
    FloatingActionButton fbt_note_add;
    private List<Note> mNotes;
    private NotepadSqliteOpenHelper notepadSqliteOpenHelper;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_main);
        viewinit();
        DBinit();
        Eventinit();
    }

    private void Eventinit() {
        noteAdapter = new NoteAdapter(this, mNotes);
        rlv_note.setAdapter(noteAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlv_note.setLayoutManager(linearLayoutManager);
    }

    private void DBinit() {
        notepadSqliteOpenHelper = new NotepadSqliteOpenHelper(this);
        mNotes = new ArrayList<>();

        Note note = Note.builder().title("title1").content("content1").createTime(new Date().toString()).build();
        mNotes.add(note);
    }
//        从数据库中得到数据
    private List<Note> getDataFromDB() {
        return notepadSqliteOpenHelper.queryAllFromDB();

    }
    private void viewinit() {
        rlv_note = findViewById(R.id.rlv_note);
        fbt_note_add = findViewById(R.id.fbt_note_add);

    }

    public void addNote(View view) {
        startActivity(new Intent(this, Note_Add.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDB();
    }

    private void refreshDataFromDB() {
        mNotes = getDataFromDB();
        noteAdapter.refreshData(mNotes);
    }
}