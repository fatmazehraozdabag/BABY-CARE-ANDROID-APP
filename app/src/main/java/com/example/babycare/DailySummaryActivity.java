package com.example.babycare;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailySummaryActivity extends AppCompatActivity {

    private TextView txtTodayDate;
    private TextView txtFeedingSummary, txtSleepSummary, txtDiaperSummary, txtBathSummary;
    private LinearLayout summaryRecordsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_summary);

        txtTodayDate = findViewById(R.id.txtTodayDate);
        txtFeedingSummary = findViewById(R.id.txtFeedingSummary);
        txtSleepSummary = findViewById(R.id.txtSleepSummary);
        txtDiaperSummary = findViewById(R.id.txtDiaperSummary);
        txtBathSummary = findViewById(R.id.txtBathSummary);
        summaryRecordsContainer = findViewById(R.id.summaryRecordsContainer);

        loadDailySummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDailySummary();
    }

    private void loadDailySummary() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String today = dateFormat.format(new Date());
        txtTodayDate.setText("Tarih: " + today);

        List<CareRecordModel> feedingList = RecordStorage.getRecordModels(this, "feeding_records");
        List<CareRecordModel> sleepList = RecordStorage.getRecordModels(this, "sleep_records");
        List<CareRecordModel> diaperList = RecordStorage.getRecordModels(this, "diaper_records");
        List<CareRecordModel> bathList = RecordStorage.getRecordModels(this, "bath_records");

        int totalFeedingAmount = 0;
        int totalFeedingDuration = 0;
        int feedingCount = 0;
        for (CareRecordModel m : feedingList) {
            if (today.equals(m.getDate())) {
                totalFeedingAmount += m.getAmount();
                totalFeedingDuration += m.getDuration();
                feedingCount++;
            }
        }
        txtFeedingSummary.setText("Toplam: " + totalFeedingAmount + " ml | " + totalFeedingDuration + " dakika (" + feedingCount + " öğün)");

        int totalSleepDuration = 0;
        int sleepCount = 0;
        for (CareRecordModel m : sleepList) {
            if (today.equals(m.getDate())) {
                totalSleepDuration += m.getDuration();
                sleepCount++;
            }
        }
        txtSleepSummary.setText("Toplam Uyku: " + totalSleepDuration + " dakika (" + sleepCount + " kez)");

        int diaperCount = 0;
        for (CareRecordModel m : diaperList) {
            if (today.equals(m.getDate())) {
                diaperCount++;
            }
        }
        txtDiaperSummary.setText("Toplam: " + diaperCount + " bez değişimi");

        int bathCount = 0;
        int totalBathDuration = 0;
        for (CareRecordModel m : bathList) {
            if (today.equals(m.getDate())) {
                totalBathDuration += m.getDuration();
                bathCount++;
            }
        }
        txtBathSummary.setText("Toplam: " + bathCount + " kez (" + totalBathDuration + " dakika)");

        summaryRecordsContainer.removeAllViews();
        List<CareRecordModel> todayAllRecords = new ArrayList<>();
        addMatchingDate(todayAllRecords, feedingList, today);
        addMatchingDate(todayAllRecords, sleepList, today);
        addMatchingDate(todayAllRecords, diaperList, today);
        addMatchingDate(todayAllRecords, bathList, today);

        if (todayAllRecords.isEmpty()) {
            TextView emptyTxt = new TextView(this);
            emptyTxt.setText("Bugün için kaydedilmiş veri bulunmuyor.");
            emptyTxt.setTextSize(15);
            emptyTxt.setTextColor(Color.GRAY);
            emptyTxt.setPadding(0, 16, 0, 16);
            summaryRecordsContainer.addView(emptyTxt);
        } else {
            for (CareRecordModel item : todayAllRecords) {
                summaryRecordsContainer.addView(createSummaryCardView(item));
            }
        }
    }

    private void addMatchingDate(List<CareRecordModel> dest, List<CareRecordModel> source, String today) {
        for (CareRecordModel m : source) {
            if (today.equals(m.getDate())) {
                dest.add(m);
            }
        }
    }

    private View createSummaryCardView(CareRecordModel item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 20, 24, 20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        card.setBackgroundColor(Color.parseColor("#F9F9F9"));

        TextView txtContent = new TextView(this);
        txtContent.setText(item.getSummary());
        txtContent.setTextSize(15);
        txtContent.setTextColor(Color.parseColor("#333333"));
        card.addView(txtContent);

        return card;
    }
}
