package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytodolist.R;
import com.example.mytodolist.IndividualTodoItemView;
import com.example.mytodolist.model.databaseManager;
import com.example.mytodolist.model.task;

import java.util.ArrayList;


public class todoListView extends AppCompatActivity {

    databaseManager manager;
    public static final String TODO_OBJ="com.example.mytodolist.OBJECT";
    private ArrayList<task> tasks;
    private int taskLength=0;
    private task currentSelectedItem=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_view);
        manager=new databaseManager(this);
        manager.open();
        tasks=new ArrayList<task>();
        getAllNewTask();
        checkItems();

        Button addButton=findViewById(R.id.addButton);
        Button deleteButton=findViewById(R.id.removeButton);
        final Button updateButton=findViewById(R.id.updateButton);
        RadioGroup todoListGroup=findViewById(R.id.todoListRadio);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent= new Intent(v.getContext(), IndividualTodoItemView.class);
                manager.close();
                addIntent.putExtra("UPDATE_SET", false);
                startActivity(addIntent);

            }
        });

        //This listener gathers which radio button is selected and uses that in order to complete
        //delete or update activities
        todoListGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int selectedId)
            {
                if( hasSelection(group)) {
                    RadioButton selectedButton=findViewById(selectedId);
                    currentSelectedItem=tasks.get(selectedButton.getId());
                    updateButton.setEnabled(true);
                    System.out.println(currentSelectedItem.getItem().toString());
                }
                else
                {
                    updateButton.setEnabled(false);
                }

            }
        }
        );

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.deleteTask(currentSelectedItem);
                checkItems();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent=new Intent(v.getContext(), IndividualTodoItemView.class);
                manager.close();
                updateIntent.putExtra("UPDATE_SET", true);
                updateIntent.putExtra(TODO_OBJ, currentSelectedItem);
                startActivity(updateIntent);
            }
        });
    }

    @Override
    protected  void onResume()
    {
          super.onResume();
          /*Still need to pull whole database better solution would grab only task not in list
           maybe we could keep a counter*/
          getAllNewTask();
          checkItems();
//        readInContents();

        // ensure we can navigate to the correct point in the back stack
        FragmentManager fm=this.getSupportFragmentManager();
        fm.popBackStack();
    }

    /**
     * Called by checkItems if a new task was added
     */
    private void updateList()
    {
        RadioGroup radioButtonsGroup=findViewById(R.id.todoListRadio);
        task currentTask;
        String currentTaskText;
        for(int i=0; i<tasks.size(); i++) {
            System.out.println("In the loop " + i);
            currentTask = tasks.get(i);
            if(currentTask.isDeleted()){
                radioButtonsGroup.removeView(radioButtonsGroup.getChildAt(i));
                //resetRadioButtonIds
                for(int j=i; j<tasks.size(); j++)
                {
                    //move the id back one to fill the spot for the missing
                    //radioButton
                    radioButtonsGroup.getChildAt(j).setId(j-1);
                }

            }
            else if (!(currentTask.isDisplayed())) {
                currentTaskText = currentTask.getItem();
                RadioButton newRadioButton = new RadioButton(this);
                newRadioButton.setId(i);
                newRadioButton.setText(currentTaskText);
                LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                checkParams.setMargins(8, 8, 8, 8);
                checkParams.gravity = Gravity.CENTER;
                radioButtonsGroup.addView(newRadioButton, i, checkParams);
                tasks.get(i).setDisplayed(true);
            }
        }
    }

    /**
     * Checks if new todoItems have been added to array
     * or if previous ones have been updated or deleted
     * updates UI if neccessary
     */
    public void checkItems()
    {
        if(taskLength == 0)
        {
            //check for first time items in database
            updateList();
            taskLength=tasks.size();
            System.out.println("Run updateList");
        }
        else
        {
            for (int i = 0; i < tasks.size(); i++) {
                task currentTask = tasks.get(i);
                if (currentTask.isDeleted()) {
                    updateList();
                    tasks.remove(i);
                    System.out.println("Update item deletion occured");
                }
                if (currentTask.isUpdated()) {
                    updateList();
                }
            }
            if (taskLength < tasks.size()) {
                updateList();
                taskLength = tasks.size();
            }
        }
    }


    public void getAllNewTask()
    {
        tasks=manager.getAllTask();
        System.out.println("Run getAllTasks");
    }


    public static boolean hasSelection(RadioGroup group)
    {
        return (group.getCheckedRadioButtonId() != -1);
    }

//    private void readInContents()
//    {
//        TextView itemList=findViewById(R.id.itemList);
//        Cursor tableCursor=manager.getAllRows();
//        String currentString="";
//        String buildString="";
//        if(tableCursor.getCount() > 0 && tableCursor.moveToFirst())
//        {
//
//             while(!tableCursor.isAfterLast())
//            {
//                int itemIndex=tableCursor.getColumnIndex("item");
//                System.out.println(itemIndex);
//                currentString= tableCursor.getString(itemIndex);
//                buildString=currentString+"\n"+buildString;
//                tableCursor.moveToNext();
//
//            }
//        }
//        itemList.setText(buildString);
//
//    }
}
