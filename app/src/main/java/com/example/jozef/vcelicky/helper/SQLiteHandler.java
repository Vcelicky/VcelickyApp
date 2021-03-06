package com.example.jozef.vcelicky.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jozef.vcelicky.HiveBaseInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = "Database";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_MEASUREMENTS = "measurements";
    private static final String TABLE_DEVICES = "devices";

    // User Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EXPIRES = "expires";

    // Measurement table column names
    private static final String KEY_TIME = "time";
    private static final String KEY_TEMPIN = "tempIn";
    private static final String KEY_TEMPOUT = "tempOut";
    private static final String KEY_HUMIIN = "humiIn";
    private static final String KEY_HUMIOUT = "humiOut";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_POSITION = "position";
    private static final String KEY_BATTERY = "battery";
    private static final String KEY_DEVICEID = "deviceId";

    // Devices table column names
    private static final String KEY_DEVICENAME = "deviceName";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_USERID = "userId";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_ROLE + " TEXT,"
                + KEY_TOKEN + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_EXPIRES + " BIGINT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        String CREATE_MEASUREMENT_TABLE = "CREATE TABLE " + TABLE_MEASUREMENTS + " ("
                + KEY_TIME + " BIGINT PRIMARY KEY,"
                + KEY_TEMPIN + " INTEGER,"
                + KEY_TEMPOUT + " INTEGER,"
                + KEY_HUMIIN + " INTEGER,"
                + KEY_HUMIOUT + " INTEGER,"
                + KEY_WEIGHT + " INTEGER,"
                + KEY_POSITION + " BOOLEAN,"
                + KEY_BATTERY + " INTEGER,"
                + KEY_DEVICEID + " TEXT" + ")";
        db.execSQL(CREATE_MEASUREMENT_TABLE);

        String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_DEVICES + " ("
                + KEY_DEVICEID + " TEXT PRIMARY KEY,"
                + KEY_DEVICENAME + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_USERID + " INTEGER" + ")";
        db.execSQL(CREATE_DEVICE_TABLE);

        Log.i(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES);

        // Create tables again
        onCreate(db);
    }

    //    Users table handler methods
    /**
     * Storing user details in database
     * */
    public void addUser(boolean add, int userId, String name, String email, int role, String token, String phone, long expires) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userId); // User ID
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role); // User Role
        values.put(KEY_TOKEN, token); // Token
        values.put(KEY_PHONE, phone);
        values.put(KEY_EXPIRES, expires);

        long id;
        if(add) {
            // Inserting Row
            id = db.insert(TABLE_USER, null, values);
            Log.i(TAG, "New user inserted into sqlite: " + id);
        }
        else{
            db.update(TABLE_USER, values, KEY_ID + "='" + userId + "'", null);
            Log.i(TAG, "User updated in sqlite: " + userId);
        }
        db.close(); // Closing database connection
        Log.i(TAG, values.toString());
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(String email) {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER
                + " WHERE " + KEY_EMAIL + "='" + email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("id", cursor.getString(0));
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("role_id", String.valueOf(cursor.getInt(3)));
            user.put("token", cursor.getString(4));
            user.put("phone", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        Log.i(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public boolean isUser(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USER
                + " WHERE " + KEY_ID + "='" + userId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.delete(TABLE_MEASUREMENTS, null, null);
        db.close();
        Log.i(TAG, "Deleted all users info from sqlite");
    }

    public boolean isExpired() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_EXPIRES
                + " FROM " + TABLE_USER;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            Log.i(TAG, String.valueOf(new Date().getTime() / 1000));
            if(cursor.getLong(0) >= new Date().getTime() / 1000){
                cursor.close();
                db.close();
                return false;
            }
        }
        cursor.close();
        db.close();
        return true;
    }

//    Measurements table handler methods

    public void addMeasurement(long time, int tempIn, int tempOut, int humiIn, int humiOut, int weight, boolean position, int battery, String deviceId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIME, time);
        values.put(KEY_TEMPIN, tempIn);
        values.put(KEY_TEMPOUT, tempOut);
        values.put(KEY_HUMIIN, humiIn);
        values.put(KEY_HUMIOUT, humiOut);
        values.put(KEY_WEIGHT, weight);
        values.put(KEY_POSITION, position);
        values.put(KEY_BATTERY, battery);
        values.put(KEY_DEVICEID, deviceId);

        // Inserting Row
        long id = db.insert(TABLE_MEASUREMENTS, null, values);
        db.close(); // Closing database connection

        Log.i(TAG, "New measurement inserted into sqlite: " + id);
        Log.i(TAG, values.toString());
    }

    public ArrayList<HiveBaseInfo> getActualMeasurement(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HiveBaseInfo> devices = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DEVICES
                + " WHERE " + KEY_USERID + "='" + userId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            HiveBaseInfo device;
            for(int i = 0; i < cursor.getCount(); i++){
                device = new HiveBaseInfo();
                device.setHiveId(cursor.getString(0));
                device.setHiveName(cursor.getString(1));
                device.setHiveLocation(cursor.getString(2));
                devices.add(device);
                cursor.moveToNext();
                Log.i(TAG, "Fetching device from SQLite: " + device.getHiveId());
            }
        }
        Log.i(TAG, "Number of devices: " + devices.size());
        for(int i = 0; i < devices.size(); i++){
            selectQuery = "SELECT * FROM " + TABLE_MEASUREMENTS
                    + " WHERE " + KEY_DEVICEID + "='" + devices.get(i).getHiveId()
                    + "' ORDER BY " + KEY_TIME + " DESC LIMIT 1";
            cursor = db.rawQuery(selectQuery, null);
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                devices.get(i).setTime(cursor.getLong(0));
                devices.get(i).setInsideTemperature(cursor.getFloat(1));
                devices.get(i).setOutsideTemperature(cursor.getFloat(2));
                devices.get(i).setInsideHumidity(cursor.getFloat(3));
                devices.get(i).setOutsideHumidity(cursor.getFloat(4));
                devices.get(i).setWeight(cursor.getFloat(5));
                devices.get(i).setAccelerometer(Boolean.parseBoolean(cursor.getString(6)));
                devices.get(i).setBattery(cursor.getFloat(7));
                Log.i(TAG, "Fetching actual measurement from Sqlite: " + devices.get(i).toString());
            }
        }
        cursor.close();
        db.close();
        // return actual measurement for all devices
        return devices;
    }

    // Get most recent time stamp of measurement based on device ID
    public long getMostRecentTimeStamp(String id){
        long recent = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_TIME
                + " FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_DEVICEID + "='" + id
                + "' ORDER BY " + KEY_TIME + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            recent = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return recent;
    }

    public ArrayList<HiveBaseInfo> getAllMeasurements(String id){
        ArrayList<HiveBaseInfo> hiveList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_DEVICEID + "='" + id
                + "' ORDER BY " + KEY_TIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            HiveBaseInfo record;
            for(int i = 0; i < cursor.getCount(); i++){
                record = new HiveBaseInfo();
                record.setTime(cursor.getLong(0));
                record.setInsideTemperature(cursor.getInt(1));
                record.setOutsideTemperature(cursor.getInt(2));
                record.setInsideHumidity(cursor.getInt(3));
                record.setOutsideHumidity(cursor.getInt(4));
                record.setWeight(cursor.getInt(5));
                record.setAccelerometer(cursor.getString(6).equals("1"));
                record.setBattery(cursor.getInt(7));
                record.setHiveId(cursor.getString(8));
                hiveList.add(record);
                cursor.moveToNext();
                Log.i(TAG, "Fetching measurement from SQLite: " + record.getTime());
            }
        }
        cursor.close();
        db.close();
        return hiveList;
    }

