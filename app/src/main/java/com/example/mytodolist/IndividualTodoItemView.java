package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AutomaticZenRule;
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
import com.example.mytodolist.model.task;

public class IndividualTodoItemView extends AppCompatActivity {


    databaseManager manager;
    private boolean updateSet=false;
    private task updateTask;
    public static final String TASK_UPDATE_INDEX="com.example.mytodolist.INTEGER";
    public Button addToListButton;
    public EditText todoEditBox;
    public addToListClickListener addToListClick;

    /**
     * onCreate used when an activity is newly created by an intent, and is not
     * resumed from a previously created activity on the backstack
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_todo_item_view);
        manager=new databaseManager(this);
        manager.open();


        todoEditBox=findViewById(R.id.todoEditBox);
        addToListButton=findViewById(R.id.addToList);
        addToListButton.setEnabled(false);

        Intent updateIntent=getIntent();
        updateSet=updateIntent.getExtras().getBoolean("UPDATE_SET");
        if(updateSet)
        {
            updateTask=(task) updateIntent.getSerializableExtra(todoListView.TODO_OBJ);
            setUpdateHint(updateTask.getItem().toString());
        }

          editBoxWatcher editWatcher=new editBoxWatcher(this.addToListButton);
          todoEditBox.addTextChangedListener(editWatcher);


          addToListClick = new addToListClickListener();
          addToListButton.setOnClickListener(addToListClick);

    }


    /**
     * Used to handle back press in espresso testing environment
     */
    @Override
    public void onBackPressed()
    {
        Intent backIntent=new Intent(getApplicationContext(), todoListView.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
    }

    /**
     * Calls the database manager to add the new item to the database
     * @param itemText
     * @return a boolean determining if the add to database was successful
     */
    public boolean addItemToDatabase(String itemText)
    {
        boolean success=false;
        try {
            manager.insertTask(itemText);
            Cursor itemCursor=manager.getItemByText(itemText);
            if(itemCursor.moveToFirst())
            {
                 int itemIndex=itemCursor.getColumnIndex("item");
                String textFound=itemCursor.getString(itemIndex);
                System.out.println(textFound);
                int itemNum=itemCursor.getCount();
                success=(itemNum>0);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * Calls the database manager to update an existing task item in the databse
     * to the newly updated text provided by parameter updateText
     * @param updateTask
     * @param updateText
     * @return
     */
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

    /**
     * Gets the item text from the edit box to use in variables
     * allows editBox text to be sent as input to another function
     * @return
     */
    public String getItemText()
    {
        EditText itemText=findViewById(R.id.todoEditBox);
        return itemText.getText().toString();
    }

    /**
     * Sets the hint text in the editBox allows update function
     * to indicate what item is being changed.
     * @param hint
     */
    public void setUpdateHint( CharSequence hint)
    {
        EditText itemText=findViewById(R.id.todoEditBox);
        itemText.setHint(hint);
    }

    public class editBoxWatcher implements TextWatcher{

        public final Button addToListButton;

        public editBoxWatcher(Button addToListButton)
        {
            this.addToListButton=addToListButton;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            this.addToListButton.setEnabled(!getItemText().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public class addToListClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) throws IllegalArgumentException
        {

            if(v==null){
                throw new IllegalArgumentException();
            }

            Intent returnToList=new Intent(v.getContext(), todoListView.class);
            returnToList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(updateSet)
            {
                updateItemInDatabase(updateTask, getItemText());
                returnToList.putExtra("UPDATE_RECORD_SET", true);
                returnToList.putExtra(TASK_UPDATE_INDEX, updateTask.getId());
            }
            else
            {
                if(!(addItemToDatabase(getItemText())))
                {
                    System.err.println("Failed to add item "+getItemText());
                }
                returnToList.putExtra("UPDATE_RECORD_SET", false);
            }
            startActivity(returnToList);
        }
    }
}
