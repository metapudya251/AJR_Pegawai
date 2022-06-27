package com.patriciameta.ajr_pegawai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class BulanActivity4 extends AppCompatActivity {
    private LinearLayout mei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulan4);

        mei = findViewById(R.id.mei);
        mei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(BulanActivity4.this, Top5CustActivity.class));
            }
        });
    }
}