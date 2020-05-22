package com.example.mytodolist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.VerificationMode;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.matcher.*;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentActivity.*;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.Matchers.*;
import com.example.mytodolist.model.databaseManager;
import com.example.mytodolist.model.task;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;
import java.lang.Thread.*;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

@RunWith(AndroidJUnit4.class)
public class todoListViewTest {

    public Context testContext;
    public databaseManager testManager;
    public todoListView testTodoListActivity;
    public Intent testIntent;
    public final long  DEFAULT_SLEEP_TIME=500;
    public Handler mainHandler;
    //Set Time out of functions occurring outside main thread to 10 seconds
    public final long timeOutAmount=10;
    public final TimeUnit timeOutUnit=TimeUnit.SECONDS;


    @Rule
    public IntentsTestRule<todoListView> todoListViewIntentsRule=
            new IntentsTestRule<>(todoListView.class);


    @Before
    public void setup()
    {
        testContext= ApplicationProvider.getApplicationContext();
        testManager=new databaseManager(testContext);
        testManager.open();
        testIntent=new Intent(testContext, todoListView.class);
        Instrumentation.ActivityResult externalResult=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        //Stub all external intents
        Intents.intending(Matchers.not(
                IntentMatchers.isInternal())).respondWith(
                externalResult);
        testTodoListActivity=todoListViewIntentsRule.getActivity();
        testTodoListActivity.startActivity(testIntent);
        mainHandler=new Handler(Looper.getMainLooper());

    }


    @After
    public void tearDown()
    {
        //We need to make sure the radio button is cleared out for the next test
        Log.i("TEAR_DOWN_INFO", "Running tear down");
        RadioGroup clearTodoListRadio=testTodoListActivity.findViewById(R.id.todoListRadio);
        try {
            if (clearTodoListRadio.getChildCount() > 0) {
                Log.i("TEAR_DOWN_INFO", "Cleaning up "+
                        clearTodoListRadio.getChildCount()
                        + " left over tasks");
                int radioGroupSize = clearTodoListRadio.getChildCount();
                int i=0;
                while( i < radioGroupSize) {
                    int oldRadioGroupSize=radioGroupSize;
                    Log.i("TEAR_DOWN_INFO", "Cleaning up left over task number: " + i);
                    RadioButton currentRadioButton=(RadioButton) clearTodoListRadio.getChildAt(i);
                    Log.i("TEAR_DOWN_INFO", "Current radio button text "+
                            currentRadioButton.getText());
                    String radioButtonTag=(String) currentRadioButton.getTag();
                    Log.i("TEAR_DOWN_INFO", "Current radio button id "+
                            currentRadioButton.getId());
                    Matcher currentRadioButtonMatcher = ViewMatchers.withTagValue(
                            Matchers.is((Object) radioButtonTag));
                    Espresso.onView(currentRadioButtonMatcher).perform(
                            ViewActions.click()
                    );
                    Thread.sleep(DEFAULT_SLEEP_TIME);
                    Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                            ViewActions.click()
                    );
                    Thread.sleep(DEFAULT_SLEEP_TIME);
                    if (currentRadioButtonMatcher.matches(ViewAssertions.doesNotExist())) {
                        Log.i("TEAR_DOWN_INFO", "Success");
                    }

                    //update the radio group size since the tags have slid down by 1
                    if(i+1< oldRadioGroupSize)
                    {
                        radioGroupSize=clearTodoListRadio.getChildCount();
                    }
                    else
                    {
                        //if the next iteration would be end the loop increment i
                        i=i+1;
                    }
                }
                radioGroupSize = clearTodoListRadio.getChildCount();
                Log.i("TEAR_DOWN_INFO", "New radio list button count " + radioGroupSize);
            }
        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }


