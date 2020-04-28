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
    public static final String TODO_COLUM_TWO_TYPE="TEXT";

    public task(String item, int id)
    {
        this.item=item;
        this.id=id;
    }

    public int getId() {
        return this.id;
    }

    public String  getItem()
    {
        return this.item;
    }

    public void setItem(String newItem)
    {
        this.item=newItem;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted(boolean status)
    {
        this.deleted=status;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public void setUpdated(boolean status)
    {
        this.updated=status;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed)
    {
        this.displayed=displayed;
    }
}
