package com.example.sdl_mobileinterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ShowEPC extends AppCompatActivity {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private TextView tvShow;
    private TextView tvEPC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_epc);

        tvShow = findViewById(R.id.tvShow);
        tvEPC = findViewById(R.id.tvEPC);

        //get EPCs
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                String EPC = "\n";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("UserActivity", snapshot.getKey() );
                    if (!(snapshot.getKey().equalsIgnoreCase("Default"))){
                         EPC = EPC + (snapshot.getKey()+"\n");
                    }
                }
                tvEPC.setText(EPC);

            }
        });

    }

}