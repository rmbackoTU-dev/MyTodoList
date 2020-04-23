package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytodolist.R;
import com.example.mytodolist.IndividualTodoItemView;
import com.example.mytodolist.model.databaseManager;

import static com.example.mytodolist.DeleteActivity.ERROR_TEXT;

public class todoListView extends AppCompatActivity {

    databaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_view);
        manager=new databaseManager(this);
        manager.open();

        readInContents();

        Button addButton=findViewById(R.id.addButton);
        Button deleteButton=findViewById(R.id.removeButton);



        Intent externalIntent=getIntent();
        if(externalIntent.getStringExtra(ERROR_TEXT) != null)
        {
            Toast errorToast=new Toast(this);
            errorToast.setText(externalIntent.getStringExtra(ERROR_TEXT));
            errorToast.setDuration(Toast.LENGTH_LONG);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent= new Intent(v.getContext(), IndividualTodoItemView.class);
                manager.close();
                startActivity(addIntent);

            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent deleteIntent= new Intent(v.getContext(), DeleteActivity.class);
                manager.close();
                startActivity(deleteIntent);
            }
        });
    }

    @Override
    protected  void onRestart()
    {
        super.onRestart();
        readInContents();

        // ensure we can navigate to the correct point in the back stack
        FragmentManager fm=this.getSupportFragmentManager();
        fm.popBackStack();
    }

    private void readInContents()
    {
        TextView itemList=findViewById(R.id.itemList);
        Cursor tableCursor=manager.getAllRows();
        String currentString="";
        String buildString="";
        if(tableCursor.getCount() > 0 && tableCursor.moveToFirst())
        {

             while(!tableCursor.isAfterLast())
            {
                int itemIndex=tableCursor.getColumnIndex("item");
                System.out.println(itemIndex);
                currentString= tableCursor.getString(itemIndex);
                buildString=currentString+"\n"+buildString;
                tableCursor.moveToNext();

            }
        }
        itemList.setText(buildString);

    }
}
