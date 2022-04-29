package com.example.sdl_mobileinterface;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Menu extends AppCompatActivity {

    //variables-----------------------------------------------------
    private TextView tvMenu;
    private Button btnRegister;
    private Button btnEdit;
    private Button btnShowEPC;
    private Button btnETA;
    private Button btnML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //UI Declaration-----------------------------------------------------
        tvMenu = findViewById(R.id.tvMenu);
        btnRegister = findViewById(R.id.btnRegister);
        btnEdit = findViewById(R.id.btnEdit);
        btnShowEPC = findViewById(R.id.btnShowEPC);
        btnETA = findViewById(R.id.btnETA);
        btnML = findViewById(R.id.btnML);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoRegister = new Intent(Menu.this, HomePageActivity.class);
                startActivity(gotoRegister);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoEdit = new Intent(Menu.this, Edit_Info.class);
                startActivity(gotoEdit);
            }
        });

        btnShowEPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoShowEPC = new Intent(Menu.this, ShowEPC.class);
                startActivity(gotoShowEPC);
            }
        });

        btnETA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoETA = new Intent(Menu.this, ETA_calc.class);
                startActivity(gotoETA);
            }
        });

        btnML.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoML = new Intent(Menu.this, ML1.class);
                startActivity(gotoML);
            }
        }));

    }
}