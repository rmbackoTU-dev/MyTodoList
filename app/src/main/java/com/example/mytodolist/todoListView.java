package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.mytodolist.model.databaseManager;
import com.example.mytodolist.model.task;
import java.util.ArrayList;


public class todoListView extends AppCompatActivity {

    databaseManager manager;
    public static final String TODO_OBJ="com.example.mytodolist.OBJECT";
    private ArrayList<task> tasks;
    private int taskLength=0;
    private task currentSelectedItem=null;
    private Bundle currentExtras;


    /**
     * onCreate used when an activity is newly created by an intent, and is not
     * resumed from a previously created activity on the backstack
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_view);
        manager=new databaseManager(this);
        manager.open();
        tasks=new ArrayList<task>();
        getAllNewTask();

        Button addButton=findViewById(R.id.addButton);
        final Button deleteButton=findViewById(R.id.removeButton);
        final Button updateButton=findViewById(R.id.updateButton);
        RadioGroup todoListGroup=findViewById(R.id.todoListRadio);

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);


        //create an add button function call.
         addClickListener addClick=new addClickListener();
       addButton.setOnClickListener(addClick);

        /**
         * Below is a different way of setting an add click listener
         * it is more efficient but harder to read.
         */
        //add button function call
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent addIntent= new Intent(v.getContext(), IndividualTodoItemView.class);
//                manager.close();
//                addIntent.putExtra("UPDATE_SET", false);
//                startActivity(addIntent);
//
//            }
//        });

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
                    System.out.println(currentSelectedItem.getId());
                }
                else
                {
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }

            }
        }
        );

        //Delete button function call
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTaskFromDatabaseAndUI(currentSelectedItem);
                taskLength=taskLength-1;
            }
        });

        //Update button function call
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

    /**
     * Used to ensure when a new intent is used to launch the
     * activity the intent is updated with the current extras values
     * @param newIntent
     */
    @Override
    protected  void onNewIntent(Intent newIntent)
    {
        super.onNewIntent(newIntent);
        //getting the extras from the new Intent
        currentExtras=newIntent.getExtras();
        if(currentExtras == null)
        {
            System.out.println("Checking new Intent");
            currentExtras=new Bundle();
            currentExtras.putBoolean("UPDATE_RECORD_SET", false);
            currentExtras.putInt(IndividualTodoItemView.TASK_UPDATE_INDEX, -1);
        }
    }

    /**
     * onRestart called whenever an intent uses FLAG_ACTIVITY_CLEAR_TOP or
     * FLAG_ACTIVITY_SINGLE_TOP flag to update information on the screen
     * based on the action that occurred.
     */
    @Override
    protected  void onRestart()
    {

          super.onRestart();
          boolean updateRecordSet=false;
          int indexToSet=-1;
          /*Still need to pull whole database better solution would grab only task not in list
           maybe we could keep a counter*/
          updateRecordSet=currentExtras.getBoolean("UPDATE_RECORD_SET");
          manager.open();
          task updateTask;
          /**After we recieve a intent back from the IndividualTodoItemView that a record was updated
          *get the index of the updated record and replace it in our tasks with a task listed with a
          *isListed **/
          if(updateRecordSet)
          {
              indexToSet=currentExtras.getInt(IndividualTodoItemView.TASK_UPDATE_INDEX);
              updateTask=tasks.get(indexToSet);
              updateTask.setUpdated(true);
              tasks.remove(indexToSet);
              tasks.add(indexToSet, updateTask);
          }
          getAllNewTask();

        // ensure we can navigate to the correct point in the back stack
        FragmentManager fm=this.getSupportFragmentManager();
        fm.popBackStack();
    }

    /**
     * Delete a task from the database
     * then the UI
     */
    public void removeTaskFromDatabaseAndUI(task taskToRemove){
        //deleting the task should set it as isDeleted
        task removedTask=manager.deleteTask(taskToRemove);
        int removedID=removedTask.getId();

        //replace the new task with isDeleted in place of the
        //old task without is deleted
        tasks.remove(removedTask);
        tasks.add(removedID, removedTask);
        removeDeletedTaskFromUI();
    }

    /**
     * iterates through tasks and removes any task marked as isDeleted
     * from the radioGroup
     */
    public void removeDeletedTaskFromUI()
    {
        task currentTask;
        int isDeletedIndex=0;
        for(int m=0; m< tasks.size(); m++)
        {
            currentTask=tasks.get(m);
            if(currentTask.isDeleted())
            {
                isDeletedIndex=m;
            }
        }
        /**Assume m is the first found deleted task
        *all task after M should be removed from the radio group
        *on a second traversal of task only task that have not been deleted
        *should be added back to the radio group
         * if nothing is deleted
         * everything is added back to UI safely**/
        RadioGroup currentRadioGroup=findViewById(R.id.todoListRadio);
        int radioButtonCount=currentRadioGroup.getChildCount();
        RadioButton currentRadioButton;
        int isAddedIndex=isDeletedIndex;
        int oldRadioButtonCount;
        while(isDeletedIndex < radioButtonCount)
        {
            oldRadioButtonCount=radioButtonCount;
            currentRadioButton=(RadioButton) currentRadioGroup.getChildAt(isDeletedIndex);
            currentRadioGroup.removeView(currentRadioButton);
            /**radio Button count will decrease as items are removed meaning items
             * in index 2 will take the space of index 1 creating looping
             *without incrementing. To end the loop however isDeletedIndex
             * must be updated When radioCount=isDeletedIndex+1
             * more traditional would be a while loop with a boolean sentinal,
             * but this works for now.
            **/
            if(isDeletedIndex+1 <  oldRadioButtonCount)
            {
                radioButtonCount=currentRadioGroup.getChildCount();
            }
            else
            {
                //end loop by incrementing isDeletedIndex
                isDeletedIndex=isDeletedIndex+1;
            }
        }

        int newTaskId=isAddedIndex;
        RadioButton newRadioButton;
        ArrayList<task> displayedTasks=new ArrayList<task>();
        for(int n=isAddedIndex; n< tasks.size(); n++)
        {
            currentTask=tasks.get(n);
            if(!currentTask.isDeleted())
            {
                newRadioButton = new RadioButton(this);
                newRadioButton.setId(isAddedIndex);
                newRadioButton.setText(currentTask.getItem());
                currentRadioGroup.addView(newRadioButton);
                currentTask.setDisplayed(true);
                currentTask.setId(isAddedIndex);
                displayedTasks.add(currentTask);
                isAddedIndex=isAddedIndex+1;
            }
        }

        //run third loop to properly add back in all displayed tasks in to activity task list
        task currentDisplayedTask;
        int startOfNewlyAddedTasks=newTaskId;
        for(int o=0; o<displayedTasks.size(); o++)
        {
            currentDisplayedTask = displayedTasks.get(o);
            tasks.remove(newTaskId);
            tasks.add(newTaskId, currentDisplayedTask);
            newTaskId=newTaskId+1;
        }
        /**any tasks left over on tasks at positions greater than newTaskID+displayedTasks.size()
        should be able to safely be removed from tasks**/
        for(int p=startOfNewlyAddedTasks; p<tasks.size(); p++)
        {
            tasks.remove(p);
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
            newButton.setId(currentTask.getId());
            newButton.setText(currentTask.getItem());
            currentRadioGroup.addView(newButton);
            currentTask.setDisplayed(true);
            displayedTasks.add(currentTask);
        }
        return displayedTasks;

    }


    /**
     * Sets the isDisplay on task list to match the is displayed
     * in task
     * @param taskList
     * @return
     */
    public ArrayList<task> setDisplayedAndUpdated(ArrayList<task> taskList)
    {
        task currentTask;
        ArrayList<task> newTaskList=new ArrayList<task>();
        task globalCurrentTask;
        for(int i=0; i<taskList.size(); i++)
        {
            currentTask=taskList.get(i);
            //if the index is lower than the size of the task list
            //we assume that the task is old and has a record
            //otherwise the data pulled from the database is correct.
            if(i<tasks.size()){
                globalCurrentTask=tasks.get(i);
                currentTask.setDisplayed(globalCurrentTask.isDisplayed());
                currentTask.setUpdated(globalCurrentTask.isUpdated());
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
        ArrayList<task> updatedList=new ArrayList<task>();
        ArrayList<task> additionList=new ArrayList<task>();

        //check which tasks are displayed first
        taskList=setDisplayedAndUpdated(taskList);
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

            //Only want to add a item if it is not displayed or not marked
            //to be deleted.
            if(!currentTask.isDisplayed() && !currentTask.isDeleted())
            {
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
        }

        //Perform all of the interface update tasks
        if (!additionList.isEmpty()) {
            //ensure any new tasks are added to the task list using their
            //id
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
        }
    }


    public static boolean hasSelection(RadioGroup group)
    {
        return (group.getCheckedRadioButtonId() != -1);
    }


    //Listener Inheritance inner classes
    public class addClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            Intent addIntent= new Intent(v.getContext(), IndividualTodoItemView.class);
            manager.close();
            addIntent.putExtra("UPDATE_SET", false);
            startActivity(addIntent);
        }
    }



}
