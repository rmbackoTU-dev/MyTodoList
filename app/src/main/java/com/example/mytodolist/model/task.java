package com.example.mytodolist.model;

import java.io.Serializable;

public class task implements Serializable {

    private String item;
    private int id;
    private boolean deleted=false;
    private boolean updated=false;
    private boolean displayed=false;


    public static final String TODO_TABLE_NAME="todo";
    public static final String TODO_COLUMN_ONE="id";
    public static final String TODO_COLUMN_ONE_TYPE="INTEGER PRIMARY KEY";
    public static final String TODO_COLUMN_TWO="item";
    public static final String TODO_COLUMN_TWO_TYPE ="TEXT";

    /**
     * Used to initialize a new task from the database
     * Edit the task item instead of the item in the database
     * store the task item when the tasks needs to be saved
     * @param item
     * @param id
     */
    public task(String item, int id)
    {
        this.item=item;
        this.id=id;
    }

    /**
     * Used to set the id of a task
     * @param setID
     */
    public void setId(int setID)
    {
        this.id=setID;
    }

    /**
     * @return the id number for a task
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return the item text for a task
     */
    public String  getItem()
    {
        return this.item;
    }

    /**
     * used to Set the item text of a task
     * @param newItem
     */
    public void setItem(String newItem)
    {
        this.item=newItem;
    }

    /**
     * Used to determine if a item has been removed
     * from the database and should be removed
     * from any of its current usages
     * @return the deleted status
     */
    public boolean isDeleted()
    {
        return deleted;
    }

    /**
     * used to mark an item as deleted after it has been removed
     * from the database
     * @param  status
     */
    public void setDeleted(boolean status)
    {
        this.deleted=status;
    }

    /**
     * Used to determine if there is a newer version
     * of a task in the database
     * @return the updated status
     */
    public boolean isUpdated()
    {
        return updated;
    }

    /**
     * used to mark an item as updated after its has been changed
     * in the database
     * @param  status
     */
    public void setUpdated(boolean status)
    {
        this.updated=status;
    }

    /**
     * Used to determine if a task is already displayed in the todoListView
     * @return the displayed status
     */
    public boolean isDisplayed() {
        return displayed;
    }

    /**
     * used to mark an item as displayed after its has been added to
     * a radioGroup on the todoListView Activity
     * @param displayed
     */
    public void setDisplayed(boolean displayed)
    {
        this.displayed=displayed;
    }
}
