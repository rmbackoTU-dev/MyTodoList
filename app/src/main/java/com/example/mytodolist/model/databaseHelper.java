package com.example.mytodolist.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final int  DB_VERSION=1;
    private static String databaseName="todoDatabase";
    private static final String TODO_TABLE_CREATE="CREATE TABLE " + task.TODO_TABLE_NAME+ "("+
            task.TODO_COLUMN_ONE+" "+ task.TODO_COLUMN_ONE_TYPE+", "+ task.TODO_COLUMN_TWO+" "+
            task.TODO_COLUMN_TWO_TYPE +");";

    public static final String TODO_TABLE_DELETE="DROP TABLE IF EXISTS "+ task.TODO_TABLE_NAME;


    public databaseHelper(Context c)
    {
        super(c, databaseName, null, DB_VERSION );
        this.context=c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        db.execSQL(TODO_TABLE_DELETE);

        onCreate(db);
    }
}
