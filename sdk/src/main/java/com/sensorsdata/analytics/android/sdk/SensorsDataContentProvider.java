package com.sensorsdata.analytics.android.sdk;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SensorsDataContentProvider extends ContentProvider {
    private final static int APP_START = 1;
    private final static int APP_END_STATE = 2;
    private final static int APP_PAUSED_TIME = 3;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private ContentResolver mContentResolver;

    @Override
    public boolean onCreate() {
        if (getContext() != null) {
            String packName = getContext().getPackageName();
            uriMatcher.addURI(packName + ".SensorsDataContentProvider", SensorsDataTable.APP_STARTED.getName(), APP_START);
            uriMatcher.addURI(packName + ".SensorsDataContentProvider", SensorsDataTable.APP_END_STATE.getName(), APP_END_STATE);
            uriMatcher.addURI(packName + ".SensorsDataContentProvider", SensorsDataTable.APP_PAUSED_TIME.getName(), APP_PAUSED_TIME);
            sharedPreferences = getContext().getSharedPreferences("com.sensorsdata.analytics.android.sdk.SensorsDataAPI", Context.MODE_PRIVATE);
            mEditor = sharedPreferences.edit();
            mEditor.apply();
            mContentResolver = getContext().getContentResolver();
        }
        return false;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (contentValues == null) {
            return uri;
        }
        int code = uriMatcher.match(uri);
        switch (code) {
            case APP_START:
                boolean appStart = contentValues.getAsBoolean(SensorsDatabaseHelper.APP_STARTED);
                mEditor.putBoolean(SensorsDatabaseHelper.APP_STARTED, appStart);
                mContentResolver.notifyChange(uri, null);
                break;
            case APP_END_STATE:
                boolean appEnd = contentValues.getAsBoolean(SensorsDatabaseHelper.APP_END_STATE);
                mEditor.putBoolean(SensorsDatabaseHelper.APP_END_STATE, appEnd);
                break;
            case APP_PAUSED_TIME:
                long pausedTime = contentValues.getAsLong(SensorsDatabaseHelper.APP_PAUSED_TIME);
                mEditor.putLong(SensorsDatabaseHelper.APP_PAUSED_TIME, pausedTime);
                break;
        }
        mEditor.commit();
        return uri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        int code = uriMatcher.match(uri);
        MatrixCursor matrixCursor = null;
        switch (code) {
            case APP_START:
                int appStart = sharedPreferences.getBoolean(SensorsDatabaseHelper.APP_STARTED, true) ? 1 : 0;
                matrixCursor = new MatrixCursor(new String[]{SensorsDatabaseHelper.APP_STARTED});
                matrixCursor.addRow(new Object[]{appStart});
                break;
            case APP_END_STATE:
                int appEnd = sharedPreferences.getBoolean(SensorsDatabaseHelper.APP_END_STATE, true) ? 1 : 0;
                matrixCursor = new MatrixCursor(new String[]{SensorsDatabaseHelper.APP_END_STATE});
                matrixCursor.addRow(new Object[]{appEnd});
                break;
            case APP_PAUSED_TIME:
                long pausedTime = sharedPreferences.getLong(SensorsDatabaseHelper.APP_PAUSED_TIME, 0);
                matrixCursor = new MatrixCursor(new String[]{SensorsDatabaseHelper.APP_PAUSED_TIME});
                matrixCursor.addRow(new Object[]{pausedTime});
                break;
        }
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
