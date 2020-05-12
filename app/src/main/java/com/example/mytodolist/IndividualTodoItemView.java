package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mytodolist.model.databaseManager;
import com.example.mytodolist.model.task;

public class IndividualTodoItemView extends AppCompatActivity {


    databaseManager manager;
    public static final int ADD_UPDATE_VIEW_REPONSE_CODE=2;
    private boolean updateSet=false;
    private task updateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_todo_item_view);
        manager=new databaseManager(this);
        manager.open();

        EditText todoEditBox=findViewById(R.id.todoEditBox);
        final Button addToList=findViewById(R.id.addToList);
        addToList.setEnabled(false);

        Intent updateIntent=getIntent();
        updateSet=updateIntent.getExtras().getBoolean("UPDATE_SET");
        System.out.println("UPDATE IS SET TO "+updateSet);
        if(updateSet)
        {
            updateTask=(task) updateIntent.getSerializableExtra(todoListView.TODO_OBJ);
            setUpdateHint(updateTask.getItem().toString());
        }

        TextWatcher itemTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addToList.setEnabled(!getItemText().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        todoEditBox.addTextChangedListener(itemTextWatcher);

        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToList=new Intent(v.getContext(), todoListView.class);
                returnToList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if(updateSet)
                {
                    updateItemInDatabase(updateTask, getItemText());

                }
                else
                {
                    if (addItemToDatabase(getItemText())) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                }
                startActivity(returnToList);
            }
        });

    }

    public boolean addItemToDatabase(String itemText)
    {
        boolean success=false;
        try {
            manager.insert(itemText);
            Cursor itemCursor=manager.getItemByText(itemText);
            if(itemCursor.moveToFirst())
            {
                 int itemIndex=itemCursor.getColumnIndex("item");
                String textFound=itemCursor.getString(itemIndex);
                System.out.println(textFound);
                int itemNum=itemCursor.getCount();
                System.out.println("The items returned by the add to database query is "+
                        itemNum);
                success=(itemNum>0);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return success;
    }

    public boolean updateItemInDatabase(task updateTask, String updateText)
    {
        boolean success=false;
        try
        {
            manager.updateTask(updateTask, updateText);
            success=true;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return  success;
    }

    public String getItemText()
    {
        EditText itemText=findViewById(R.id.todoEditBox);
        return itemText.getText().toString();
    }

    public void setUpdateHint( CharSequence hint)
    {
        EditText itemText=findViewById(R.id.todoEditBox);
        itemText.setHint(hint);
    }
}
