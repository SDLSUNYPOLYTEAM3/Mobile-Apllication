package com.example.sdl_mobileinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class Edit_Info extends AppCompatActivity {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private TextView tvEdit;
    private EditText etEPC_edit;
    private TextView tvInst1;
    private EditText etP1;
    private TextView tvInst2;
    private EditText etP2;
    private Button btnSub;

    private int EPC_len = 5;
    private boolean EPC_length = false;
    private boolean EPC_found = false;

    private float pref1;
    private float pref2;
    private boolean P1_range = false;
    private boolean P2_range = false;
    private boolean pref_sum = false;
    private boolean p1_valid = false;
    private boolean p2_valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        tvEdit = findViewById(R.id.tvEdit);
        etEPC_edit = findViewById(R.id.etEPC_edit);
        tvInst1 = findViewById(R.id.tvInst1);
        etP1 = findViewById(R.id.etP1);
        tvInst2 = findViewById(R.id.tvInst2);
        etP2 = findViewById(R.id.etP2);
        btnSub = findViewById(R.id.btnSub);

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear all error messages
                etEPC_edit.setError(null);
                etP1.setError(null);
                etP2.setError(null);

                //EPC capture-------------------------------------------------------------
                //check if EPC is null
                if (etEPC_edit.getText().toString().equalsIgnoreCase("")) {
                    etEPC_edit.setError("User must Enter EPC");
                } else {
                    //check if EPC is correct length
                    if (etEPC_edit.length() != EPC_len) {
                        EPC_length = false;
                        etEPC_edit.setError("EPC must be 5 binary bits");
                    } else {
                        EPC_length = true;
                    }

                    //find EPC in DB
                    if(EPC_length){
                        EPC_find(etEPC_edit.getText().toString(), new EPC_find_Listener() {
                            @Override
                            public void EPC_exist(boolean EPC_exist) {
                                EPC_found = EPC_exist;
                                Log.d("UserActivity", "EPC_found = " + EPC_found);
                                if (!EPC_found) {
                                    etEPC_edit.setError("EPC not found");
                                }
                            }
                        });
                    }
                }//end EPC capture

                //capture preference1
                if (!etP1.getText().toString().equalsIgnoreCase("")) {
                    pref1 = Float.valueOf(etP1.getText().toString());
                }
                if (etP1.getText().toString().equalsIgnoreCase("")) {
                    etP1.setError("User must enter preference");
                } else {
                    //check if pref is 0-10
                    if ((pref1 < 0) || (pref1 > 10)) {
                        etP1.setError("Preference must be 0-10");
                    } else {
                        P1_range = true;
                    }
                }

                //capture preference2
                if (!etP2.getText().toString().equalsIgnoreCase("")) {
                    pref2 = Float.valueOf(etP2.getText().toString());
                }
                if (etP2.getText().toString().equalsIgnoreCase("")) {
                    etP2.setError("User must enter preference");
                } else {
                    //check if pref is 0-10
                    if ((pref2 < 0) || (pref2 > 10)) {
                        etP2.setError("Preference must be 0-10");
                    } else {
                        P2_range = true;
                    }
                }

                //check if sum = 1
                pref1 = pref1/10;
                pref2 = pref2/10;
                if (P1_range && P2_range) {
                    if (pref1 + pref2 != 1) {
                        Log.d("UserActivity", "Pref1 + Pref2 = " + (pref1 + pref2));
                        etP1.setError(("Preferences must sum to 10"));
                        etP2.setError(("Preferences must sum to 10"));
                    } else {
                        pref_sum = true;
                    }
                }
                //validate preferences
                if (P1_range && P2_range && pref_sum){
                    p2_valid = true;
                    Log.d("UserActivity", "Pref2 captured: " + pref2);
                    p1_valid = true;
                    Log.d("UserActivity", "Pref1 captured: " + pref1);
                }
                //write new user preferences to the cloud DB
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (EPC_found && p1_valid && p2_valid) {
                            Log.d("UserActivity", "Pushing user data to cloud");
                            String epc = etEPC_edit.getText().toString();
                            mDatabase.child(epc + "/pref1").setValue(pref1);
                            mDatabase.child(epc + "/pref2").setValue(pref2);
                            //Log.d("UserActivity", "userID = " + mAuth.getUid());
                            Toast.makeText(Edit_Info.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 500);



            }//end of OnClick
        }); //end of click Listener
    }//end of onCreate

    public interface EPC_find_Listener{
        void EPC_exist(boolean EPC_exist);
    }

    //checkEPC function START
    //checks for EPC duplicates
    public void EPC_find(String epc, final EPC_find_Listener listener) {
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //Log.d("UserActivity", snapshot.getKey() );
                    counter++;
                    if ((snapshot.getKey().equalsIgnoreCase(epc))){
                        listener.EPC_exist(true);
                        Log.d("UserActivity", "EPC found : " + snapshot.getKey() );
                        break;
                    }else {
                        if (counter == dataSnapshot.getChildrenCount()){
                            listener.EPC_exist(false);
                            Log.d("UserActivity", "EPC DNE");
                        }
                    }
                }

            }
        });
    }
}
