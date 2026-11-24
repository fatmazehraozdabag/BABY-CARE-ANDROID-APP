package com.example.babycare;

import org.json.JSONException;
import org.json.JSONObject;

public class CareRecordModel {
    private String id;
    private String type;
    private String date;
    private String time;
    private String note;
    private int amount;
    private int duration;
    private String diaperType;

    public CareRecordModel(String id, String type, String date, String time, String note, int amount, int duration, String diaperType) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.time = time;
        this.note = note;
        this.amount = amount;
        this.duration = duration;
        this.diaperType = diaperType;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getNote() {
        return note;
    }

    public int getAmount() {
        return amount;
    }

    public int getDuration() {
        return duration;
    }

    public String getDiaperType() {
        return diaperType;
    }

    public String getSummary() {
        switch (type) {
            case "feeding":
                return "🍼 Beslenme\nTarih: " + date + "\nSaat: " + time + "\nMiktar: " + amount + " ml\nSüre: " + duration + " dakika\nNot: " + (note.isEmpty() ? "-" : note);
            case "sleep":
                return "😴 Uyku\nTarih: " + date + "\nSaat: " + time + "\nSüre: " + duration + " dakika\nNot: " + (note.isEmpty() ? "-" : note);
            case "diaper":
                return "👶 Bez / Tuvalet\nTarih: " + date + "\nSaat: " + time + "\nTür: " + diaperType + "\nNot: " + (note.isEmpty() ? "-" : note);
            case "bath":
                return "🛀 Banyo\nTarih: " + date + "\nSaat: " + time + "\nSüre: " + duration + " dakika\nNot: " + (note.isEmpty() ? "-" : note);
            default:
                return "Tarih: " + date + "\nSaat: " + time + "\nNot: " + note;
        }
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("type", type);
            obj.put("date", date);
            obj.put("time", time);
            obj.put("note", note);
            obj.put("amount", amount);
            obj.put("duration", duration);
            obj.put("diaperType", diaperType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static CareRecordModel fromJsonObject(JSONObject obj) {
        if (obj == null) return null;
        try {
            String id = obj.optString("id", String.valueOf(System.currentTimeMillis()));
            String type = obj.optString("type", "");
            String date = obj.optString("date", "");
            String time = obj.optString("time", "");
            String note = obj.optString("note", "");
            int amount = obj.optInt("amount", 0);
            int duration = obj.optInt("duration", 0);
            String diaperType = obj.optString("diaperType", "");
            return new CareRecordModel(id, type, date, time, note, amount, duration, diaperType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
