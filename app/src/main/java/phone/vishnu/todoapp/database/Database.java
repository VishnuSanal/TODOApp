package phone.vishnu.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;


public class Database extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "TODO_Table";
    private static final String TODO_COLUMN = "todo";
    private static final String ADDED_DATE_COLUMN = "date_added";
    private static final String END_DATE_COLUMN = "date_end";
    private static final String DB_NAME = "TODO.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + TODO_COLUMN + " TEXT, " + ADDED_DATE_COLUMN + " TEXT, " + END_DATE_COLUMN + " INTEGER " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(String todo, String date_added, int date_end) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TODO_COLUMN, todo);
        values.put(ADDED_DATE_COLUMN, date_added);
        values.put(END_DATE_COLUMN, date_end);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<String> get() {
        ArrayList<String> todoArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{TODO_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TODO_COLUMN);//int
            todoArray.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        Collections.reverse(todoArray);
        return todoArray;
    }

    public void delete(String todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TODO_COLUMN + " = ?", new String[]{todo});
        db.close();
    }

    public void update(String oldTask, String newTask,int date_end) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TODO_COLUMN, newTask);
        cv.put(END_DATE_COLUMN, date_end);
        db.update(TABLE_NAME, cv, TODO_COLUMN + " = ?", new String[]{oldTask});
    }

    public String getDate(String todo) {
        String date = "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ADDED_DATE_COLUMN}, TODO_COLUMN + " = ? ", new String[]{todo}, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            date = cursor.getString(cursor.getColumnIndex(ADDED_DATE_COLUMN));
        }
        cursor.close();
        db.close();
        return date;
    }

    public int getTarget(String todo) {
        int target = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{END_DATE_COLUMN}, TODO_COLUMN+ " = ? ", new String[]{todo}, null, null, null, null);

        if (cursor.moveToFirst()) {
            target = cursor.getInt(cursor.getColumnIndex(END_DATE_COLUMN));
        }

        cursor.close();
        db.close();
        return target;
    }

}