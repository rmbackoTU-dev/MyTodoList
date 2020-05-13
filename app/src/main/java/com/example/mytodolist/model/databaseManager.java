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

    /**
     * Opens a connection to the database
     * Must be called in order to call other manager functions
     * @return
     */
    public databaseManager open()
    {
        dbHelper=new databaseHelper(this.context);
        writableDb=dbHelper.getWritableDatabase();
        readableDb=dbHelper.getReadableDatabase();
        return this;
    }

    /**
     * Inserts a task in to the todoList table of the database
     * @param value
     * @throws SQLException
     */
    public void insertTask(String value)
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
        ContentValues val=new ContentValues();
        val.put(task.TODO_COLUMN_ONE,  id);
        val.put(task.TODO_COLUMN_TWO, value);
        writableDb.insertOrThrow(task.TODO_TABLE_NAME, null, val);
    }

    /**
     * DEAD CODE UNCOMMENT WHEN USED
     * Gets the database row with the id provided in param id
     * and return it via a cursor object
     * @param id
     * @return returns a cursor containing the row of
     * the item with the id of parameter id
     */
//    public Cursor getItemById(int id)
//    {
//        String query="SELECT "+ task.TODO_COLUMN_ONE +
//                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
//                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_ONE+"='"+
//                id+"';";
//       Cursor newCursor=readableDb.rawQuery(query, null);
//        return newCursor;
//    }

    /**
     * Retrieves a todoList row by the item text
     * @param text
     * @return Cursor of all returned todoList rows with
     * item field set to text
     */
    public Cursor getItemByText(String text)
    {

        String query="SELECT "+ task.TODO_COLUMN_ONE +
                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_TWO+"='"+
                text+"';";
        Cursor newCursor=readableDb.rawQuery(query, null);
        return newCursor;

    }

    /**
     * DeadCode uncomment when used
     * Used to get a cursor with all of the databaseRows in the
     * todoList table
     * @return a cursor pointing to all of the retrieved rows
     */
//    public Cursor getAllTodoRows()
//    {
//
//        String rawQuery="SELECT * FROM "+ task.TODO_TABLE_NAME+";";
//        Cursor newCursor=readableDb.rawQuery(rawQuery, null);
//        String[] columns=newCursor.getColumnNames();
//        for(int i=0; i< columns.length; i++)
//        {
//            System.out.println(columns[i]);
//        }
//        return newCursor;
//    }


    /**
     * DEAD CODE Uncomment when used
     * Used to update a item with a given item id
     * @param id
     * @param value
     * @throws IllegalArgumentException
     */
//    public void updateValueatID(int id, String value)
//            throws IllegalArgumentException
//    {
//        //New Value Column
//        ContentValues updatedValue=new ContentValues();
//        updatedValue.put(task.TODO_TABLE_NAME, value);
//
//        //find which row to update based on the id
//        String integerString=new Integer(id).toString();
//        String countQuery="SELECT * FROM "+ task.TODO_TABLE_NAME+
//                " WHERE "+ task.TODO_COLUMN_ONE+" ="+integerString+";";
//        Cursor cursor=readableDb.rawQuery(countQuery, null );
//        int count= cursor.getCount();
//
//        if(count > 1)
//        {
//            throw new IllegalArgumentException(" Can not safely update item");
//        }
//        else
//        {
//            String updateQuery="UPDATE "+ task.TODO_TABLE_NAME+" SET "+
//                    task.TODO_COLUMN_TWO+"='"+value+"'"+"WHERE "+
//                    task.TODO_COLUMN_ONE+"="+integerString+";";
//            readableDb.execSQL(updateQuery);
//        }
//    }


    /**
     * Currently dead code
     * Used to get a task from the database by ID
     * Uncomment when needed
     * @param taskId
     * @return
     */
