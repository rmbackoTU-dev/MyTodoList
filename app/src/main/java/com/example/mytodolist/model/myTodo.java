package com.example.mytodolist.model;

public class myTodo {

    String item;
    int id;


    public static final String TODO_TABLE_NAME="todo";
    public static final String TODO_COLUMN_ONE="id";
    public static final String TODO_COLUMN_ONE_TYPE="INTEGER PRIMARY KEY";
    public static final String TODO_COLUMN_TWO="item";
    public static final String TODO_COLUM_TWO_TYPE="TEXT";

    public myTodo(String item, int id)
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
}
