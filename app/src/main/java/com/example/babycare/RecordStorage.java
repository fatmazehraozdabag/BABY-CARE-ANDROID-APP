package com.example.babycare;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordStorage {

    private static final String PREF_NAME = "BabyCarePrefs";

    public static List<CareRecordModel> getRecordModels(Context context, String key) {
        List<CareRecordModel> list = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(key, "[]");
        try {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                CareRecordModel model = CareRecordModel.fromJsonObject(obj);
                if (model != null) {
                    list.add(model);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void saveOrUpdateModel(Context context, String key, CareRecordModel model) {
        List<CareRecordModel> currentList = getRecordModels(context, key);
        boolean updated = false;
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getId().equals(model.getId())) {
                currentList.set(i, model);
                updated = true;
                break;
            }
        }
        if (!updated) {
            currentList.add(0, model);
        }
        saveAllModels(context, key, currentList);
    }

    public static void deleteModel(Context context, String key, String id) {
        List<CareRecordModel> currentList = getRecordModels(context, key);
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getId().equals(id)) {
                currentList.remove(i);
                break;
            }
        }
        saveAllModels(context, key, currentList);
    }

    public static void clearRecords(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(key).apply();
    }

    private static void saveAllModels(Context context, String key, List<CareRecordModel> list) {
        JSONArray array = new JSONArray();
        for (CareRecordModel m : list) {
            array.put(m.toJsonObject());
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(key, array.toString()).apply();
    }
}
