package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;

import com.example.myapplication.adapter.NoteAdapter;
import com.example.myapplication.bean.Note;
import com.example.myapplication.databaseHelper.NotepadSqliteOpenHelper;
import com.example.myapplication.R;
import com.example.myapplication.utils.SpfUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class Notepad_Main extends AppCompatActivity {

    RecyclerView rlv_note;
    FloatingActionButton fbt_note_add;
    private List<Note> mNotes;
    private NotepadSqliteOpenHelper notepadSqliteOpenHelper;
    private NoteAdapter noteAdapter;
    private int currentListLayoutMode = NoteAdapter.TYPE_LINEAR_LAYOUT;

    public static final String KEY_NOTE_LAYOUT_MODE = "key_layout_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_main);
        viewinit();
        DBinit();
        Eventinit();
    }

    /*
     *保存当前的布局选项
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item;
        if (currentListLayoutMode == NoteAdapter.TYPE_LINEAR_LAYOUT) {
            item = menu.findItem(R.id.menu_note_linear).setChecked(true);
        } else if (currentListLayoutMode == NoteAdapter.TYPE_GRID_LAYOUT) {
            item = menu.findItem(R.id.menu_note_grid).setChecked(true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("ResourceType")
    private void Eventinit() {
        noteAdapter = new NoteAdapter(this, mNotes);
        rlv_note.setAdapter(noteAdapter);
        setToLL();
    }

    private void DBinit() {
        notepadSqliteOpenHelper = new NotepadSqliteOpenHelper(this);
        mNotes = new ArrayList<>();
//        Note note = Note.builder().title("title1").content("content1").createTime(new Date()).build();
//        mNotes.add(note);
    }

    /*
     *    从数据库中得到数据
     */
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

    /*
     * 回到界面刷新
     * */
    @Override
    protected void onResume() {
        super.onResume();
        refreshDataFromDB();
        setLayout();

    }

    private void setToGL() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rlv_note.setLayoutManager(gridLayoutManager);
        noteAdapter.setViewType(NoteAdapter.TYPE_GRID_LAYOUT);
        noteAdapter.notifyDataSetChanged();
    }

    private void setToLL() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlv_note.setLayoutManager(linearLayoutManager);
        noteAdapter.setViewType(NoteAdapter.TYPE_LINEAR_LAYOUT);
        noteAdapter.notifyDataSetChanged();
    }

    private void setLayout() {
        currentListLayoutMode = SpfUtil.getIntWithDefault(this, KEY_NOTE_LAYOUT_MODE, NoteAdapter.TYPE_LINEAR_LAYOUT);
        if (currentListLayoutMode == NoteAdapter.TYPE_LINEAR_LAYOUT) {
            setToLL();
        } else if (currentListLayoutMode == NoteAdapter.TYPE_GRID_LAYOUT) {
            setToGL();
        }
    }

    private void refreshDataFromDB() {
        mNotes = getDataFromDB();
        noteAdapter.refreshData(mNotes);
    }

    /*
     * 创建菜单
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_note_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNotes = notepadSqliteOpenHelper.quearyFromDbByTitle(newText);
                noteAdapter.refreshData(mNotes);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * 选中菜单
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.menu_note_linear:
                setToLL();
                currentListLayoutMode = NoteAdapter.TYPE_LINEAR_LAYOUT;
                SpfUtil.saveInt(this, KEY_NOTE_LAYOUT_MODE, currentListLayoutMode);
                return true;
            case R.id.menu_note_grid:
                setToGL();
                currentListLayoutMode = NoteAdapter.TYPE_GRID_LAYOUT;
                SpfUtil.saveInt(this, KEY_NOTE_LAYOUT_MODE, currentListLayoutMode);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}