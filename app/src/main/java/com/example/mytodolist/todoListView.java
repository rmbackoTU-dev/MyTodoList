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
        System.out.println("Running on create");
        getAllNewTask();

        Button addButton=findViewById(R.id.addButton);
        final Button deleteButton=findViewById(R.id.removeButton);
        final Button updateButton=findViewById(R.id.updateButton);
        RadioGroup todoListGroup=findViewById(R.id.todoListRadio);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent= new Intent(v.getContext(), IndividualTodoItemView.class);
                manager.close();
                addIntent.putExtra("UPDATE_SET ", false);
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
                    deleteButton.setEnabled(true);
                    System.out.println(currentSelectedItem.getItem().toString());
                }
                else
                {
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }

            }
        }
        );

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.deleteTask(currentSelectedItem);
                getAllNewTask();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent=new Intent(v.getContext(), IndividualTodoItemView.class);
                manager.close();
                updateIntent.putExtra("UPDATE_SET ", true);
                updateIntent.putExtra(TODO_OBJ, currentSelectedItem);
                startActivity(updateIntent);
            }
        });
    }

    @Override
    protected  void onResume()
    {

          super.onResume();
//          clearUIRadioList();
          /*Still need to pull whole database better solution would grab only task not in list
           maybe we could keep a counter*/
          getAllNewTask();
//        readInContents();

        // ensure we can navigate to the correct point in the back stack
        FragmentManager fm=this.getSupportFragmentManager();
        fm.popBackStack();
    }

    /**
     * Called by getAllNewTasks to update items that have been modified
     * in the interface
     */
    private void updateList(ArrayList<task> updateList)
    {
        RadioGroup radioButtonsGroup=findViewById(R.id.todoListRadio);
        task currentTask;
        RadioButton updateButton;
        for(int i=0; i<updateList.size(); i++)
        {
            currentTask=updateList.get(i);
            updateButton=(RadioButton) radioButtonsGroup.getChildAt(currentTask.getId());
            //update text to new text
            updateButton.setText(currentTask.getItem());
        }
    }

    /**
     * Called by getAllNewTasks to add items to the interface
     * initially
     * @param tasksToAdd
     */
    private void addToList(ArrayList<task> tasksToAdd)
    {
        RadioGroup currentRadioGroup=findViewById(R.id.todoListRadio);
        RadioButton newButton;
        task currentTask;
        for(int i=0; i<tasksToAdd.size(); i++)
        {
            currentTask=tasksToAdd.get(i);
            newButton=new RadioButton(this);
            newButton.setId(currentTask.getId()-1);
            newButton.setText(currentTask.getItem());
            currentRadioGroup.addView(newButton);
        }

    }

    /**
     * Called by getAllNewTasks to remove items which have been deleted
     * from the interface
     * @param tasksToDelete
     */
    private void deleteFromList(ArrayList<task> tasksToDelete)
    {
        RadioGroup currentRadioGroup=findViewById(R.id.todoListRadio);
        task currentTask;
        RadioButton removeButton;
        for(int i=0; i<tasksToDelete.size(); i++)
        {
            currentTask=tasksToDelete.get(i);
            removeButton=(RadioButton) currentRadioGroup.getChildAt(
                    currentTask.getId());
            currentRadioGroup.removeView(removeButton);
        }
    }

    /**
     *updates the activities global tasks list and calls check Items
     * to update the user interface
     */
    public void getAllNewTask()
    {
        ArrayList<task> taskList=manager.getAllTask();
        ArrayList<task> updatedList=new ArrayList<task>();
        ArrayList<task> removeList=new ArrayList<task>();


        task currentTask;
        for(int i=0; i< taskList.size(); i++)
        {
            currentTask=taskList.get(i);
            System.out.println("Current Task is : "+currentTask.getItem());
            /**add task to updated list if the task
            *has recently been updated
             * due to a modification occurring the task can be removed
             * from the task list which would be for new tasks
             * that are added to the interface
             **/
            System.out.println("Task id: "+currentTask.getId()+" Current Item: "+currentTask.getItem()+
                    " is updated: "+currentTask.isUpdated());
            if(currentTask.isUpdated())
            {
                updatedList.add(currentTask);
                //remove the task currently stored in tasks at the task id
                //replace with the new task with the same id
                tasks.remove(currentTask.getId());
                tasks.add(currentTask.getId()-1, currentTask);
                taskList.remove(currentTask);
            }
            /** add task to remove list if the task
             * has recently been deleted
             * due to a modification occurring the task can be
             * removed from the task list which would be for new tasks
             * that are added to the interface
             */
            System.out.println("Task id "+currentTask.getId()+" is deleted: " +
                    currentTask.isDeleted());

            if(currentTask.isDeleted());
            {
                System.out.println("Entered add to remove list");
                removeList.add(currentTask);
                tasks.remove(currentTask);
                taskList.remove(currentTask);
            }

            System.out.println("Remove list is empty : "+removeList.isEmpty());
            /**
             * remove tasks from tasks list that have already
             * been displayed on the interface and have not
             * had any changes
             */
            if(currentTask.isDisplayed())
            {
                taskList.remove(currentTask);
            }
        }

        System.out.println("Remove list is empty 2: "+removeList.isEmpty());

        if(taskList.isEmpty() && updatedList.isEmpty() && removeList.isEmpty())
        {
            System.out.println("No changes have occurred");
        }
        else {
            //Perform all of the interface update tasks
            if (!taskList.isEmpty()) {
                System.out.println("Not EMPTY new items ");
                //ensure any new tasks are addded to the task list using their
                //id
                task newTask;
                for(int j=0; j<taskList.size(); j++)
                {
                    newTask=taskList.get(j);
                    System.out.println("Adding the task with id "
                    +(newTask.getId()-1));
                    tasks.add(newTask.getId()-1, newTask);
                }
                //perform addition to interface
                addToList(taskList);
            }

            if (!updatedList.isEmpty()) {
                 updateList(updatedList);
                 System.out.println("Not EMPTY update items");
            }

            if (!removeList.isEmpty()) {
                System.out.println("NOT EMPTY deleted items");
                task removeItem;
                for(int k=0; k<removeList.size(); k++)
                {
                    removeItem=removeList.get(k);
                    System.out.println("Removing: "+removeItem.getId()+": "+
                            removeItem.getItem());
                }
                deleteFromList(removeList);

            }
        }
    }


    public static boolean hasSelection(RadioGroup group)
    {
        return (group.getCheckedRadioButtonId() != -1);
    }

//    public void  clearUIRadioList()
//    {
//        RadioGroup currentRadioGroup=findViewById(R.id.todoListRadio);
//        if(currentRadioGroup.getChildCount() != 0)
//        {
//            int radioGroupLength=currentRadioGroup.getChildCount();
//            RadioButton currentButton;
//            for(int i=0; i< radioGroupLength; i++ )
//            {
//                currentButton=(RadioButton) currentRadioGroup.getChildAt(i);
//                currentRadioGroup.removeView(currentButton);
//            }
//        }
//    }
}
