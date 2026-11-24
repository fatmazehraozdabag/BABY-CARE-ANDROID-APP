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

public class FeedingActivity extends AppCompatActivity {

    private static final String PREF_KEY = "feeding_records";
    private LinearLayout recordsContainer;
    private Button btnSave;
    private EditText edtAmount, edtDuration, edtNote;
    private Button btnAdd30, btnAdd60, btnAdd90, btnAdd120, btnClearAmount;
    private Button btnDur5, btnDur10, btnDur15, btnDur20;
    private String editingRecordId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feeding);

        edtAmount = findViewById(R.id.edtAmount);
        edtDuration = findViewById(R.id.edtDuration);
        edtNote = findViewById(R.id.edtNote);

        btnSave = findViewById(R.id.btnSaveFeeding);
        recordsContainer = findViewById(R.id.recordsContainer);

        btnAdd30 = findViewById(R.id.btnAdd30);
        btnAdd60 = findViewById(R.id.btnAdd60);
        btnAdd90 = findViewById(R.id.btnAdd90);
        btnAdd120 = findViewById(R.id.btnAdd120);
        btnClearAmount = findViewById(R.id.btnClearAmount);

        btnDur5 = findViewById(R.id.btnDur5);
        btnDur10 = findViewById(R.id.btnDur10);
        btnDur15 = findViewById(R.id.btnDur15);
        btnDur20 = findViewById(R.id.btnDur20);

        // Hızlı miktar butonları (+30, +60, +90, +120, Sil)
        btnAdd30.setOnClickListener(v -> addAmount(30));
        btnAdd60.setOnClickListener(v -> addAmount(60));
        btnAdd90.setOnClickListener(v -> addAmount(90));
        btnAdd120.setOnClickListener(v -> addAmount(120));
        btnClearAmount.setOnClickListener(v -> edtAmount.setText(""));

        // Hızlı süre butonları (5 dk, 10 dk, 15 dk, 20 dk)
        btnDur5.setOnClickListener(v -> edtDuration.setText("5"));
        btnDur10.setOnClickListener(v -> edtDuration.setText("10"));
        btnDur15.setOnClickListener(v -> edtDuration.setText("15"));
        btnDur20.setOnClickListener(v -> edtDuration.setText("20"));

        loadRecords();

        btnSave.setOnClickListener(v -> {

            String amountText = edtAmount.getText().toString().trim();
            String durationText = edtDuration.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (amountText.isEmpty() || durationText.isEmpty()) {

                Toast.makeText(
                        FeedingActivity.this,
                        "Lütfen miktar ve süreyi girin veya butonları kullanın!",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            int amount = Integer.parseInt(amountText);
            int duration = Integer.parseInt(durationText);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = new Date();

            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);

            String recordId = (editingRecordId != null) ? editingRecordId : String.valueOf(System.currentTimeMillis());

            CareRecordModel record = new CareRecordModel(
                    recordId,
                    "feeding",
                    currentDate,
                    currentTime,
                    note,
                    amount,
                    duration,
                    ""
            );

            RecordStorage.saveOrUpdateModel(this, PREF_KEY, record);

            if (editingRecordId != null) {
                Toast.makeText(this, "Kayıt güncellendi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Beslenme kaydı eklendi (" + amount + " ml, " + duration + " dk)!", Toast.LENGTH_SHORT).show();
            }

            resetInputs();
            loadRecords();
        });
    }

    private void addAmount(int addValue) {
        String current = edtAmount.getText().toString().trim();
        int curVal = 0;
        try {
            if (!current.isEmpty()) {
                curVal = Integer.parseInt(current);
            }
        } catch (NumberFormatException ignored) {}
        edtAmount.setText(String.valueOf(curVal + addValue));
    }

    private void resetInputs() {
        editingRecordId = null;
        edtAmount.setText("");
        edtDuration.setText("");
        edtNote.setText("");
        btnSave.setText("Beslenme Kaydını Kaydet");
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
            edtAmount.setText(String.valueOf(item.getAmount()));
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
