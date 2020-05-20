package com.example.mytodolist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
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
                for (int i = 0; i < radioGroupSize; i++) {
                    Log.i("TEAR_DOWN_INFO", "Cleaning up left over task number: " + i);
                    RadioButton currentRadioButton=(RadioButton) clearTodoListRadio.getChildAt(i);
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
}
