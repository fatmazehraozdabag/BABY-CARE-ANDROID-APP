package com.example.babycare;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class SleepActivity extends AppCompatActivity {

    private static final String PREF_KEY = "sleep_records";
    private LinearLayout recordsContainer;
    private Button btnSave;
    private EditText edtSleepStart, edtSleepEnd, edtNote;
    private TextView txtCalculatedDuration;
    private String editingRecordId = null;
    private int calculatedMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sleep);

        edtSleepStart = findViewById(R.id.edtSleepStart);
        edtSleepEnd = findViewById(R.id.edtSleepEnd);
        edtNote = findViewById(R.id.edtSleepNote);
        txtCalculatedDuration = findViewById(R.id.txtCalculatedDuration);

        btnSave = findViewById(R.id.btnSaveSleep);
        recordsContainer = findViewById(R.id.recordsContainer);

        // Kullanıcı klavyeden yazdıkça anlık süreyi hesaplayan dinleyici
        TextWatcher timeWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                recalculateDuration();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtSleepStart.addTextChangedListener(timeWatcher);
        edtSleepEnd.addTextChangedListener(timeWatcher);

        loadRecords();

        btnSave.setOnClickListener(v -> {
            String startTime = edtSleepStart.getText().toString().trim();
            String endTime = edtSleepEnd.getText().toString().trim();
            String note = edtNote.getText().toString().trim();

            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(
                        SleepActivity.this,
                        "Lütfen uyku başlangıç ve bitiş saatini yazın (Örn: 10.00)",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (calculatedMinutes <= 0) {
                Toast.makeText(
                        SleepActivity.this,
                        "Lütfen saatleri doğru formatta yazın (Örn: 10.00 ve 11.30)",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());

            // Saat aralığını formatlayarak kaydedelim (10.00 - 11.30)
            String timeInterval = startTime.replace(':', '.') + " - " + endTime.replace(':', '.');

            String recordId = (editingRecordId != null) ? editingRecordId : String.valueOf(System.currentTimeMillis());

            CareRecordModel record = new CareRecordModel(
                    recordId,
                    "sleep",
                    currentDate,
                    timeInterval,
                    note,
                    0,
                    calculatedMinutes,
                    ""
            );

            RecordStorage.saveOrUpdateModel(this, PREF_KEY, record);

            if (editingRecordId != null) {
                Toast.makeText(this, "Kayıt güncellendi!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Uyku kaydı eklendi (" + formatDuration(calculatedMinutes) + ")!", Toast.LENGTH_SHORT).show();
            }

            resetInputs();
            loadRecords();
        });
    }

    private void recalculateDuration() {
        String start = edtSleepStart.getText().toString().trim();
        String end = edtSleepEnd.getText().toString().trim();

        // Nokta (.) veya iki nokta (:) desteği (örn: 10.00 veya 10:00)
        String delimiter = start.contains(".") ? "\\." : (start.contains(":") ? ":" : null);
        String endDelimiter = end.contains(".") ? "\\." : (end.contains(":") ? ":" : null);

        if (delimiter != null && endDelimiter != null) {
            try {
                String[] startParts = start.split(delimiter);
                String[] endParts = end.split(endDelimiter);

                if (startParts.length >= 2 && endParts.length >= 2) {
                    int startHour = Integer.parseInt(startParts[0]);
                    int startMin = Integer.parseInt(startParts[1]);
                    int endHour = Integer.parseInt(endParts[0]);
                    int endMin = Integer.parseInt(endParts[1]);

                    if (startHour >= 0 && startHour < 24 && startMin >= 0 && startMin < 60 &&
                        endHour >= 0 && endHour < 24 && endMin >= 0 && endMin < 60) {

                        int startTotal = startHour * 60 + startMin;
                        int endTotal = endHour * 60 + endMin;

                        int diff = endTotal - startTotal;
                        // Gece uykuları için (örn: 22.00'den 07.00'ye)
                        if (diff < 0) {
                            diff += 24 * 60;
                        }

                        calculatedMinutes = diff;
                        txtCalculatedDuration.setText("⏱️ Hesaplanan Uyku: " + formatDuration(diff));
                        txtCalculatedDuration.setTextColor(Color.parseColor("#0D47A1"));
                        txtCalculatedDuration.setBackgroundColor(Color.parseColor("#D7E7FF"));
                        return;
                    }
                }
            } catch (Exception ignored) {}
        }

        calculatedMinutes = 0;
        txtCalculatedDuration.setText("⏱️ Hesaplanan Süre: Henüz yazılmadı");
        txtCalculatedDuration.setTextColor(Color.parseColor("#555A6E"));
        txtCalculatedDuration.setBackgroundColor(Color.parseColor("#ECEFF1"));
    }

    private String formatDuration(int totalMinutes) {
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;
        if (hours > 0 && mins > 0) {
            return hours + " saat " + mins + " dakika";
        } else if (hours > 0) {
            return hours + " saat";
        } else {
            return mins + " dakika";
        }
    }

    private void resetInputs() {
        editingRecordId = null;
        edtSleepStart.setText("");
        edtSleepEnd.setText("");
        edtNote.setText("");
        calculatedMinutes = 0;
        txtCalculatedDuration.setText("⏱️ Hesaplanan Süre: Henüz yazılmadı");
        btnSave.setText("Uyku Kaydını Kaydet");
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
            String time = item.getTime();
            if (time != null && time.contains("-")) {
                String[] parts = time.split("-");
                edtSleepStart.setText(parts[0].trim());
                edtSleepEnd.setText(parts[1].trim());
            }
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