//    public task getTaskbyID(int taskId)
//    {
//        String query="SELECT "+ task.TODO_COLUMN_ONE +
//                " , "+ task.TODO_COLUMN_TWO+ " FROM "+
//                task.TODO_TABLE_NAME+ " WHERE "+ task.TODO_COLUMN_ONE+"='"+
//                taskId+"';";
//        Cursor newCursor=readableDb.rawQuery(query, null);
//        task newTodo=null;
//        int newTaskId;
//        String newTaskItem;
//        if(newCursor.moveToFirst() && (newCursor.getCount() > 0))
//        {
//            newTaskId=newCursor.getInt(
//                    newCursor.getColumnIndex("id"));
//            newTaskItem=newCursor.getString(
//                    newCursor.getColumnIndex("item"));
//            newTodo=new task(newTaskItem, newTaskId);
//        }
//        return newTodo;
//    }

    /**
     * Used to get all of the rows from the task table
     * can be used when the task list in an activity needs to be updated
     * @return ArrayList\<task\> tasks which is the list
     * of all tasks in the database
     */
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
 //               System.out.println("id: "+taskId);
                taskItem=newCursor.getString(
                        newCursor.getColumnIndex("item")
                );
 //               System.out.println("item: "+taskItem);
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
     *Used to delete a task from the database and update the task object to
     * isDeleted
     * @param taskToRemove
     * @sideeffect setsDeletedOnTask
     */
    public task deleteTask(task taskToRemove)
    {

        try {
            String selection = task.TODO_COLUMN_ONE + " LIKE ?";
            //Specify the arguments in the place holder order.
            int currentId = taskToRemove.getId();
            String[] selectionArgs = {new Integer(currentId).toString()};
            //Issue SQL statement.
            int deletedRows = writableDb.delete(task.TODO_TABLE_NAME, selection, selectionArgs);
            System.out.println("Deleteing task with id "+taskToRemove.getId());
            taskToRemove.setDeleted(true);
            return  taskToRemove;
        }catch(SQLException e)
        {
            e.printStackTrace();
            taskToRemove.setDeleted(false);
            return taskToRemove;
        }
    }

    /**
     * Used to update a task with a new item value
    * @param  taskToUpdate
     * @sideeffect taskToUpdate values are updated
     * @sideeffect sets updated on task
     */
    public void updateTask(task taskToUpdate, String newItem)
    {
        try {
            int queryTaskID;
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

    /**
     * DEAD CODE UNCOMMENT WHEN NEEDED
     * Used to update a task with a new ID in the database
     * can be used in cases where limiting id fragmentation is
     * important
     */
//    public void updateTaskID(task taskToUpdate, int newID)
//    {
//        try {
//            int queryTaskID;
//            String queryTaskItem;
//            int currentId = taskToUpdate.getId();
//            System.out.println("Updating id "+currentId+" to "+newID);
//            String currentItem = taskToUpdate.getItem();
//            ContentValues updateValues = new ContentValues();
//            updateValues.put(task.TODO_COLUMN_ONE, currentId);
//            updateValues.put(task.TODO_COLUMN_TWO, currentItem);
//            String updateSelectionQuery = "SELECT " + task.TODO_COLUMN_ONE + ", " +
//                    task.TODO_COLUMN_TWO + " FROM " +
//                    task.TODO_TABLE_NAME + " WHERE " +
//                    task.TODO_COLUMN_ONE + "='" +
//                    currentId + "';";
//            Cursor updateCursor = readableDb.rawQuery(updateSelectionQuery, null);
//
//            if (updateCursor.moveToFirst() && updateCursor.getCount() > 0) {
//
//                queryTaskItem=updateCursor.getString(
//                        updateCursor.getColumnIndex("item"));
//                String updateQuery="UPDATE "+ task.TODO_TABLE_NAME+ " SET "+
//                        task.TODO_COLUMN_ONE+"='"+newID+"' WHERE "+
//                        task.TODO_COLUMN_TWO+"='"+queryTaskItem+"';";
//                writableDb.execSQL(updateQuery);
//                taskToUpdate.setId(newID);
//                //do not set update because content was not modified
//            }
//        }catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }

    /**
     * Closes the connection to the database
     * Necessary to prevent data corruption in the database
     */
    public void close()
    {
        readableDb.close();
        writableDb.close();
    }
}
