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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener, View.OnKeyListener{

    EditText fullnameEditText, emailEditText, passwordEditText ;
    Button loginOptionChosenButton, registerOptionChosenButton , submitInfoButton ;
    RelativeLayout registerOrLoginRelativeLayout;
    LinearLayout fieldsLinearLayout ;

    boolean isInRegisterMode ;

    SQLiteDatabase db ;

    public static String currentUserEmail;
    public static String currentUserFullname ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Connfa");

        //when the user enters the activity, he is in the register form, so the boolean is set to true
        isInRegisterMode = true ;

        //all the elements of the activity_main.xml are set in the oncreate method
        fullnameEditText = (EditText) findViewById(R.id.fullNameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText=  (EditText) findViewById(R.id.passwordEditText);
        submitInfoButton = (Button) findViewById(R.id.submitInfoButton);
        fieldsLinearLayout = (LinearLayout) findViewById(R.id.fieldsLinearLayout);
        registerOrLoginRelativeLayout = (RelativeLayout) findViewById(R.id.registerOrLoginRelativeLayout);

        //we set onclickListeners for both the login and register buttons.
        loginOptionChosenButton = (Button) findViewById(R.id.loginOptionChosenButton);
        registerOptionChosenButton = (Button) findViewById(R.id.registerChosenButton);
        loginOptionChosenButton.setOnClickListener(this);
        registerOptionChosenButton.setOnClickListener(this);

        //we also set an onKey listener on the password so that when the users click enter after writing his password, the button is automatically clicked
        passwordEditText=  (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);


    }

    @Override
    public void onClick(View view) {
        //when the user in the register form and clicks on the Login button, he goes to the login form (same activity) but the boolean isInRegisterMode is set to false
        if(view.getId() == R.id.loginOptionChosenButton && isInRegisterMode != false){
            submitInfoButton.setText("Login");
            isInRegisterMode = false ;
            //when in the login form, no need to enter your fullName bcz you are already in the database
            fieldsLinearLayout.removeView(fullnameEditText);
            //when the user in the login form and clicks on the Register button, he goes to the login form (same activity) but the boolean isInRegisterMode is set to true
        }else if(view.getId() == R.id.registerChosenButton && isInRegisterMode!=true ){
            submitInfoButton.setText("Register");
            isInRegisterMode = true ;
            //when in the register form, you need to enter you fullname bcz you are not currently in the database
            fieldsLinearLayout.addView(fullnameEditText, 0);
        }
    }

    public void submitInfo(View view){
        //conditions in case the user clicks on the button while in the register form
        if(isInRegisterMode == true ){
            String fullname = fullnameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password= passwordEditText.getText().toString();
            //check if the user left an empty field
            if(fullname.length() == 0 || email.length()== 0 || password.length() == 0){
                Toast.makeText(this, "You must fill all fields", Toast.LENGTH_SHORT).show();
            }else {
                //check if the password is less than 8 characters
                if(password.length() < 8 ){
                    Toast.makeText(this, "Password must contain at least 8 characters", Toast.LENGTH_SHORT).show();
                }else {
                    //check if there are no digits in the password
                    if(password.matches("\\d+")){
                        Toast.makeText(this, "Password must contain a digit", Toast.LENGTH_SHORT).show();
                    }else {
                        try {
                            //connect to the database
                            SQLiteDatabase db = this.openOrCreateDatabase("Users",MODE_PRIVATE,null);
                            db.execSQL("CREATE TABLE IF NOT EXISTS usersTable (id INTEGER PRIMARY KEY,fullname varchar , email varchar, password varchar)");
                            Cursor c = db.rawQuery("SELECT * FROM usersTable WHERE email ='"+ email + "'",null);
                            //if the user is not in the database, add him
                            if(c.getCount()== 0){
                                db.execSQL("INSERT INTO usersTable (fullname, email, password) VALUES ('" + fullname + "','"  + email + "','" + password + "')"  );
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                                currentUserEmail = email ;
                                currentUserFullname = fullname;
                                showUserList();
                                //if the user is in the database, notify that there already exist a user with this email
                            }else {
                                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else {
            //if the user is in the login mode, check for the conditions
            String email = emailEditText.getText().toString();
            String password= passwordEditText.getText().toString();
            //check if the user left an empty field
            if(email.length()==0 || password.length() == 0){
                Toast.makeText(this, "You must fill all fields", Toast.LENGTH_SHORT).show();
            }else {
                SQLiteDatabase db = this.openOrCreateDatabase("Users",MODE_PRIVATE,null);
                db.execSQL("CREATE TABLE IF NOT EXISTS usersTable (id INTEGER PRIMARY KEY,fullname varchar , email varchar, password varchar)");
                Cursor c = db.rawQuery("SELECT * FROM usersTable WHERE email ='"+ email + "' AND password = '"+ password + "'",null);
                //chekc if the user is not in the database
                if(c.getCount() == 0 ){
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                    //if the user is in the database, log him in
                }else {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    int fullnameIndex = c.getColumnIndex("fullname");
                    c.moveToFirst();
                    String name = c.getString(fullnameIndex);
                    currentUserEmail = email;
                    currentUserFullname = name ;
                    showUserList();
                }
            }
        }
    }


    //this function sends the user to the user list, he is logged in and his name will not appear in the user list
    public void showUserList(){
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);

    }

    //This function allows the user to submit his info whenever he clicks on enter while writing his password
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            submitInfo(view);
        }
        return false;
    }



}

