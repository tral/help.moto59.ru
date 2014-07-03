package ru.moto59.help;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

  class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
      // ����������� �����������
      super(context, "helpmoto59ruDB", null, 1);
    }

    public String getPhone() {
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor c = db.query("phone", null, "_id=1", null, null, null, null);
    	
    	if (c.moveToFirst()) {
            int idx = c.getColumnIndex("phone");
            String phone = c.getString(idx);
            return phone;
		}
    	
    	return "";
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
      
      ContentValues cv = new ContentValues();

      // ����� �������� ��� �������� SMS
      db.execSQL("create table phone ("
              + "_id integer primary key," 
              + "phone text"
              + ");");
      
      // ������������, ��� ������� �������� � ������� � _id=1
      cv.put("_id", 1);
      cv.put("phone", "9955555555"); // ��� "+7" !!!
      db.insert("phone", null, cv);  
      
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }