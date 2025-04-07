package com.example.database;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail;
    Button buttonSave, buttonUpdate, buttonDelete;
    ListView listView;

    AppDatabase db;
    List<Person> personList;
    ArrayAdapter<String> adapter;
    int selectedPersonId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        buttonSave = findViewById(R.id.button_save);
        buttonUpdate = findViewById(R.id.button_update);
        buttonDelete = findViewById(R.id.button_delete);
        listView = findViewById(R.id.list_view_persons);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "person_db")
                .allowMainThreadQueries()
                .build();

        loadList();

        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                showToast("Please fill in both name and email.");
                return;
            }

            Person person = new Person();
            person.name = name;
            person.email = email;
            db.personDao().insert(person);
            clearFields();
            loadList();
            showToast("Saved successfully");
        });

        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if (selectedPersonId == -1) {
                showToast("Please select a person to update.");
                return;
            }

            if (name.isEmpty() || email.isEmpty()) {
                showToast("Please fill in both name and email.");
                return;
            }

            Person person = new Person();
            person.id = selectedPersonId;
            person.name = name;
            person.email = email;
            db.personDao().update(person);
            clearFields();
            loadList();
            showToast("Updated successfully");
        });

        buttonDelete.setOnClickListener(v -> {
            if (selectedPersonId != -1) {
                Person person = new Person();
                person.id = selectedPersonId;
                db.personDao().delete(person);
                clearFields();
                loadList();
                showToast("Deleted successfully");
            } else {
                showToast("Please select a person to delete.");
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Person selected = personList.get(position);
            editTextName.setText(selected.name);
            editTextEmail.setText(selected.email);
            selectedPersonId = selected.id;
        });
    }

    void loadList() {
        personList = db.personDao().getAllPersons();
        String[] items = new String[personList.size()];
        for (int i = 0; i < personList.size(); i++) {
            Person p = personList.get(i);
            items[i] = p.id + " - " + p.name + " : " + p.email;
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    void clearFields() {
        editTextName.setText("");
        editTextEmail.setText("");
        selectedPersonId = -1;
    }

    void showToast(String msg) {
        Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
