package com.example.todolist;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    private ListView listview;
    private TextView textview1;
    private Button button;
    private EditText editText;
    private KeyListener originalKeyListener;
    private ArrayList<String> strings;
    private  ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        strings = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strings);
        listview.setAdapter(adapter);
        readItems();
        editText.setVisibility(View.INVISIBLE);
        originalKeyListener = editText.getKeyListener();
        editText.setKeyListener(null);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
                editText.setKeyListener(originalKeyListener);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);

            }
        });

        String strUserName = editText.getText().toString();
        if (strUserName.trim().equals("")) {
            Toast.makeText(this, "plz enter your name ", Toast.LENGTH_SHORT).show();
            return;
        }

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String text = editText.getText().toString();
                    strings.add(text);
                    editText.getText().clear();
                    editText.setVisibility(View.INVISIBLE);
                    adapter.notifyDataSetChanged();
                    handled = true;
                    writeItems();
                }
                return false;
            }
        });

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, strings);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                strings.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Task Completed", Toast.LENGTH_LONG).show();
                writeItems();
                return true;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If it loses focus...
                if (!hasFocus) {
                    // Hide soft keyboard.
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    // Make it non-editable again.
                    editText.setKeyListener(null);
                }
            }
        });
    }

        private void readItems () {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                strings = new ArrayList<String>(FileUtils.readLines(todoFile));
            } catch (IOException e) {
                strings = new ArrayList<String>();
            }
        }

        private void writeItems () {
            File filesDir = getFilesDir();
            File todoFile = new File(filesDir, "todo.txt");
            try {
                FileUtils.writeLines(todoFile, strings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

