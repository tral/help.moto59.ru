package ru.moto59.help;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

  class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
      // конструктор суперкласса
      super(context, "helpmoto59ruDB", null, 1);
    }

    // Вставляет расход (трату) денег
    public long insertExpense(long cat_id, int val, String date_, String comment) {
    	
    	SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
    	
		Calendar c=Calendar.getInstance(); 
		int year=c.get(Calendar.YEAR); 
		int month=c.get(Calendar.MONTH)+1; // с нуля месяцы  
		int day=c.get(Calendar.DAY_OF_MONTH);
		int min=c.get(Calendar.MINUTE);
		int sec=c.get(Calendar.SECOND);
		int hour=c.get(Calendar.HOUR_OF_DAY);
		
        //cv.put("_id", 1);
        cv.put("date_", date_);
        cv.put("enter_time", String.format("%02d", day)+"."+String.format("%02d", month)+"."+year+" "+hour+":"+min+":"+sec);
        cv.put("comment", comment);
        cv.put("cat_id", cat_id);
        cv.put("val", val);
        long rowID = db.insert("data_", null, cv);
		
		return rowID;
    			
    }
    
      
    // Удаляет все категории
    public long deleteCategories() {
    	SQLiteDatabase db = this.getWritableDatabase();
		long delCount  = db.delete("category", null, null);
		return delCount;
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

      // номер телефона для отправки SMS
      db.execSQL("create table phone ("
              + "_id integer primary key," 
              + "phone text"
              + ");");
      
      // Договорились, что телефон хранится в таблице с _id=1
      cv.put("_id", 1);
      cv.put("phone", "9955555555"); // без "+7" !!!
      db.insert("phone", null, cv);  
      
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
  }