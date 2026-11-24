package com.example.babycare;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BabyProfileActivity extends AppCompatActivity {

    public static final String PREF_NAME = "BabyProfilePrefs";
    public static final String KEY_NAME = "baby_name";
    public static final String KEY_BIRTH = "baby_birth";
    public static final String KEY_GENDER = "baby_gender";
    public static final String KEY_BLOOD = "baby_blood";

    private EditText edtBabyName, edtBirthDate, edtBloodType;
    private TextView txtProfileAgeDisplay;
    private Button btnGenderGirl, btnGenderBoy, btnSaveProfile, btnGoToGrowth;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_profile);

        edtBabyName = findViewById(R.id.edtBabyName);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtBloodType = findViewById(R.id.edtBloodType);
        txtProfileAgeDisplay = findViewById(R.id.txtProfileAgeDisplay);

        btnGenderGirl = findViewById(R.id.btnGenderGirl);
        btnGenderBoy = findViewById(R.id.btnGenderBoy);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnGoToGrowth = findViewById(R.id.btnGoToGrowth);

        btnGenderGirl.setOnClickListener(v -> selectGender("Kız"));
        btnGenderBoy.setOnClickListener(v -> selectGender("Erkek"));

        edtBirthDate.setOnClickListener(v -> showDatePicker());

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnGoToGrowth.setOnClickListener(v -> {
            Intent intent = new Intent(BabyProfileActivity.this, GrowthActivity.class);
            startActivity(intent);
        });

        loadExistingProfile();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, month + 1, year);
            edtBirthDate.setText(dateStr);
            updateAgeDisplay(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        // Gelecek tarih seçilmesin
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void selectGender(String gender) {
        selectedGender = gender;
        if ("Kız".equals(gender)) {
            btnGenderGirl.setBackgroundColor(Color.parseColor("#F06292"));
            btnGenderGirl.setTextColor(Color.WHITE);
            btnGenderBoy.setBackgroundColor(Color.parseColor("#E3F2FD"));
            btnGenderBoy.setTextColor(Color.parseColor("#1565C0"));
        } else {
            btnGenderBoy.setBackgroundColor(Color.parseColor("#42A5F5"));
            btnGenderBoy.setTextColor(Color.WHITE);
            btnGenderGirl.setBackgroundColor(Color.parseColor("#FCE4EC"));
            btnGenderGirl.setTextColor(Color.parseColor("#C2185B"));
        }
    }

    private void updateAgeDisplay(String birthDateStr) {
        String ageText = calculateAge(birthDateStr);
        if (ageText != null) {
            txtProfileAgeDisplay.setText("🎂 Bebeğin Yaşı: " + ageText);
            txtProfileAgeDisplay.setBackgroundColor(Color.parseColor("#D7E7FF"));
            txtProfileAgeDisplay.setTextColor(Color.parseColor("#0D47A1"));
        }
    }

    public static String calculateAge(String birthDateStr) {
        if (birthDateStr == null || !birthDateStr.contains(".")) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            Date birth = sdf.parse(birthDateStr);
            if (birth == null) return null;

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birth);

            Calendar nowCal = Calendar.getInstance();
            if (nowCal.before(birthCal)) return "0 günlük";

            long diffMillis = nowCal.getTimeInMillis() - birthCal.getTimeInMillis();
            long totalDays = diffMillis / (1000 * 60 * 60 * 24);

            if (totalDays < 30) {
                return totalDays + " günlük";
            }

            int years = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            int months = nowCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH);
            int days = nowCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH);

            if (days < 0) {
                months--;
                nowCal.add(Calendar.MONTH, -1);
                days += nowCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            if (months < 0) {
                years--;
                months += 12;
            }

            if (years > 0) {
                if (months > 0) {
                    return years + " yaş " + months + " aylık";
                }
                return years + " yaşında";
            } else {
                if (days > 0) {
                    return months + " ay " + days + " günlük";
                }
                return months + " aylık";
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void saveProfile() {
        String name = edtBabyName.getText().toString().trim();
        String birth = edtBirthDate.getText().toString().trim();
        String blood = edtBloodType.getText().toString().trim();

        if (name.isEmpty() || birth.isEmpty()) {
            Toast.makeText(this, "Lütfen bebeğin adını ve doğum tarihini girin!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_NAME, name)
                .putString(KEY_BIRTH, birth)
                .putString(KEY_GENDER, selectedGender)
                .putString(KEY_BLOOD, blood)
                .apply();

        Toast.makeText(this, "Bebek profili kaydedildi! 👶", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadExistingProfile() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "");
        String birth = prefs.getString(KEY_BIRTH, "");
        String gender = prefs.getString(KEY_GENDER, "");
        String blood = prefs.getString(KEY_BLOOD, "");

        if (!name.isEmpty()) {
            edtBabyName.setText(name);
        }
        if (!birth.isEmpty()) {
            edtBirthDate.setText(birth);
            updateAgeDisplay(birth);
        }
        if (!gender.isEmpty()) {
            selectGender(gender);
        }
        if (!blood.isEmpty()) {
            edtBloodType.setText(blood);
        }
    }
}
