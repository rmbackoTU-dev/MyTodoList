package com.example.mytodolist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.StringRes;

public class databaseManager {

    private Context context;
    private SQLiteDatabase writableDb;
    private SQLiteDatabase readableDb;
    private databaseHelper dbHelper;
    private static int id=0;

    public databaseManager(Context c)
    {
        this.context=c;
    }

    public databaseManager open()
    {
        dbHelper=new databaseHelper(this.context);
        writableDb=dbHelper.getWritableDatabase();
        readableDb=dbHelper.getReadableDatabase();
        return this;
    }

    public void insert(String value)
            throws SQLException
    {
        String rawQuery="SELECT " +myTodo.TODO_COLUMN_ONE+ " FROM "+
                myTodo.TODO_TABLE_NAME+";";
        Cursor idCursor=readableDb.rawQuery(rawQuery, null);
        if(idCursor.getCount() >0 && idCursor.moveToFirst())
        {
            idCursor.moveToLast();
            id=idCursor.getInt(idCursor.getColumnIndex("id"))+1;
        }
        System.out.println("Count:  "+idCursor.getCount());
        ContentValues val=new ContentValues();
        val.put(myTodo.TODO_COLUMN_ONE,  id);
        val.put(myTodo.TODO_COLUMN_TWO, value);
        writableDb.insertOrThrow(myTodo.TODO_TABLE_NAME, null, val);
    }

    public Cursor getItemById(int id)
    {
        String query="SELECT "+myTodo.TODO_COLUMN_ONE +
                " , "+ myTodo.TODO_COLUMN_TWO+ " FROM "+
                myTodo.TODO_TABLE_NAME+ " WHERE "+myTodo.TODO_COLUMN_ONE+"='"+
                id+"';";
        //String idString=new Integer(id).toString();
        //String[] projector={
              // myTodo.TODO_COLUMN_ONE,
              // myTodo.TODO_COLUMN_TWO
       // };
       // String selector= myTodo.TODO_COLUMN_ONE + " = ?";
        //String[] selectionArgs={ idString } ;
       // Cursor newCursor=readableDb.query(myTodo.TODO_TABLE_NAME, projector, selector, selectionArgs,
        //        null, null, "ORDER BY "+myTodo.TODO_COLUMN_ONE +" ASC", null);
        Cursor newCursor=readableDb.rawQuery(query, null);
        return newCursor;
    }

    public Cursor getItemByText(String text)
    {

        String query="SELECT "+myTodo.TODO_COLUMN_ONE +
                " , "+ myTodo.TODO_COLUMN_TWO+ " FROM "+
                myTodo.TODO_TABLE_NAME+ " WHERE "+myTodo.TODO_COLUMN_TWO+"='"+
                text+"';";
        //String selector=myTodo.TODO_COLUMN_TWO + "='"+text+"'";
        //String[] projector={
          //      myTodo.TODO_COLUMN_ONE,
            //    myTodo.TODO_COLUMN_TWO
        //};

        //String[] selectorArgs={ text};
        //Cursor newCursor=readableDb.query(myTodo.TODO_TABLE_NAME, projector, selector, selectorArgs,
          //      null, null, myTodo.TODO_COLUMN_ONE+" ASC");
        Cursor newCursor=readableDb.rawQuery(query, null);
        return newCursor;

    }

    public Cursor getAllRows()
    {

        String rawQuery="SELECT * FROM "+myTodo.TODO_TABLE_NAME+";";
        Cursor newCursor=readableDb.rawQuery(rawQuery, null);
        String[] columns=newCursor.getColumnNames();
        for(int i=0; i< columns.length; i++)
        {
            System.out.println(columns[i]);
        }
        return newCursor;
    }

    public void deleteById(int id)
    {
        String selection = myTodo.TODO_COLUMN_ONE+" LIKE ?";
        //Specify the arguments in the place holder order.
        String[] selectionArgs={ new Integer(id).toString()};
        //Issue SQL statement.
        int deletedRows=writableDb.delete(myTodo.TODO_TABLE_NAME, selection, selectionArgs);
    }


    public void updateValueatID(int id, String value)
            throws IllegalArgumentException
    {
        //New Value Column
        ContentValues updatedValue=new ContentValues();
        updatedValue.put(myTodo.TODO_TABLE_NAME, value);

        //find which row to update based on the id
        String integerString=new Integer(id).toString();
        String countQuery="SELECT * FROM "+myTodo.TODO_TABLE_NAME+
                " WHERE "+myTodo.TODO_COLUMN_ONE+" ="+integerString+";";
        Cursor cursor=readableDb.rawQuery(countQuery, null );
        int count= cursor.getCount();

        if(count > 1)
        {
            throw new IllegalArgumentException(" Can not safely update item");
        }
        else
        {
            String updateQuery="UPDATE "+myTodo.TODO_TABLE_NAME+" SET "+
                    myTodo.TODO_COLUMN_TWO+"='"+value+"'"+"WHERE "+
                    myTodo.TODO_COLUMN_ONE+"="+integerString+";";
            readableDb.execSQL(updateQuery);
        }
    }

    public void close()
    {
        readableDb.close();
        writableDb.close();
    }
}
