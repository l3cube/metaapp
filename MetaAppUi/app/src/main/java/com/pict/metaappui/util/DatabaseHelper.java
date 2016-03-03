package com.pict.metaappui.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pict.metaappui.modal.FileItem;
import com.pict.metaappui.modal.UserRequest;
import com.pict.metaappui.modal.UserResponses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tushar on 23/12/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MetaAppDb";

    // Table Names
    private static final String TABLE_USERREQUEST = "UserRequest";
    private static final String TABLE_USERRESPONSES = "UserResponses";
    private static final String TABLE_USERFILES = "UserFiles";


    //Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_REQUESTID = "RequestId";
    private static final String KEY_TOPIC = "Topic";

    //UserRequest column names
    private static final String KEY_INTENTDESC = "Intent_Desc";
    private static final String KEY_DEADLINE = "Deadline";
    private static final String KEY_PENDING = "Pending";

    //UserResponses column names
    private static final String KEY_SERVICEDESC = "Service_Desc";
    private static final String KEY_COST = "Cost";
    private static final String KEY_TIMETOCOMPLETE = "TTC";

    //UserFiles column names
    private static final String KEY_NAME = "Name";
    private static final String KEY_LOCATION = "Location";
    private static final String KEY_FILETYPE = "FileType";
    // Photos = 1
    // Videos = 2
    // Text = 3


    // Table Create Statements
    // No boolean datatype in sqlite so use integer with 0 as false and 1 as true
    private static final String CREATE_TABLE_USERREQUEST = "CREATE TABLE "
            + TABLE_USERREQUEST + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REQUESTID
            + " INTEGER UNIQUE," + KEY_TOPIC + " TEXT," + KEY_INTENTDESC + " TEXT,"
            + KEY_DEADLINE + " DATETIME," + KEY_PENDING + " INTEGER" + ")";

    private static final String CREATE_TABLE_USERRESPONSES = "CREATE TABLE "
            + TABLE_USERRESPONSES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REQUESTID
            + " INTEGER ," + KEY_TOPIC + " TEXT," + KEY_SERVICEDESC + " TEXT,"
            + KEY_TIMETOCOMPLETE + " DATETIME," + KEY_COST + " REAL" + ")";

    private static final String CREATE_TABLE_USERFILES = "CREATE TABLE "
            + TABLE_USERFILES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT ," + KEY_LOCATION + " TEXT UNIQUE," +KEY_FILETYPE + " INTEGER" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERREQUEST);
        db.execSQL(CREATE_TABLE_USERRESPONSES);
        db.execSQL(CREATE_TABLE_USERFILES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERREQUEST);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERREQUEST);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERFILES);
        onCreate(db);

    }

    public long createUserRequest(UserRequest obj){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_REQUESTID,obj.getRequestId());
        values.put(KEY_TOPIC,obj.getTopic());
        values.put(KEY_INTENTDESC, obj.getIntent_desc());
        values.put(KEY_DEADLINE, obj.getDeadline());
        if(obj.isPending())
            values.put(KEY_PENDING, 1);
        else
            values.put(KEY_PENDING, 0);
        long id=db.insert(TABLE_USERREQUEST,null,values);
        return id;
    }

    public UserRequest getUserRequest(int requestId){
        SQLiteDatabase db=this.getReadableDatabase();

        String select_query="SELECT * FROM "+TABLE_USERREQUEST+" WHERE "+KEY_REQUESTID+" = "+requestId;
        Log.i(LOG,select_query);

        Cursor c=db.rawQuery(select_query, null);
        if(c!=null)
            c.moveToFirst();

        UserRequest obj=new UserRequest();
        obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
        obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
        obj.setIntent_desc(c.getString(c.getColumnIndex(KEY_INTENTDESC)));
        obj.setDeadline(c.getString(c.getColumnIndex(KEY_DEADLINE)));
        if(c.getInt(c.getColumnIndex(KEY_PENDING))==1)
            obj.setPending(true);
        else
            obj.setPending(false);

        return obj;

    }

    public List<UserRequest> getAllUserRequests(){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERREQUEST;
        Log.i(LOG,select_query);

        List<UserRequest> list=new ArrayList<UserRequest>();
        Cursor c=db.rawQuery(select_query,null);

        if(c.moveToFirst()) {
            do {
                UserRequest obj = new UserRequest();
                obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
                obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
                obj.setIntent_desc(c.getString(c.getColumnIndex(KEY_INTENTDESC)));
                obj.setDeadline(c.getString(c.getColumnIndex(KEY_DEADLINE)));
                if (c.getInt(c.getColumnIndex(KEY_PENDING)) == 1)
                    obj.setPending(true);
                else
                    obj.setPending(false);
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public long createUserResponses(UserResponses obj){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_REQUESTID, obj.getRequestId());
        values.put(KEY_TOPIC,obj.getTopic());
        values.put(KEY_SERVICEDESC,obj.getService_desc());
        values.put(KEY_COST, obj.getCost());
        values.put(KEY_TIMETOCOMPLETE, obj.getTime_to_complete());
        long id=db.insert(TABLE_USERRESPONSES, null, values);
        return id;
    }

    public List<UserResponses> getUserResponses(int requestId){
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERRESPONSES+" WHERE "+KEY_REQUESTID+" = "+requestId;
        Log.i(LOG, select_query);

        List<UserResponses> list=new ArrayList<UserResponses>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                UserResponses obj = new UserResponses();
                obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                obj.setRequestId(c.getInt(c.getColumnIndex(KEY_REQUESTID)));
                obj.setTopic(c.getString(c.getColumnIndex(KEY_TOPIC)));
                obj.setService_desc(c.getString(c.getColumnIndex(KEY_SERVICEDESC)));
                obj.setTime_to_complete(c.getString(c.getColumnIndex(KEY_TIMETOCOMPLETE)));
                obj.setCost(c.getFloat(c.getColumnIndex(KEY_COST)));
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public int updatePendingRow(int requestId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_PENDING, 0);
        int noofrowsaffected=db.update(TABLE_USERREQUEST, values, KEY_REQUESTID + "=" + requestId, null);
        return noofrowsaffected;
    }

    public long createUserFiles(FileItem obj,String type){
        int fileType = getFileType(type);
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(KEY_NAME,obj.getName());
        values.put(KEY_LOCATION, obj.getLocation());
        values.put(KEY_FILETYPE, fileType);
        long id=db.insert(TABLE_USERFILES, null, values);
        return id;
    }

    public int getFileType(String type){
        if(type.equals("Photo"))
            return 1;
        else if(type.equals("Video"))
            return 2;
        else if(type.equals("Text Files"))
            return 3;
        else
            return -1;
    }

    public List<FileItem> getAllUserFiles(String type){
        int fileType = getFileType(type);
        SQLiteDatabase db=this.getReadableDatabase();
        String select_query="SELECT * FROM "+TABLE_USERFILES+" WHERE "+KEY_FILETYPE+" = "+fileType;
        Log.i(LOG, select_query);

        List<FileItem> list=new ArrayList<FileItem>();
        Cursor c=db.rawQuery(select_query, null);

        if(c.moveToFirst()) {
            do {
                FileItem obj = new FileItem();
                obj.setName(c.getString(c.getColumnIndex(KEY_NAME)));
                obj.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                obj.setIsChecked(false);
                list.add(obj);
            }while (c.moveToNext());
        }
        return list;
    }

    public boolean deleteFile(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_USERFILES,KEY_LOCATION + " = ?",new String[]{ location });
        if(count==1)
            return true;
        return false;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}

