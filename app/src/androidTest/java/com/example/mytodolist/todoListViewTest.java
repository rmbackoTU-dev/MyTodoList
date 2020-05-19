package com.example.mytodolist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
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
import org.junit.internal.runners.JUnit4ClassRunner;
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


        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.getMessage());
        }
    }


}
