package com.gp.saveform2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // zmienne STAŁE dostepne w całej klasie
    public static final String DB_NAME = "AmTest.db";
    public static final String TBL_NAME = "person";
    public static final String COL_ID = "id";
    public static final String COL_RULES = "rules";
    public static final String COL_GDPR = "gdpr";
    public static final String COL_BORNDATE = "borDate";
    public static final String COL_SURNAME = "surname";
    public static final String COL_NAME = "name";
    public static final String COL_CITY = "city";

    public DBHelper(@Nullable Context ctx) {
        // wywołaj konstruktor klasy: SQLiteOpenHelper
        super(ctx, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL_NAME + "("+COL_ID+" integer primary key,"
                + COL_RULES + " integer,"
                + COL_GDPR + " integer,"
                + COL_BORNDATE + " date,"
                + COL_SURNAME + " text,"
                + COL_NAME + " text,"
                + COL_CITY + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
        onCreate(db);
    }

    public boolean insertEntry(String surname, String name, String city, String bornDate, Integer rules, Integer gdpr){
		// wybranie trybu Zapisu do BD z 2-ch możliwych (getReadableDatabase)
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(COL_RULES, rules);
        data.put(COL_GDPR, gdpr);
        data.put(COL_BORNDATE, bornDate);
        data.put(COL_SURNAME, surname);
        data.put(COL_NAME, name);
        data.put(COL_CITY, city);
        // wgranie zebranych danych
        long newId = db.insert(TBL_NAME,null,data);
        System.out.println(newId);
        // newId = -1 błąd zapisu oraz 0+ zapis pomyślny
        // mechanizm weryfikacji wprowadzenia danych
        return (newId > -1) ? true : false;
    }
    // public Cursor getData(int id){}
    public int rowsCount(){
        return 0;
    }
    // public boolean updateContent(){}

    public ArrayList<String> getAllEntrys() {
		ArrayList<String> lst = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("SELECT * FROM "+TBL_NAME,null);
		res.moveToFirst();
		while(res.isAfterLast()==false)
		{	// iteruj tak długo, aż po ostatnim rekordzie nie ma nic
			lst.add(res.getString(res.getColumnIndex(COL_NAME)));
			res.moveToNext(); // przejdź do następnego wiersza danych
		}
		return lst;
    }
}























