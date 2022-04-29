package com.example.sdl_mobileinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class HomePageActivity extends AppCompatActivity {

    //Firebase setup -----------------------------------------------------------------------
    private static final String TAG = "ReadAndWriteSnippets";

    // [START declare_database_ref]
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    // [END declare_database_ref]

//    public void ReadAndWriteSnippets(DatabaseReference database) {
//        // [START initialize_database_ref]
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        // [END initialize_database_ref]
//    }


    //variables -----------------------------------------------------------------------------
    private Button btnSignOut;
    private Button btnSubmit;
    private TextView tvWelcome;
    private EditText etEPC;
    private EditText etFname;
    private EditText etLname;
    private EditText etPref1;
    private EditText etPref2;

    private String EPC = null;
    private boolean duplicate_EPC = true;
    private boolean EPC_valid = false;
    private String Fname = null;
    private boolean Fname_valid = false;
    private String Lname = null;
    private boolean Lname_valid = false;
    private float Pref1 = -1;
    private boolean Pref1_valid = false;
    private float Pref2 = -1;
    private boolean Pref2_valid = false;


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // [START config_signOut]
        // Configure Google Sign out
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signOut]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        //UI stuff -------------------------------------------------------------------------
        tvWelcome = findViewById(R.id.tvWelcome);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnSubmit = findViewById(R.id.btnSubmit);
        etEPC = findViewById(R.id.etEPC);
        etFname = findViewById(R.id.etFname);
        etLname = findViewById(R.id.etLname);
        etPref1 = findViewById(R.id.etPref1);
        etPref2 = findViewById(R.id.etP2);


        //Personalized welcome banner
        String currentUser = mAuth.getCurrentUser().getDisplayName();
        if (currentUser != null) {
            tvWelcome.setText("Welcome " + currentUser);
        }

        //SignOut button
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        //Submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EPC variables
                if (!etEPC.getText().toString().equalsIgnoreCase("")) {
                    EPC = etEPC.getText().toString();
                }
                boolean EPC_type = true;
                boolean EPC_length = false;
                duplicate_EPC = true;
                EPC_valid = false;
                //Unique_EPC = true;
                int EPC_len = 5;

                //name variables
                if (!etFname.getText().toString().equalsIgnoreCase("")) {
                    Fname = etFname.getText().toString();
                    Fname = Fname.substring(0, 1).toUpperCase() + Fname.substring(1);
                }
                if (!etLname.getText().toString().equalsIgnoreCase("")) {
                    Lname = etLname.getText().toString();
                    Lname = Lname.substring(0, 1).toUpperCase() + Lname.substring(1);
                }
                boolean name_isAlpha;

                //preferences variables
                if (!etPref1.getText().toString().equalsIgnoreCase("")) {
                    Pref1 = Float.valueOf(etPref1.getText().toString());
                }
                if (!etPref2.getText().toString().equalsIgnoreCase("")) {
                    Pref2 = Float.valueOf(etPref2.getText().toString());
                }
                boolean Pref1_range = false;
                boolean Pref2_range = false;
                boolean Pref1_int = false;
                boolean Pref2_int = false;
                boolean Pref_sum = false;

                //clear all error messages
                etEPC.setError(null);
                etFname.setError(null);
                etLname.setError(null);
                etPref1.setError(null);
                etPref2.setError(null);

                //EPC capture-------------------------------------------------------------
                //check if EPC is null
                if (etEPC.getText().toString().equalsIgnoreCase("")) {
                    etEPC.setError("User must Enter EPC");
                } else {   //check if EPC is binary
                    char[] numbers = EPC.toCharArray();
                    //Log.d("UserActivity", "numbers array is: " + numbers);
                    for (char num : numbers) {
                        if (num != '0' && num != '1') {
                            EPC_type = false;
                        }
                    }
                    if (EPC_type == false) {
                        etEPC.setError("EPC must be binary");
                    }
                    //check if EPC is correct length
                    if (EPC.length() != EPC_len) {
                        EPC_length = false;
                        etEPC.setError("EPC must be 5 binary bits");
                    } else {
                        EPC_length = true;
                    }
                    //Check for duplicate EPC
                    if (EPC_length && EPC_type) {
                        //duplicate_EPC = check_EPC_duplicate(EPC);
                        check_EPC_duplicate(EPC,
                                new check_EPC_duplicate_Listener() {
                                    @Override
                                    public void Unique_EPC(boolean Unique_EPC) {
                                        duplicate_EPC = !Unique_EPC;
                                        Log.d("UserActivity", "duplicate_EPC = " + duplicate_EPC);

                                        //validate EPC
                                        if (duplicate_EPC) {
                                            etEPC.setError("Duplicate EPC detected");
                                        }else{
                                            EPC_valid = true;
                                            Log.d("UserActivity", "EPC_valid = " + EPC_valid);
                                        }
                                    }
                                });
                    }

                }
                //First name capture
                if (etFname.getText().toString().equalsIgnoreCase("")) {
                    etFname.setError("User must enter first name");
                } else {
                    //check if name is only letters
                    name_isAlpha = Fname.matches("[a-zA-Z]+");
                    if (!name_isAlpha) {
                        etFname.setError("Letters only!");
                    } else {
                        Fname_valid = true;
                        Log.d("UserActivity", "Fname captured: " + Fname);
                    }
                }
                //Last name capture
                if (etLname.getText().toString().equalsIgnoreCase("")) {
                    etLname.setError("User must enter last name");
                } else {
                    //check if name is only letters
                    name_isAlpha = Lname.matches("[a-zA-Z]+");
                    if (!name_isAlpha) {
                        etLname.setError("Letters only!");
                    } else {
                        Lname_valid = true;
                        Log.d("UserActivity", "Lname captured: " + Lname);
                    }
                }
                //capture preference1
                if (Pref1 == -1) {
                    etPref1.setError("User must enter preference");
                } else {
                    //check if pref is 0-10
                    if ((Pref1 < 0) || (Pref1 > 10)) {
                        etPref1.setError("Preference must be 0-10");
                    } else {
                        Pref1_range = true;
                    }
                    if (Pref1 % 1 != 0) {
                        etPref1.setError(("Preference must be integer"));
                    } else {
                        Pref1_int = true;
                        Pref1 = Pref1 / 10;
                        //String Pref1_String = String.format("%.1f", Pref1);
                        //Log.d("UserActivity", "Pref1_String = " + Pref1_String);
                        //Pref1 = Float.parseFloat(Pref1_String);
                        //Log.d("UserActivity", "Pref1 = " + (Pref1));
                    }
                }
                //capture preference2
                if (Pref2 == -1) {
                    etPref2.setError("User must enter preference");
                } else {
                    //check if pref is 0-10
                    if ((Pref2 < 0) || (Pref2 > 10)) {
                        etPref2.setError("Preference must be 0-10");
                    } else {
                        Pref2_range = true;
                    }
                    if (Pref2 % 1 != 0) {
                        etPref2.setError(("Preference must be integer"));
                    } else {
                        Pref2_int = true;
                        Pref2 = Pref2 / 10;
                        //String Pref2_String = String.format("%.1f", Pref2);
                        //Log.d("UserActivity", "Pref2_String = " + Pref2_String);
                        //Pref2 = Float.parseFloat(Pref2_String);
                        //Log.d("UserActivity", "Pref2 = " + (Pref2));
                    }
                }
                //check if sum = 1
                if (Pref1_int && Pref1_range && Pref2_int && Pref2_range) {
                    if (Pref1 + Pref2 != 1) {
                        Log.d("UserActivity", "Pref1 + Pref2 = " + (Pref1 + Pref2));
                        etPref1.setError(("Preferences must sum to 10"));
                        etPref2.setError(("Preferences must sum to 10"));
                    } else {
                        Pref_sum = true;
                    }
                }
                if (Pref_sum && Pref1_int && Pref1_range && Pref2_int && Pref2_range) {
                    Pref2_valid = true;
                    Log.d("UserActivity", "Pref2 captured: " + Pref2);
                    Pref1_valid = true;
                    Log.d("UserActivity", "Pref1 captured: " + Pref1);
                }
                //write new user data to the cloud DB
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("UserActivity", "!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                        if (EPC_valid && Fname_valid && Lname_valid && Pref1_valid && Pref2_valid) {
//                            Log.d("UserActivity", "Pushing user data to cloud");
//                            CustomUser user = new CustomUser(Fname, Lname, Pref1, Pref2);
//                            //mDatabase.child(EPC).setValue(user);
//                        }
//                    }
//                };
//                Thread thread1 = new Thread(runnable);
//                thread1.start();
//                try{
//                    thread1.sleep(5000);
//                }catch ( Exception e){
//
//                }


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (EPC_valid && Fname_valid && Lname_valid && Pref1_valid && Pref2_valid) {
                            Log.d("UserActivity", "Pushing user data to cloud");
                            CustomUser user = new CustomUser(Fname, Lname, Pref1, Pref2);
                            mDatabase.child(EPC).setValue(user);
                            //Log.d("UserActivity", "userID = " + mAuth.getUid());
                            Toast.makeText(HomePageActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 500);

                //Reset all editText
//                etEPC.setText("");
//                etFname.setText("");
//                etLname.setText("");
//                etPref1.setText("");
//                etPref2.setText("");
            }
        });
    }

    //--------------------------------------------------------------------------------------
    private void signOut() {
        //Firebase sign out
        // [START auth_sign_out]
        mAuth.signOut();
        // [END auth_sign_out]

        //Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //go back to main activity
                        Intent gotoMain = new Intent(HomePageActivity.this, MainActivity.class);
                        startActivity(gotoMain);
                    }
                });
    }

    public interface check_EPC_duplicate_Listener{
        void Unique_EPC(boolean Unique_EPC);
    }

    //checkEPC function START
    //checks for EPC duplicates
    public void check_EPC_duplicate(String epc, final check_EPC_duplicate_Listener listener) {
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                Log.d("UserActivity", "Looking for duplicates");
                Log.d("UserActivity", "snapshot size = " + Float.valueOf(dataSnapshot.getChildrenCount()).toString());
                long counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    counter++;
                    Log.d("UserActivity", "counter = " + counter);
                    if (snapshot.getKey().equalsIgnoreCase(epc)) {
                        listener.Unique_EPC(false);
                        Log.d("UserActivity", "Duplicate detected: " + snapshot.getKey());
                        break;
                    } else {
                        if (counter == dataSnapshot.getChildrenCount()) {
                            listener.Unique_EPC(true);
                            Log.d("UserActivity", "No duplicates detected!");
                            ;
                        }
                    }
                }
            }
        });
    }


//    public void check_EPC_duplicate(String epc, final check_EPC_duplicate_Listener listener){
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("UserActivity", "Looking for duplicates");
//                Log.d("UserActivity", "snapshot size = " + Float.valueOf(dataSnapshot.getChildrenCount()).toString());
//                long counter = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    counter++;
//                    Log.d("UserActivity", "counter = " + counter);
//                    if (snapshot.getKey().equalsIgnoreCase(epc)) {
//                        listener.Unique_EPC(false);
//                        Log.d("UserActivity", "Duplicate detected: " + snapshot.getKey());
//                        break;
//                    }else{
//                        if (counter == dataSnapshot.getChildrenCount()){
//                            listener.Unique_EPC(true);
//                            Log.d("UserActivity", "No duplicates detected!");;
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                throw error.toException();
//            }
//        });
//    }

}



// [START custom_user_class]
@IgnoreExtraProperties
class CustomUser {

    public String epc;
    public String fname;
    public String lname;
    public Float pref1;
    public Float pref2;

    public CustomUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public CustomUser(String fname, String lname, float Pref1, float Pref2) {
        //this.epc = epc;
        this.fname = fname;
        this.lname = lname;
        this.pref1 = Pref1;
        this.pref2 = Pref2;
    }

}