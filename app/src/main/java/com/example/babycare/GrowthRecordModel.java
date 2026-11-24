package com.example.babycare;

import org.json.JSONException;
import org.json.JSONObject;

public class GrowthRecordModel {
    private String id;
    private String date;
    private String weight;
    private String height;
    private String head;
    private String note;

    public GrowthRecordModel(String id, String date, String weight, String height, String head, String note) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.head = head;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getHead() {
        return head;
    }

    public String getNote() {
        return note;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("📏 Gelişim Ölçümü\n");
        sb.append("Tarih: ").append(date).append("\n");
        if (!weight.isEmpty()) {
            sb.append("⏲️ Kilo: ").append(weight).append(" kg\n");
        }
        if (!height.isEmpty()) {
            sb.append("📏 Boy: ").append(height).append(" cm\n");
        }
        if (!head.isEmpty()) {
            sb.append("👶 Baş Çevresi: ").append(head).append(" cm\n");
        }
        if (!note.isEmpty()) {
            sb.append("Not: ").append(note);
        }
        return sb.toString().trim();
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", id);
            obj.put("date", date);
            obj.put("weight", weight);
            obj.put("height", height);
            obj.put("head", head);
            obj.put("note", note);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static GrowthRecordModel fromJsonObject(JSONObject obj) {
        if (obj == null) return null;
        try {
            String id = obj.optString("id", String.valueOf(System.currentTimeMillis()));
            String date = obj.optString("date", "");
            String weight = obj.optString("weight", "");
            String height = obj.optString("height", "");
            String head = obj.optString("head", "");
            String note = obj.optString("note", "");
            return new GrowthRecordModel(id, date, weight, height, head, note);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
