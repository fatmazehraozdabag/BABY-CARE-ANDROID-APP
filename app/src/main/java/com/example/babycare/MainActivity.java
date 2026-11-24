package com.example.babycare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View layoutProfileHeader;
    private TextView txtBabyHeaderName, txtBabyHeaderAge;
    private Button btnDailySummary, btnFeeding, btnSleeping, btnDiaper, btnBath, btnGrowth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        layoutProfileHeader = findViewById(R.id.layoutProfileHeader);
        txtBabyHeaderName = findViewById(R.id.txtBabyHeaderName);
        txtBabyHeaderAge = findViewById(R.id.txtBabyHeaderAge);

        btnDailySummary = findViewById(R.id.btnDailySummary);
        btnFeeding = findViewById(R.id.btnFeeding);
        btnSleeping = findViewById(R.id.btnSleeping);
        btnDiaper = findViewById(R.id.btnDiaper);
        btnBath = findViewById(R.id.btnBath);
        btnGrowth = findViewById(R.id.btnGrowth);

        layoutProfileHeader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BabyProfileActivity.class);
            startActivity(intent);
        });

        btnDailySummary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DailySummaryActivity.class);
            startActivity(intent);
        });

        btnFeeding.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeedingActivity.class);
            startActivity(intent);
        });

        btnSleeping.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SleepActivity.class);
            startActivity(intent);
        });

        btnDiaper.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DiaperActivity.class);
            startActivity(intent);
        });

        btnBath.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BathActivity.class);
            startActivity(intent);
        });

        btnGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GrowthActivity.class);
            startActivity(intent);
        });

        updateHeaderProfileInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeaderProfileInfo();
    }

    private void updateHeaderProfileInfo() {
        SharedPreferences prefs = getSharedPreferences(BabyProfileActivity.PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(BabyProfileActivity.KEY_NAME, "");
        String birth = prefs.getString(BabyProfileActivity.KEY_BIRTH, "");
        String gender = prefs.getString(BabyProfileActivity.KEY_GENDER, "");

        if (!name.isEmpty()) {
            String emoji = "👧".equals(gender) ? " 👧" : ("Erkek".equals(gender) ? " 👦" : " 👶");
            txtBabyHeaderName.setText(name + emoji);

            String age = BabyProfileActivity.calculateAge(birth);
            if (age != null) {
                txtBabyHeaderAge.setText("Bugün " + age + " 🎂");
            } else {
                txtBabyHeaderAge.setText("Profil detayları için dokunun");
            }
        } else {
            txtBabyHeaderName.setText("Baby Care 👶");
            txtBabyHeaderAge.setText("Profil oluşturmak için dokun ✏️");
        }
    }
}
