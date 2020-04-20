package phone.vishnu.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;


public class Database extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "table_name";
    private static final String TASK_COLUMN = "task";
    private static final String DATE_COLUMN = "date";
    private static final String TARGET_COLUMN = "target";
    private static final String DB_NAME = "Task.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_COLUMN + " TEXT, " + DATE_COLUMN + " TEXT, " + TARGET_COLUMN + " TEXT " + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(String task, String date, String target) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TASK_COLUMN, task);

        values.put(DATE_COLUMN, date);

        values.put(TARGET_COLUMN, target);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<String> get() {

        ArrayList<String> todoArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{TASK_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TASK_COLUMN);//int
            todoArray.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        Collections.reverse(todoArray);
        return todoArray;
    }

    public void delete(String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, TASK_COLUMN + " = ?", new String[]{time});
        db.close();
    }

    public void update(String oldTask, String newTask) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(TASK_COLUMN, newTask);

        db.update(TABLE_NAME, cv, TASK_COLUMN + " = ?", new String[]{oldTask});
    }

    public String getDate(String todo) {

        String date = "";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        cursor = db.query(TABLE_NAME,new String[]{TASK_COLUMN, DATE_COLUMN},TASK_COLUMN + "=?",new String[]{todo},null,null,null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            date = cursor.getString(1);
        }

        cursor.close();

        db.close();

        return date;

    }

    public String getTarget(String todo) {

        String target = "";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        cursor = db.query(TABLE_NAME,new String[]{TASK_COLUMN, TARGET_COLUMN},TARGET_COLUMN + "=?",new String[]{todo},null,null,null);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()) {
            target = cursor.getString(1);
        }
        cursor.close();

        db.close();

        return target;

    }

    public ArrayList<String> getTargetList() {

        ArrayList<String> targetArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{TARGET_COLUMN}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(TARGET_COLUMN);//int
            targetArray.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        Collections.reverse(targetArray);
        return targetArray;
    }

}