package com.example.mytodolist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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
        String rawQuery="SELECT " + task.TODO_COLUMN_ONE+ " FROM "+
                task.TODO_TABLE_NAME+";";
        Cursor idCursor=readableDb.rawQuery(rawQuery, null);
        if(idCursor.getCount() >0 && idCursor.moveToFirst())
        {
            idCursor.moveToLast();
            id=idCursor.getInt(idCursor.getColumnIndex("id"))+1;
        }
        System.out.println("Count:  "+idCursor.getCount());
        ContentValues val=new ContentValues();
        val.put(task.TODO_COLUMN_ONE,  id);
        val.put(task.TODO_COLUMN_TWO, value);
        writableDb.insertOrThrow(task.TODO_TABLE_NAME, null, val);
    }

    public Cursor getItemById(int id)
    {
        String query="SELECT "+ task.TODO_COLUMN_ONE +
                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_ONE+"='"+
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

        String query="SELECT "+ task.TODO_COLUMN_ONE +
                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_TWO+"='"+
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

        String rawQuery="SELECT * FROM "+ task.TODO_TABLE_NAME+";";
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
        String selection = task.TODO_COLUMN_ONE+" LIKE ?";
        //Specify the arguments in the place holder order.
        String[] selectionArgs={ new Integer(id).toString()};
        //Issue SQL statement.
        int deletedRows=writableDb.delete(task.TODO_TABLE_NAME, selection, selectionArgs);
    }


    public void updateValueatID(int id, String value)
            throws IllegalArgumentException
    {
        //New Value Column
        ContentValues updatedValue=new ContentValues();
        updatedValue.put(task.TODO_TABLE_NAME, value);

        //find which row to update based on the id
        String integerString=new Integer(id).toString();
        String countQuery="SELECT * FROM "+ task.TODO_TABLE_NAME+
                " WHERE "+ task.TODO_COLUMN_ONE+" ="+integerString+";";
        Cursor cursor=readableDb.rawQuery(countQuery, null );
        int count= cursor.getCount();

        if(count > 1)
        {
            throw new IllegalArgumentException(" Can not safely update item");
        }
        else
        {
            String updateQuery="UPDATE "+ task.TODO_TABLE_NAME+" SET "+
                    task.TODO_COLUMN_TWO+"='"+value+"'"+"WHERE "+
                    task.TODO_COLUMN_ONE+"="+integerString+";";
            readableDb.execSQL(updateQuery);
        }
    }


    public task getTaskbyID(int taskId)
    {
        String query="SELECT "+ task.TODO_COLUMN_ONE +
                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_ONE+"='"+
                taskId+"';";
        Cursor newCursor=readableDb.rawQuery(query, null);
        task newTodo=null;
        int newTaskId;
        String newTaskItem;
        if(newCursor.moveToFirst() && (newCursor.getCount() > 0))
        {
            newTaskId=newCursor.getInt(
                    newCursor.getColumnIndex("id"));
            newTaskItem=newCursor.getString(
                    newCursor.getColumnIndex("item"));
            newTodo=new task(newTaskItem, newTaskId);
        }
        return newTodo;
    }

    public ArrayList<task> getAllTask()
    {

        int taskId;
        String taskItem;
        String rawQuery="SELECT * FROM "+ task.TODO_TABLE_NAME+";";
        Cursor newCursor=readableDb.rawQuery(rawQuery, null);
        if(newCursor.moveToFirst() && (newCursor.getCount() > 0))
        {
            ArrayList listOfTasks=new ArrayList();
            while(!newCursor.isAfterLast())
            {
                taskId=newCursor.getInt(
                        newCursor.getColumnIndex("id"));
                System.out.println("id: "+taskId);
                taskItem=newCursor.getString(
                        newCursor.getColumnIndex("item")
                );
                System.out.println("item: "+taskItem);
                task newTodo=new task(taskItem, taskId);
                listOfTasks.add(newTodo);
                newCursor.moveToNext();
            }
            return  listOfTasks;
        }
        else
        {
            ArrayList emptyList=new ArrayList<task>();
            System.err.println("Returning empty array");
            return emptyList;
        }
    }

    /**
     *
     * @param taskToRemove
     * @sideeffect setsDeletedOnTask
     */
    public void deleteTask(task taskToRemove)
    {

        try {
            String selection = task.TODO_COLUMN_ONE + " LIKE ?";
            //Specify the arguments in the place holder order.
            int currentId = taskToRemove.getId();
            String[] selectionArgs = {new Integer(currentId).toString()};
            //Issue SQL statement.
            int deletedRows = writableDb.delete(task.TODO_TABLE_NAME, selection, selectionArgs);
            taskToRemove.setDeleted(true);
        }catch(SQLException e)
        {
            e.printStackTrace();
            taskToRemove.setDeleted(false);
        }
    }

    /**
    * @param  taskToUpdate
     * @sideeffect taskToUpdate values are updated
     * @sideeffect sets updated on task
     */
    public void updateTask(task taskToUpdate, String newItem)
    {
        try {
            int queryTaskID;
            String queryTaskItem;
            int currentId = taskToUpdate.getId();
            String currentItem = taskToUpdate.getItem();
            ContentValues updateValues = new ContentValues();
            updateValues.put(task.TODO_COLUMN_ONE, currentId);
            updateValues.put(task.TODO_COLUMN_TWO, currentItem);

            String updateSelectionQuery = "SELECT " + task.TODO_COLUMN_ONE + ", " +
                    task.TODO_COLUMN_TWO + " FROM " +
                    task.TODO_TABLE_NAME + " WHERE " +
                    task.TODO_COLUMN_ONE + "='" +
                    currentId + "';";
            Cursor updateCursor = readableDb.rawQuery(updateSelectionQuery, null);

            if (updateCursor.moveToFirst() && updateCursor.getCount() > 0) {

                queryTaskID=updateCursor.getInt(
                        updateCursor.getColumnIndex("id"));
                String updateQuery="UPDATE "+ task.TODO_TABLE_NAME+ " SET "+
                        task.TODO_COLUMN_TWO+"='"+newItem+"' WHERE "+
                        task.TODO_COLUMN_ONE+"='"+queryTaskID+"';";
                writableDb.execSQL(updateQuery);
                taskToUpdate.setItem(newItem);
                taskToUpdate.setUpdated(true);
            }
            else
            {
                taskToUpdate.setUpdated(false);
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
            taskToUpdate.setUpdated(false);
        }
    }

    public void close()
    {
        readableDb.close();
        writableDb.close();
    }
}
