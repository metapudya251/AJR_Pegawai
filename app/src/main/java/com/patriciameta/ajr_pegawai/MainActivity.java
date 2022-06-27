package com.patriciameta.ajr_pegawai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.textview.MaterialTextView;
import com.patriciameta.ajr_pegawai.preferences.UserPreferences;

public class MainActivity extends AppCompatActivity {
    public static Activity mainActivity;

    private MaterialTextView tvWelcome, tvName;
    private ImageView ivUser;
    private UserPreferences userPreferences;
    private LinearLayout mobil, cust, driver5, cust5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Ubah Title pada App Bar Aplikasi
        setTitle("Home");

        userPreferences = new UserPreferences(MainActivity.this);

        //  set Text View
        tvWelcome = findViewById(R.id.tv_welcome);
        tvName = findViewById(R.id.tv_name);

        tvName.setText(userPreferences.getUserLogin().getNama());
        //  Set Text View
        ivUser = findViewById(R.id.imageView2);
        mobil = findViewById(R.id.lap_mobil);
        cust = findViewById(R.id.lap_cust);
        driver5 = findViewById(R.id.top_driver);
        cust5 = findViewById(R.id.top_cust);

        mobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, BulanActivity.class));
            }
        });

        cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, BulanActivity2.class));
            }
        });

        driver5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, BulanActivity3.class));
            }
        });

        cust5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(MainActivity.this, BulanActivity4.class));
            }
        });

        checkLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_logout) {
            // Jika menu yang dipilih adalah menu Exit, maka tampilkan sebuah dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_confirm).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    userPreferences.logout();
                    checkLogin();
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkLogin(){
        if(!userPreferences.checkLogin()){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}