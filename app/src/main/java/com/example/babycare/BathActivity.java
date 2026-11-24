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

public class BathActivity extends AppCompatActivity {

    private static final String PREF_KEY = "bath_records";
    private LinearLayout recordsContainer;
    private Button btnSave;
    private EditText edtDuration, edtNote;
    private Button btnBathDur5, btnBathDur10, btnBathDur15, btnBathDur20;
    private Button btnNoteHappy, btnNoteHair, btnNoteMassage;
    private String editingRecordId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bath);

        edtDuration = findViewById(R.id.edtBathDuration);
        edtNote = findViewById(R.id.edtBathNote);

        btnSave = findViewById(R.id.btnSaveBath);
        recordsContainer = findViewById(R.id.recordsContainer);

        btnBathDur5 = findViewById(R.id.btnBathDur5);
        btnBathDur10 = findViewById(R.id.btnBathDur10);
        btnBathDur15 = findViewById(R.id.btnBathDur15);
        btnBathDur20 = findViewById(R.id.btnBathDur20);

        btnNoteHappy = findViewById(R.id.btnNoteHappy);
        btnNoteHair = findViewById(R.id.btnNoteHair);
        btnNoteMassage = findViewById(R.id.btnNoteMassage);

        // Hızlı süre butonları (5 dk, 10 dk, 15 dk, 20 dk)
        btnBathDur5.setOnClickListener(v -> edtDuration.setText("5"));
        btnBathDur10.setOnClickListener(v -> edtDuration.setText("10"));
        btnBathDur15.setOnClickListener(v -> edtDuration.setText("15"));
        btnBathDur20.setOnClickListener(v -> edtDuration.setText("20"));

        // Hızlı hazır not butonları
        btnNoteHappy.setOnClickListener(v -> appendNote("Huzurlu geçti"));
        btnNoteHair.setOnClickListener(v -> appendNote("Saç yıkandı"));
        btnNoteMassage.setOnClickListener(v -> appendNote("Masaj yapıldı"));

        loadRecords();

        btnSave.setOnClickListener(v -> {

            String durationText = edtDuration.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (durationText.isEmpty()) {
                Toast.makeText(
                        BathActivity.this,
                        "Lütfen banyo süresini girin veya butonları kullanın!",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int duration = Integer.parseInt(durationText);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = new Date();

            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);

            String recordId = (editingRecordId != null) ? editingRecordId : String.valueOf(System.currentTimeMillis());

            CareRecordModel record = new CareRecordModel(
                    recordId,
                    "bath",
                    currentDate,
                    currentTime,
                    note,
                    0,
                    duration,
                    ""
            );

            RecordStorage.saveOrUpdateModel(this, PREF_KEY, record);

            if (editingRecordId != null) {
                Toast.makeText(this, "Kayıt güncellendi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Banyo kaydı eklendi (" + duration + " dk)!", Toast.LENGTH_SHORT).show();
            }

            resetInputs();
            loadRecords();
        });
    }

    private void appendNote(String text) {
        String current = edtNote.getText().toString().trim();
        if (current.isEmpty()) {
            edtNote.setText(text);
        } else if (!current.contains(text)) {
            edtNote.setText(current + ", " + text);
        }
    }

    private void resetInputs() {
        editingRecordId = null;
        edtDuration.setText("");
        edtNote.setText("");
        btnSave.setText("Banyo Kaydını Kaydet");
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
            edtDuration.setText(String.valueOf(item.getDuration()));
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
