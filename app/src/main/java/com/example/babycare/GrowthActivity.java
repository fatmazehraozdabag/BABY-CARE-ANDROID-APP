package com.example.babycare;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GrowthActivity extends AppCompatActivity {

    private static final String PREF_NAME = "BabyGrowthPrefs";
    private static final String KEY_GROWTH = "growth_records";

    private EditText edtGrowthDate, edtGrowthWeight, edtGrowthHeight, edtGrowthHead, edtGrowthNote;
    private Button btnSaveGrowth;
    private LinearLayout growthRecordsContainer;
    private String editingRecordId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_growth);

        edtGrowthDate = findViewById(R.id.edtGrowthDate);
        edtGrowthWeight = findViewById(R.id.edtGrowthWeight);
        edtGrowthHeight = findViewById(R.id.edtGrowthHeight);
        edtGrowthHead = findViewById(R.id.edtGrowthHead);
        edtGrowthNote = findViewById(R.id.edtGrowthNote);

        btnSaveGrowth = findViewById(R.id.btnSaveGrowth);
        growthRecordsContainer = findViewById(R.id.growthRecordsContainer);

        // Varsayılan olarak bugünün tarihini koy
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        edtGrowthDate.setText(sdf.format(new Date()));

        edtGrowthDate.setOnClickListener(v -> showDatePicker());

        btnSaveGrowth.setOnClickListener(v -> saveGrowthRecord());

        loadGrowthRecords();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, month + 1, year);
            edtGrowthDate.setText(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveGrowthRecord() {
        String date = edtGrowthDate.getText().toString().trim();
        String weight = edtGrowthWeight.getText().toString().trim();
        String height = edtGrowthHeight.getText().toString().trim();
        String head = edtGrowthHead.getText().toString().trim();
        String note = edtGrowthNote.getText().toString().trim();

        if (weight.isEmpty() && height.isEmpty() && head.isEmpty()) {
            Toast.makeText(this, "Lütfen en az bir ölçüm girin (Kilo, Boy veya Baş Çevresi)!", Toast.LENGTH_SHORT).show();
            return;
        }

        String recordId = (editingRecordId != null) ? editingRecordId : String.valueOf(System.currentTimeMillis());
        GrowthRecordModel model = new GrowthRecordModel(recordId, date, weight, height, head, note);

        List<GrowthRecordModel> list = getGrowthRecords();
        boolean updated = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(model.getId())) {
                list.set(i, model);
                updated = true;
                break;
            }
        }
        if (!updated) {
            list.add(0, model);
        }
        saveAllGrowthRecords(list);

        if (editingRecordId != null) {
            Toast.makeText(this, "Gelişim ölçümü güncellendi!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Yeni gelişim ölçümü eklendi! 📏", Toast.LENGTH_SHORT).show();
        }

        resetInputs();
        loadGrowthRecords();
    }

    private void resetInputs() {
        editingRecordId = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        edtGrowthDate.setText(sdf.format(new Date()));
        edtGrowthWeight.setText("");
        edtGrowthHeight.setText("");
        edtGrowthHead.setText("");
        edtGrowthNote.setText("");
        btnSaveGrowth.setText("Ölçümü Kaydet");
    }

    private List<GrowthRecordModel> getGrowthRecords() {
        List<GrowthRecordModel> list = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_GROWTH, "[]");
        try {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                GrowthRecordModel model = GrowthRecordModel.fromJsonObject(obj);
                if (model != null) {
                    list.add(model);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveAllGrowthRecords(List<GrowthRecordModel> list) {
        JSONArray array = new JSONArray();
        for (GrowthRecordModel m : list) {
            array.put(m.toJsonObject());
        }
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_GROWTH, array.toString()).apply();
    }

    private void loadGrowthRecords() {
        growthRecordsContainer.removeAllViews();
        List<GrowthRecordModel> list = getGrowthRecords();

        if (list.isEmpty()) {
            TextView emptyTxt = new TextView(this);
            emptyTxt.setText("Henüz gelişim ölçümü kaydedilmedi.");
            emptyTxt.setTextSize(15);
            emptyTxt.setTextColor(Color.GRAY);
            emptyTxt.setPadding(0, 16, 0, 16);
            growthRecordsContainer.addView(emptyTxt);
            return;
        }

        for (GrowthRecordModel item : list) {
            growthRecordsContainer.addView(createGrowthCardView(item));
        }
    }

    private View createGrowthCardView(GrowthRecordModel item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 24, 28, 24);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 18);
        card.setLayoutParams(params);
        card.setBackgroundColor(Color.parseColor("#F5F5F5"));

        TextView txtContent = new TextView(this);
        txtContent.setText(item.getSummary());
        txtContent.setTextSize(15);
        txtContent.setTextColor(Color.parseColor("#333333"));
        card.addView(txtContent);

        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setPadding(0, 14, 0, 0);

        Button btnEdit = new Button(this);
        btnEdit.setText("Düzenle");
        btnEdit.setOnClickListener(v -> {
            editingRecordId = item.getId();
            edtGrowthDate.setText(item.getDate());
            edtGrowthWeight.setText(item.getWeight());
            edtGrowthHeight.setText(item.getHeight());
            edtGrowthHead.setText(item.getHead());
            edtGrowthNote.setText(item.getNote());
            btnSaveGrowth.setText("Ölçümü Güncelle");
            Toast.makeText(this, "Düzenleme moduna geçildi.", Toast.LENGTH_SHORT).show();
        });

        Button btnDelete = new Button(this);
        btnDelete.setText("Sil");
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Ölçümü Sil")
                .setMessage("Bu gelişim ölçümünü silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    List<GrowthRecordModel> currentList = getGrowthRecords();
                    for (int i = 0; i < currentList.size(); i++) {
                        if (currentList.get(i).getId().equals(item.getId())) {
                            currentList.remove(i);
                            break;
                        }
                    }
                    saveAllGrowthRecords(currentList);
                    Toast.makeText(this, "Ölçüm silindi.", Toast.LENGTH_SHORT).show();
                    if (item.getId().equals(editingRecordId)) {
                        resetInputs();
                    }
                    loadGrowthRecords();
                })
                .setNegativeButton("İptal", null)
                .show());

        btnLayout.addView(btnEdit);
        btnLayout.addView(btnDelete);
        card.addView(btnLayout);

        return card;
    }
}