//    Devices table handler methods
    public void addDevice(boolean add, String hiveId, String hiveName, String hiveLocation, int userId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DEVICEID, hiveId);
        values.put(KEY_DEVICENAME, hiveName);
        values.put(KEY_LOCATION, hiveLocation);
        values.put(KEY_USERID, userId);

        long id;
        if(add) {
            // Inserting Row
            id = db.insert(TABLE_DEVICES, null, values);
            Log.i(TAG, "New device inserted into sqlite: " + id);
        }
        else{
            // Updating row
            db.update(TABLE_DEVICES, values, KEY_DEVICEID + "='" + hiveId + "'", null);
            Log.i(TAG, "Updated device in sqlite: " + hiveId);
        }
        db.close(); // Closing database connection
        Log.i(TAG, values.toString());
    }

    public boolean isDevice(String hiveId){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DEVICES
                + " WHERE " + KEY_DEVICEID + "='" + hiveId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public HiveBaseInfo getDeviceInfo(String hiveId){
        HiveBaseInfo device = new HiveBaseInfo();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DEVICES
                + " WHERE " + KEY_DEVICEID + "='" + hiveId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            device.setHiveId(cursor.getString(0));
            device.setHiveName(cursor.getString(1));
            device.setHiveLocation(cursor.getString(2));
            Log.i(TAG, "Fetching device from SQLite: " + device.getHiveId());
        }
        cursor.close();
        db.close();
        return device;
    }

    public int getUserDevicesCount(int userId){
        int count;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_DEVICEID
                + " FROM " + TABLE_DEVICES
                + " WHERE " + KEY_USERID + "='" + userId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public ArrayList<HiveBaseInfo> getUserDevices(int userId){
        ArrayList<HiveBaseInfo> devices = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_DEVICES
                + " WHERE " + KEY_USERID + "='" + userId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            HiveBaseInfo device;
            for(int i = 0; i < cursor.getCount(); i++){
                device = new HiveBaseInfo();
                device.setHiveId(cursor.getString(0));
                device.setHiveName(cursor.getString(1));
                device.setHiveLocation(cursor.getString(2));
                devices.add(device);
            }
        }
        cursor.close();
        db.close();
        return devices;
    }
}