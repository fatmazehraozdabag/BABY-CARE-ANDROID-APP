package com.example.babycare;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaperActivity extends AppCompatActivity {

    private static final String PREF_KEY = "diaper_records";
    private LinearLayout recordsContainer;
    private Button btnSave;
    private Button btnTypeWet, btnTypeDirty, btnTypeBoth, btnTypeClean;
    private EditText edtDiaperType, edtNote;
    private String editingRecordId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_diaper);

        edtDiaperType = findViewById(R.id.edtDiaperType);
        edtNote = findViewById(R.id.edtDiaperNote);

        btnSave = findViewById(R.id.btnSaveDiaper);
        recordsContainer = findViewById(R.id.recordsContainer);

        btnTypeWet = findViewById(R.id.btnTypeWet);
        btnTypeDirty = findViewById(R.id.btnTypeDirty);
        btnTypeBoth = findViewById(R.id.btnTypeBoth);
        btnTypeClean = findViewById(R.id.btnTypeClean);

        // Hızlı buton tıklamaları
        btnTypeWet.setOnClickListener(v -> edtDiaperType.setText("Islak Bez"));
        btnTypeDirty.setOnClickListener(v -> edtDiaperType.setText("Kirli Bez"));
        btnTypeBoth.setOnClickListener(v -> edtDiaperType.setText("Islak & Kirli Bez"));
        btnTypeClean.setOnClickListener(v -> edtDiaperType.setText("Temiz Bez"));

        loadRecords();

        btnSave.setOnClickListener(v -> {

            String diaperType = edtDiaperType.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (diaperType.isEmpty()) {
                Toast.makeText(
                        DiaperActivity.this,
                        "Lütfen bez durumunu seçin veya yazın!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = new Date();

            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);

            String recordId = (editingRecordId != null) ? editingRecordId : String.valueOf(System.currentTimeMillis());

            CareRecordModel record = new CareRecordModel(
                    recordId,
                    "diaper",
                    currentDate,
                    currentTime,
                    note,
                    0,
                    0,
                    diaperType
            );

            RecordStorage.saveOrUpdateModel(this, PREF_KEY, record);

            if (editingRecordId != null) {
                Toast.makeText(this, "Kayıt güncellendi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bez kaydı eklendi (" + diaperType + ")!", Toast.LENGTH_SHORT).show();
            }

            resetInputs();
            loadRecords();
        });
    }

    private void resetInputs() {
        editingRecordId = null;
        edtDiaperType.setText("");
        edtNote.setText("");
        btnSave.setText("Bez Kaydını Kaydet");
    }

    private void loadRecords() {
        recordsContainer.removeAllViews();
        List<CareRecordModel> list = RecordStorage.getRecordModels(this, PREF_KEY);

        if (list.isEmpty()) {
            TextView emptyTxt = new TextView(this);
            emptyTxt.setText("Henüz kayıt bulunmuyor.");
            emptyTxt.setTextSize(16);
            emptyTxt.setPadding(0, 16, 0, 16);
            recordsContainer.addView(emptyTxt);
            return;
        }

        for (CareRecordModel item : list) {
            recordsContainer.addView(createRecordCardView(item));
        }
    }

    private View createRecordCardView(CareRecordModel item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 24, 28, 24);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);
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
            edtDiaperType.setText(item.getDiaperType());
            edtNote.setText(item.getNote());
            btnSave.setText("Kaydı Güncelle");
            Toast.makeText(this, "Düzenleme moduna geçildi.", Toast.LENGTH_SHORT).show();
        });

        Button btnDelete = new Button(this);
        btnDelete.setText("Sil");
        btnDelete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Kaydı Sil")
                .setMessage("Bu kaydı silmek istediğinize emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    RecordStorage.deleteModel(this, PREF_KEY, item.getId());
                    Toast.makeText(this, "Kayıt silindi.", Toast.LENGTH_SHORT).show();
                    if (item.getId().equals(editingRecordId)) {
                        resetInputs();
                    }
                    loadRecords();
                })
                .setNegativeButton("İptal", null)
                .show());

        btnLayout.addView(btnEdit);
        btnLayout.addView(btnDelete);
        card.addView(btnLayout);

        return card;
    }
}