        testManager.close();
        testTodoListActivity.finish();
    }


    @Test
    public void testAddClickListenerInitializedButton()
    {
        Instrumentation.ActivityResult individualTodoItemViewStub=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        Intents.intending(IntentMatchers.toPackage(
                "com.example.mytodolist.IndividualTodoItemView")).respondWith(
                individualTodoItemViewStub);
        Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(
                "com.example.mytodolist.IndividualTodoItemView"));
        Intents.intended(IntentMatchers.hasExtra("UPDATE_SET",false));

    }

    @Test
    public void testDeleteClickListenerInitializedButton()
    {
        try{
            String testInput="test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton=
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher=
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be back to todoListView activity
            //We should be able to get the count of the todoListRadio and it should be 1
            RadioGroup todoListRadio=testTodoListActivity.findViewById(R.id.todoListRadio);
            int radioButtonCount=todoListRadio.getChildCount();
            Assert.assertEquals(1, radioButtonCount);

            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInput)).perform(
                    ViewActions.click()
            );
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );
            //We should be able to get the count of the todoListRadio and it should be 0
            todoListRadio=testTodoListActivity.findViewById(R.id.todoListRadio);
            radioButtonCount=todoListRadio.getChildCount();
            Assert.assertEquals(0, radioButtonCount);
        }
        catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        }
    }


    @Test
    public void testUpdateClickListenerInitilizedButton()
    {
        try
        {
            String testInput="test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton=
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher=
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be back to todoListView activity
            //We should be able to get the count of the todoListRadio and it should be 1
            RadioGroup todoListRadio=testTodoListActivity.findViewById(R.id.todoListRadio);
            int radioButtonCount=todoListRadio.getChildCount();
            Assert.assertEquals(1, radioButtonCount);

            //Button must be clicked to enable update button
            ViewInteraction radioButton= Espresso.onView(ViewMatchers.withText(testInput));
            radioButton.perform(
                    ViewActions.click()
            );

            Espresso.onView(ViewMatchers.withId(R.id.updateButton)).perform(
                    ViewActions.click()
            );

            Intents.intending(IntentMatchers.hasComponent(
                    "com.example.mytodolist.IndividualTodoItemView"));
            Intents.intending(IntentMatchers.hasExtra("UPDATE_SET",  true));
            //Press Back to reset to main view
            Thread.sleep(DEFAULT_SLEEP_TIME);
            Espresso.pressBack();
            Thread.sleep(DEFAULT_SLEEP_TIME);

        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.getMessage());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullViewAddOnClick()
    {
        testTodoListActivity.addClick.onClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullViewDeleteOnClick()
    {
        testTodoListActivity.deleteClick.onClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullViewUpdateOnClick()
    {
        testTodoListActivity.updateClick.onClick(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOnCheckedChangeNullGroup0Id()
    {
        RadioGroup nullRadio=null;
        testTodoListActivity.radioChange.onCheckedChanged(nullRadio, 0);
    }

    @Test
    public void testEmptyGroup0Id()
    {
        RadioGroup emptyGroup=new RadioGroup(testContext);
        testTodoListActivity.radioChange.onCheckedChanged(emptyGroup, 0);
        if( !(testTodoListActivity.updateButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled" );
        }
        if( !(testTodoListActivity.deleteButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "DELETE BUTTON is not enabled" );
        }
        Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
    }

    @Test
    public void testEmptyGroup1Id()
    {
        RadioGroup emptyGroup=new RadioGroup(testContext);
        testTodoListActivity.radioChange.onCheckedChanged(emptyGroup, 1);
        if( !(testTodoListActivity.updateButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled" );
        }
        if( !(testTodoListActivity.deleteButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "DELETE BUTTON is not enabled" );
        }
        Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
    }

    @Test
    public void testEmptyGroupNegativeOneId()
    {
        RadioGroup emptyGroup=new RadioGroup(testContext);
        testTodoListActivity.radioChange.onCheckedChanged(emptyGroup, -1);
        if( !(testTodoListActivity.updateButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled" );
        }
        if( !(testTodoListActivity.deleteButton.isEnabled()))
        {
            Log.i("VAR_STATUS", "DELETE BUTTON is not enabled" );
        }
        Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
    }

    @Test
    public void test1ItemGroup0Id()
    {
        try {
            String testInput = "test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, 0);
            Log.i("RADIO_CHECK_STATUS", "Radio button currently selected is "
                    +appRadioGroup.getCheckedRadioButtonId());
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if ((testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is  not enabled");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test1ItemGroup0IdWithUIClick()
    {
        try {
            String testInput = "test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the button in the UI so has selection is not -1
            Espresso.onView(ViewMatchers.withText(testInput)).perform(
                    ViewActions.click()
            );
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, 0);
            Log.i("RADIO_CHECK_STATUS", "Radio button currently selected is "
                    +appRadioGroup.getCheckedRadioButtonId());
            //Verify that the radio button is selected
            if (testTodoListActivity.updateButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is enabled");
            }
            if (testTodoListActivity.deleteButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is  enabled");
            }
            Assert.assertTrue(testTodoListActivity.updateButton.isEnabled());
            Assert.assertTrue(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test2ItemGroup0IdWithUIClick()
    {
        try {
            String testInput = "test1";
            String testInputTwo="test2";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add Item number 2
             */
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            /*
             *Add the text to the database
             *Verify the button exist
             *
             */
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the button in the UI so has selection is not -1
            Espresso.onView(ViewMatchers.withText(testInput)).perform(
                    ViewActions.click()
            );
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, 0);
            Log.i("RADIO_CHECK_STATUS", "Radio button currently selected is "
                    +appRadioGroup.getCheckedRadioButtonId());
            //Verify that the radio button is selected
            if (testTodoListActivity.updateButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is enabled");
            }
            if (testTodoListActivity.deleteButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is  enabled");
            }
            Assert.assertTrue(testTodoListActivity.updateButton.isEnabled());
            Assert.assertTrue(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }


    @Test
    public void test2ItemGroup1IdWithUIClick()
    {
        try {
            String testInput = "test1";
            String testInputTwo="test2";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add Item number 2
             */
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            /*
             *Add the text to the database
             *Verify the button exist
             *
             */
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the button in the UI so has selection is not -1
            Espresso.onView(ViewMatchers.withText(testInputTwo)).perform(
                    ViewActions.click()
            );
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             * test with 1 selectID
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, 1);
            Log.i("RADIO_CHECK_STATUS", "Radio button currently selected is "
                    +appRadioGroup.getCheckedRadioButtonId());
            //Verify that the radio button is selected
            if (testTodoListActivity.updateButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is enabled");
            }
            if (testTodoListActivity.deleteButton.isEnabled()) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is  enabled");
            }
            Assert.assertTrue(testTodoListActivity.updateButton.isEnabled());
            Assert.assertTrue(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test2ItemGroup1IdWithoutUIClick()
    {
        try {
            String testInput = "test1";
            String testInputTwo="test2";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add Item number 2
             */
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            /*
             *Add the text to the database
             *Verify the button exist
             *
             */
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             * test with 1 selectID
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, 1);
            Log.i("RADIO_CHECK_STATUS", "Radio button currently selected is "
                    +appRadioGroup.getCheckedRadioButtonId());
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if (!(testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled ");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }


    @Test
    public void test1ItemGroupNegativeOneIdWithoutUIClick()
    {
        try {
            String testInput = "test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            RadioGroup appRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            /**
             * Function under test onCheckedChanged
             * test with -1 selectID, with 1 item
             */
            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup, -1);
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if (!(testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled ");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test1ItemGroupNegativeOneIdWithUIClick()
    {
        try {
            String testInput = "test1";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            //Click the Radio Button in the UI to see if onCheckedChangedNotices a difference
            ViewInteraction RadioButtonInteraction=Espresso.onView(ViewMatchers.withText(
                    testInput
            ));
            RadioButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            this.mainHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            Log.i("TEST_RADIO_CHANGE", "radioChange.onCheckedChange runs");
                            RadioGroup appRadioGroup=
                                    testTodoListActivity.findViewById(R.id.todoListRadio);
                            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup,
                                    -1);
                        }
                    }
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            Log.i("TEST_RADIO_CHANGE", "Outside of radioChange.onCheckedChange");
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if (!(testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled ");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test2ItemGroupNegativeOneIdWithUIClick()
    {
        try {
            String testInput = "test1";
            String testInputTwo="test2";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add Item number 2
             */
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            /*
             *Add the text to the database
             *Verify the button exist
             *
             */
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the Radio Button in the UI to see if onCheckedChangedNotices a difference
            ViewInteraction RadioButtonInteraction=Espresso.onView(
                    ViewMatchers.withText(testInputTwo));
            RadioButtonInteraction.perform(ViewActions.click());
            this.mainHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            /**
                             * Test onCheckedChange with multiple Item list -1 value
                             */
                            Log.i("TEST_RADIO_CHANGE", "radioChange.onCheckedChange runs");
                            RadioGroup appRadioGroup=
                                    testTodoListActivity.findViewById(R.id.todoListRadio);
                            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup,
                                    -1);
                        }
                    }
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            Log.i("TEST_RADIO_CHANGE", "Outside of radioChange.onCheckedChange");
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if (!(testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled ");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void test2ItemGroupNegativeOneIdWithOutUIClick()
    {
        try {
            String testInput = "test1";
            String testInputTwo="test2";
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add Item number 2
             */
            Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                    ViewActions.click()
            );
            //Sleep between actions to give the UIThread time to catch up
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Should be in the add view
            /*
             *Add the text to the database
             *Verify the button exist
             *
             */
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Run function without clicking the radio to see how the onCheckedChange behaves
            this.mainHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            /**
                             * Test onCheckedChange with multiple Item list -1 value
                             */
                            Log.i("TEST_RADIO_CHANGE", "radioChange.onCheckedChange runs");
                            RadioGroup appRadioGroup=
                                    testTodoListActivity.findViewById(R.id.todoListRadio);
                            testTodoListActivity.radioChange.onCheckedChanged(appRadioGroup,
                                    -1);
                        }
                    }
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            Log.i("TEST_RADIO_CHANGE", "Outside of radioChange.onCheckedChange");
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Verify that the radio button is selected
            if (!(testTodoListActivity.updateButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled");
            }
            if (!(testTodoListActivity.deleteButton.isEnabled())) {
                Log.i("VAR_STATUS", "UPDATE BUTTON is not enabled ");
            }
            Assert.assertFalse(testTodoListActivity.updateButton.isEnabled());
            Assert.assertFalse(testTodoListActivity.deleteButton.isEnabled());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveFromDatabaseAndUINullTask()
    {
        testTodoListActivity.removeTaskFromDatabaseAndUI(null);
    }

    @Test
    public void testRemoveFromDatabaseAndUIOneTaskRemoveFirst()
    {
        try
        {
            String testInput="test1";
            //Add Item to test with
            ViewInteraction addButtonInteraction=
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInput)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );

            //Assert there is no items in the task list
            Assert.assertEquals(0, testTodoListActivity.tasks.size());
            //Assert there is no items in the radio group
            RadioGroup taskListRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            Assert.assertEquals(0, taskListRadioGroup.getChildCount());
            //Assert there are no items in the database
            Cursor taskCursor=testManager.getItemByText("test1");
            Assert.assertEquals(0, taskCursor.getCount());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testRemoveFromDatabaseAndUITwoTasksRemoveFirst()
    {
        try
        {
            String testInput="test1";
            String testInputTwo="test2";
            //Add Item to test with
            ViewInteraction addButtonInteraction=
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            /*
             * Add a Second Item
             */
            Thread.sleep(DEFAULT_SLEEP_TIME);
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInput)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );

            //Assert there is 1 items in the task list
            Assert.assertEquals(1, testTodoListActivity.tasks.size());
            //Assert there is 1 items in the radio group
            RadioGroup taskListRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            Assert.assertEquals(1, taskListRadioGroup.getChildCount());
            //Assert there are 1 items in the database
            Cursor taskCursor=testManager.getItemByText("test2");
            Assert.assertEquals(1, taskCursor.getCount());
            //Additionally check that task 2 which is test2 has an id of 0
            task taskAtIndexZero=testTodoListActivity.tasks.get(0);
            Assert.assertEquals("test2", taskAtIndexZero.getItem());
            Assert.assertEquals(0, taskAtIndexZero.getId());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testRemoveFromDatabaseAndUITwoTasksRemoveLast()
    {
        try
        {
            String testInput="test1";
            String testInputTwo="test2";
            //Add Item to test with
            ViewInteraction addButtonInteraction=
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            /*
             * Add a Second Item
             */
            Thread.sleep(DEFAULT_SLEEP_TIME);
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInputTwo)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );

            //Assert there is 1 items in the task list
            Assert.assertEquals(1, testTodoListActivity.tasks.size());
            //Assert there is 1 items in the radio group
            RadioGroup taskListRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            Assert.assertEquals(1, taskListRadioGroup.getChildCount());
            //Assert there are 1 items in the database
            Cursor taskCursor=testManager.getItemByText("test1");
            Assert.assertEquals(1, taskCursor.getCount());
            //Additionally check that task 1 which is test1 has an id of 0;
            //Id and text should not have changed.
            task taskAtIndexZero=testTodoListActivity.tasks.get(0);
            Assert.assertEquals("test1", taskAtIndexZero.getItem());
            Assert.assertEquals(0, taskAtIndexZero.getId());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testRemoveFromDatabaseAndUIThreeTasksRemoveSecond()
    {
        try
        {
            String testInput="test1";
            String testInputTwo="test2";
            String testInputThree="test3";
            //Add Item to test with
            ViewInteraction addButtonInteraction=
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            /*
             * Add a Second Item
             */
            Thread.sleep(DEFAULT_SLEEP_TIME);
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputTwo));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            Thread.sleep(DEFAULT_SLEEP_TIME);
            /*
             * Add a Third Item
             */
            Thread.sleep(DEFAULT_SLEEP_TIME);
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInputThree));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());


            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInputTwo)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );

            //Assert there is 2 items in the task list
            Assert.assertEquals(2, testTodoListActivity.tasks.size());
            //Assert there is  2 items in the radio group
            RadioGroup taskListRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            Assert.assertEquals(2, taskListRadioGroup.getChildCount());
            //Assert there are 2 items in the database
            ArrayList<task> taskList=testManager.getAllTask();
            Assert.assertEquals(2, taskList.size());
            //Additionally check that task 3 which is test3 has an id of 1;
            //and check that task 1 which is test 1 has an id of 0 still
            //Id and text should not have changed.
            task taskAtIndexOne=testTodoListActivity.tasks.get(1);
            task taskAtIndexZero=testTodoListActivity.tasks.get(0);
            Assert.assertEquals("test3", taskAtIndexOne.getItem());
            Assert.assertEquals(1, taskAtIndexOne.getId());
            Assert.assertEquals("test1", taskAtIndexZero.getItem());
            Assert.assertEquals(0, taskAtIndexZero.getId());

        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testRemoveFromDatabaseAndUIFourTasksRemoveSecond()
    {
        try
        {
            String[] testInputs={"test1", "test2", "test3", "test4"};
            //Add Items to list
            for(int i=0; i< testInputs.length; i++)
            {
                ViewInteraction addButtonInteraction=
                        Espresso.onView(ViewMatchers.withId(R.id.addButton));
                addButtonInteraction.perform(ViewActions.click());
                Thread.sleep(DEFAULT_SLEEP_TIME*4);
                //Add the text to the database
                ViewInteraction addToListButton =
                        Espresso.onView(ViewMatchers.withId(R.id.addToList));
                //Verify the button exist
                Thread.sleep(DEFAULT_SLEEP_TIME);
                Espresso.onView(ViewMatchers.withId(R.id.todoEditBox)).perform(
                        ViewActions.click()
                );
                Thread.sleep(DEFAULT_SLEEP_TIME);
                Espresso.onView(ViewMatchers.withId(R.id.todoEditBox)).perform(
                        ViewActions.typeTextIntoFocusedView(testInputs[i]));
                Thread.sleep(DEFAULT_SLEEP_TIME);
                Espresso.onView(ViewMatchers.withId(R.id.todoEditBox)).check(
                        matches(ViewMatchers.hasFocus())).perform(
                        ViewActions.closeSoftKeyboard()
                );
                addToListButton.check(matches(ViewMatchers.isDisplayed()));
                Thread.sleep(DEFAULT_SLEEP_TIME);
                //click add To list in order to complete adding an item
                addToListButton.perform(ViewActions.click());
                Thread.sleep(DEFAULT_SLEEP_TIME*4);
            }

            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Button must be clicked to enable delete button
            Espresso.onView(ViewMatchers.withText(testInputs[1])).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Click the delete Button under test
            Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);

            //Assert there is 3 items in the task list
            Assert.assertEquals(3, testTodoListActivity.tasks.size());
            //Assert there is  3 items in the radio group
            RadioGroup taskListRadioGroup=testTodoListActivity.findViewById(R.id.todoListRadio);
            Assert.assertEquals(3, taskListRadioGroup.getChildCount());
            //Assert there are 3 items in the database
            ArrayList<task> taskList=testManager.getAllTask();
            Assert.assertEquals(3, taskList.size());
            //Assert each item has the correct id and text
            String[] expectedItems={"test1", "test3", "test4"};
            for(int j=0; j<expectedItems.length; j++)
            {
                task currentTask=testTodoListActivity.tasks.get(j);
                String currentExpectedItem=expectedItems[j];
                Assert.assertEquals(j, currentTask.getId());
                Assert.assertEquals(currentExpectedItem, currentTask.getItem());
            }

        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testOnRestartFromUpdateWithUpdatedTask()
    {
        try
        {
            String testInput="test1";
            String testInputTwo="test2";
            //Add Item to test with
            ViewInteraction addButtonInteraction=
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);

            ViewInteraction radioInteraction=Espresso.onView(ViewMatchers.withText(testInput));
            radioInteraction.perform(ViewActions.click());

            ViewInteraction updateButtonInteraction=Espresso.onView(
                    ViewMatchers.withId(R.id.updateButton));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            updateButtonInteraction.perform(ViewActions.click());

            //In the IndividualTodoItemTask
            ViewInteraction updateEditBox=Espresso.onView(
                    ViewMatchers.withId(R.id.todoEditBox)
            );
            updateEditBox.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            updateEditBox.perform(ViewActions.typeText(testInputTwo));
            Espresso.closeSoftKeyboard();
            Thread.sleep(DEFAULT_SLEEP_TIME);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Check that after update is run onRestart added the updated task to tasks.
            ArrayList<task> testTasks=testTodoListActivity.tasks;
            task updatedTask=testTasks.get(0);
            Assert.assertTrue(updatedTask.isUpdated());
        }
        catch( InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testOnRestartFromAddWithAddedTask()
    {
        try {
            String testInput = "test1";
            //Add Item to test with
            ViewInteraction addButtonInteraction =
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);

            //Assert the new task is displayed
            ArrayList<task> testTasks=testTodoListActivity.tasks;
            task addedTask=testTasks.get(0);
            Assert.assertTrue(addedTask.isDisplayed());
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }

    }

    @Test
    public void testOnRestartFromUpdateWithoutUpdatedTask()
    {
        try {
            String testInput = "test1";
            //Add Item to test with
            ViewInteraction addButtonInteraction =
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            ViewInteraction addToListButton =
                    Espresso.onView(ViewMatchers.withId(R.id.addToList));
            //Verify the button exist
            addToListButton.check(matches(ViewMatchers.isDisplayed()));
            ViewInteraction editBoxMatcher =
                    Espresso.onView(ViewMatchers.withId(R.id.todoEditBox));
            editBoxMatcher.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.perform(ViewActions.typeTextIntoFocusedView(testInput));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            editBoxMatcher.check(matches(ViewMatchers.hasFocus())).perform(
                    ViewActions.closeSoftKeyboard()
            );
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //click add To list in order to complete adding an item
            addToListButton.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);

            ViewInteraction radioInteraction = Espresso.onView(ViewMatchers.withTagValue(
                    Matchers.is((Object)"id_0")));
            radioInteraction.perform(ViewActions.click());

            ViewInteraction updateButtonInteraction = Espresso.onView(
                    ViewMatchers.withId(R.id.updateButton));
            Thread.sleep(DEFAULT_SLEEP_TIME);
            updateButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);

            //In the IndividualTodoItemTask
            Espresso.pressBack();
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"),
                    Intents.times(3));
            Intents.intended(IntentMatchers.hasExtra("UPDATE_RECORD_SET", false));
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test
    public void testOnRestartFromAddWithoutAddedTask()
    {
        try {
            String testInput = "test1";
            //Add Item to test with
            ViewInteraction addButtonInteraction =
                    Espresso.onView(ViewMatchers.withId(R.id.addButton));
            addButtonInteraction.perform(ViewActions.click());
            Thread.sleep(DEFAULT_SLEEP_TIME);
            //Add the text to the database
            Espresso.pressBack();
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"),
                    Intents.times(2));
            //Check that when pressing back that the onNewIntent Changes the empty extras
            //to add the UPDATE_RECORD_SET
            boolean updateRecordSet= testTodoListActivity.currentExtras.getBoolean("UPDATE_RECORD_SET", false);
            Assert.assertFalse(updateRecordSet);
        }
        catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAddTask()
    {
        testTodoListActivity.addToList(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullUpdateTask()
    {
        testTodoListActivity.updateList(null);
    }

}