package com.example.admin.connfa_android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class UserListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    ArrayList<String> AllUsers;
    ArrayAdapter<String> adapter;
    ListView userListView;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("Speakers");
        addNavigation();

        Intent intentFromMainActivity = getIntent();

        userListView = (ListView) findViewById(R.id.userListView);
        searchEditText = (EditText) findViewById(R.id.searchEditText);

        insertUsersIntoArray();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, AllUsers);
        userListView.setAdapter(adapter);
        //we use a filter to search through the elements in the list of ALllUsers
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (UserListActivity.this).adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //whenever a name is clicked on, the user should be able to message that user so is sent to the ChatActivity
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("SenderEmail", MainActivity.currentUserEmail);
                intent.putExtra("RecipientEmail", findRecipientEmail(AllUsers.get(position)));
                intent.putExtra("RecipientFullName", AllUsers.get(position));
                startActivity(intent);
            }
        });
    }

    //this method is called in the oncreate method, it inserts all the users of the app in the AllUsers arrayList
    public void insertUsersIntoArray() {
        AllUsers = new ArrayList<>();
        try {
            SQLiteDatabase db = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS usersTable (id INTEGER PRIMARY KEY,fullname varchar , email varchar, password varchar)");
            Cursor c = db.rawQuery("SELECT * FROM usersTable ", null);
            int fullnameIndex = c.getColumnIndex("fullname");
            int emailIndex = c.getColumnIndex("email");
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String Userfull = c.getString(fullnameIndex);
                String Useremail = c.getString(emailIndex);
                if (!Useremail.equals(MainActivity.currentUserEmail)) {
                    AllUsers.add(Userfull);
                }
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(AllUsers);
    }


    //when the user clicks on a name, we want to get his email
    public String findRecipientEmail(String fName) {
        try {
            SQLiteDatabase db = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS usersTable (id INTEGER PRIMARY KEY,fullname varchar , email varchar, password varchar)");
            Cursor c = db.rawQuery("SELECT * FROM usersTable ", null);
            int fullnameIndex = c.getColumnIndex("fullname");
            int emailIndex = c.getColumnIndex("email");
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String Userfull = c.getString(fullnameIndex);
                String Useremail = c.getString(emailIndex);
                if (fName.equals(Userfull)) {
                    return Useremail;
                }
                c.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void addNavigation(){
        drawerLayout = (DrawerLayout)findViewById(R.id.activity_user_List);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView)findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.eventsItem){
                    Intent intent = new Intent(getApplicationContext(), ListEventsActivity.class);
                    startActivity(intent);
                }else if(id == R.id.allUsersItem){
                    Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                    startActivity(intent);
                }else if(id == R.id.myMessagesItem){
                    Intent intent = new Intent(getApplicationContext(), MyMessagesActivity.class);
                    startActivity(intent);
                }else if(id == R.id.logOutItem){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

}