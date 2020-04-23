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

public class DeleteActivity extends AppCompatActivity {

    databaseManager manager;
    public static final String ERROR_TEXT="com.example.mytodolist.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        manager=new databaseManager(this);
        manager.open();

        EditText todoEditBox=findViewById(R.id.deleteEditBox);
        final Button deleteFromList=findViewById(R.id.deleteItemButton);
        deleteFromList.setEnabled(false);


        TextWatcher itemTextWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                deleteFromList.setEnabled(!getItemText().isEmpty());
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

        deleteFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent removeFromList=new Intent(v.getContext(), todoListView.class);
                int id=getItemId(getItemText());
                System.out.println(id);
                manager.deleteById(id);
                if(itemStillFound(id))
                {
                    removeFromList.putExtra(ERROR_TEXT, "Item not successfully Removed");
                }
                startActivity(removeFromList);
            }
        });
    }

    public int getItemId(String text)
    {

        Integer result=0;
        try {


            Cursor cursor = manager.getItemByText(text);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return  result;
    }

    public String getItemText()
    {
        EditText deleteField=findViewById(R.id.deleteEditBox);
        return deleteField.getText().toString();
    }

    public boolean itemStillFound(int id)
    {
        Cursor cursor=manager.getItemById(id);
        return (cursor.getCount() > 0);
    }
}
