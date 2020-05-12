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
    public static final int TODO_LIST_RESPONSE_CODE=1;
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
                removeTaskAndDecrementIDs(currentSelectedItem.getId());
                taskLength=taskLength-1;
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
    protected  void onRestart()
    {

          super.onRestart();
          /*Still need to pull whole database better solution would grab only task not in list
           maybe we could keep a counter*/
          System.out.println("Running on Restart");
          manager.open();
          getAllNewTask();

          
          task globalTask;
          for(int i=0; i<tasks.size(); i++)
          {
              globalTask=tasks.get(i);
              System.out.println("After restart ran "+globalTask.getItem()+
                      " was displayed: "+globalTask.isDisplayed());
          }

        // ensure we can navigate to the correct point in the back stack
        FragmentManager fm=this.getSupportFragmentManager();
        fm.popBackStack();
    }

    /**
     * Delete a task from the database
     * then decrement the ids so that the
     * ids in the database match what is
     * in the RadioGroup
     */
    public void removeTaskAndDecrementIDs(int idToRemove){
        RadioGroup radioButtonGroup=findViewById(R.id.todoListRadio);

        RadioButton currentRadioButton;
        task currentTask;
        int newId;
        currentTask=tasks.get(idToRemove);
        manager.deleteTask(currentTask);
        if(currentTask.isDeleted()){
            tasks.remove(currentTask.getId());
            //remove the current task from the interface
            currentRadioButton=(RadioButton) radioButtonGroup.getChildAt(idToRemove);
            System.out.println("Button Text "+currentRadioButton.getText());
            radioButtonGroup.removeView(currentRadioButton);
            //decrement the rest of the ids
            int radioButtons = radioButtonGroup.getChildCount();
            for (int i = idToRemove; i < radioButtons; i++) {
                System.out.println("Setting new id " + i);
                newId = i - 1;
                currentRadioButton = (RadioButton) radioButtonGroup.getChildAt(i);
                currentRadioButton.setId(newId);
                currentTask = tasks.get(i);
                manager.updateTaskID(currentTask, newId);
            }
        }
        else
        {
            System.err.println("Unable to delete task "+currentTask.getId());
        }

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
    private ArrayList<task> addToList(ArrayList<task> tasksToAdd)
    {
        RadioGroup currentRadioGroup=findViewById(R.id.todoListRadio);
        RadioButton newButton;
        task currentTask;
        ArrayList<task> displayedTasks=new ArrayList<task>();
        for(int i=0; i<tasksToAdd.size(); i++)
        {
            currentTask=tasksToAdd.get(i);
            newButton=new RadioButton(this);
            newButton.setId(currentTask.getId()-1);
            newButton.setText(currentTask.getItem());
            currentRadioGroup.addView(newButton);
            currentTask.setDisplayed(true);
            displayedTasks.add(currentTask);
        }
        return displayedTasks;

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
     * Sets the isDisplay on task list to match the is displayed
     * in task
     * @param taskList
     * @return
     */
    public ArrayList<task> setDisplayed(ArrayList<task> taskList)
    {
        task currentTask;
        ArrayList newTaskList=new ArrayList<task>();
        int currentTaskid;
        task globalCurrentTask;
        for(int i=0; i<taskList.size(); i++)
        {
            currentTask=taskList.get(i);
            currentTaskid=currentTask.getId();
            if(currentTaskid < tasks.size())
            {
                globalCurrentTask=tasks.get(currentTaskid);
                currentTask.setDisplayed(globalCurrentTask.isDisplayed());
            }
            //add the newly displayed task to the task list
            newTaskList.add(currentTask);
        }
        return newTaskList;
    }

    /**
     *updates the activities global tasks list and calls check Items
     * to update the user interface
     */
    public void getAllNewTask()
    {
        ArrayList<task> taskList=manager.getAllTask();
        System.out.println("Number of tasks retrieved "+taskList.size());
        ArrayList<task> updatedList=new ArrayList<task>();
        ArrayList<task> removeList=new ArrayList<task>();
        ArrayList<task> additionList=new ArrayList<task>();

        //check which tasks are displayed first
        taskList=setDisplayed(taskList);

        for(int h=0; h<taskList.size(); h++)
        {
            task aTask=taskList.get(h);
            System.out.println("task "+aTask.getId()+" is displayed: "
                    +aTask.isDisplayed());
        }

        task currentTask;
        int itemsToAdd=0;
        for(int i=0; i< taskList.size(); i++)
        {
            currentTask=taskList.get(i);
            /**
             * remove tasks from tasks list that have already
             * been displayed on the interface and have not
             * had any changes check currentTask against tasks
             * because taskList will not have displayed status
             */

            if(!currentTask.isDisplayed())
            {
                System.out.println("Current task with id "+currentTask.getId()+" " +
                        "was added to additionList");
                additionList.add(itemsToAdd, currentTask);
                itemsToAdd=itemsToAdd+1;
            }

            /**add task to updated list if the task
            *has recently been updated
             * due to a modification occurring the task can be removed
             * from the task list which would be for new tasks
             * that are added to the interface
             **/
            if(currentTask.isUpdated())
            {
                updatedList.add(currentTask);
                //remove the task currently stored in tasks at the task id
                //replace with the new task with the same id
                tasks.remove(currentTask.getId());
                tasks.add(currentTask.getId()-1, currentTask);
                taskLength=tasks.size();
            }
//            /** add task to remove list if the task
//             * has recently been deleted
//             * due to a modification occurring the task can be
//             * removed from the task list which would be for new tasks
//             * that are added to the interface
//             */
//            if(currentTask.isDeleted())
//            {
//                removeList.add(currentTask);
//                tasks.remove(currentTask);
//            }

        }

        //Perform all of the interface update tasks
        if (!additionList.isEmpty()) {
            System.out.println("Not EMPTY new items ");
            //ensure any new tasks are addded to the task list using their
            //id
            task newTask;
            for(int j=0; j<additionList.size(); j++)
            {
                newTask=taskList.get(j);
                System.out.println("Adding the task with id " +(newTask.getId()));
            }
            //perform addition to interface
            ArrayList<task> displayedTaskList=addToList(additionList);

            //add the displayed tasks to tasks so that they are not added again
            task currentDisplayedTask;
            for(int l=0; l< displayedTaskList.size(); l++)
            {
                currentDisplayedTask=displayedTaskList.get(l);
                tasks.add(currentDisplayedTask.getId(), currentDisplayedTask);
            }

        }

        if (!updatedList.isEmpty()) {
            updateList(updatedList);
            System.out.println("Not EMPTY update items");
        }

//        System.out.println("Is remove list empty: "+removeList.isEmpty());
//        if (!removeList.isEmpty()) {
//            System.out.println("NOT EMPTY deleted items");
//            task removeItem;
//            for(int k=0; k<removeList.size(); k++)
//            {
//                removeItem=removeList.get(k);
//                System.out.println("Removing: "+removeItem.getId()+": "+
//                        removeItem.getItem());
//            }
//            deleteFromList(removeList);
//
//        }

    }


    public static boolean hasSelection(RadioGroup group)
    {
        return (group.getCheckedRadioButtonId() != -1);
    }

}
