package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mytodolist.model.databaseManager;
import com.example.mytodolist.model.myTodo;

public class IndividualTodoItemView extends AppCompatActivity {

    databaseManager manager;
    public static final String ITEM_STRING="com.example.mytodolist.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_todo_item_view);
        manager=new databaseManager(this);
        manager.open();

        EditText todoEditBox=findViewById(R.id.todoEditBox);
        final Button addToList=findViewById(R.id.addToList);
        addToList.setEnabled(false);


        TextWatcher itemTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addToList.setEnabled(!getItemText().isEmpty());
                if(getItemText().isEmpty())
                {
                    System.out.println("disabled");
                }
                else
                {
                    System.out.println("enabled");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        todoEditBox.addTextChangedListener(itemTextWatcher);

        addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addToList=new Intent(v.getContext(), todoListView.class);
                if(addItemToDatabase(getItemText())){
                    String itemString=getItemText();
                    addToList.putExtra(ITEM_STRING, itemString);
                }
                else
                {
                    //add an error textbox
                }
                startActivity(addToList);
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
                success=(itemCursor.getCount() >1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return success;
    }

    public String getItemText()
    {
        EditText itemText=findViewById(R.id.todoEditBox);
        return itemText.getText().toString();
    }
}
