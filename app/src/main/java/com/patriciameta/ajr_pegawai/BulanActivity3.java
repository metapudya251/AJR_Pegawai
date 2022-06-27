package com.patriciameta.ajr_pegawai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class BulanActivity3 extends AppCompatActivity {
    private LinearLayout mei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulan3);

        mei = findViewById(R.id.mei);
        mei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(BulanActivity3.this, Top5DriverActivity.class));
            }
        });
    }
}