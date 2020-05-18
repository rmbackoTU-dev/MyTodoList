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

@RunWith(AndroidJUnit4.class)
public class todoListViewTest {

    public Context testContext;
    public databaseManager testManager;
    public todoListView testTodoListActivity;
    public Intent testIntent;


    @Rule
    public IntentsTestRule<todoListView> todoListViewIntentsRule=
            new IntentsTestRule<>(todoListView.class);


    @Before
    public void setup()
    {
        testContext= ApplicationProvider.getApplicationContext();
        testManager=new databaseManager(testContext);
        testManager.open();
        testIntent=new Intent();
        Instrumentation.ActivityResult externalResult=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        //Stub all external intents
        Intents.intending(Matchers.not(
                IntentMatchers.isInternal())).respondWith(
                        externalResult);

        //Stub all internal intents
//        Intents.intending(IntentMatchers.isInternal()).respondWith(
//                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent)
//                );

    }


    @After
    public void tearDown()
    {
        testManager.close();
    }


    @Test
    public void testAddClickListenerInitializedButton()
    {
        testTodoListActivity=todoListViewIntentsRule.getActivity();
        Instrumentation.ActivityResult individualTodoItemViewStub=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        Intents.intending(IntentMatchers.toPackage(
                "com.example.mytodolist.IndividualTodoItemView")).respondWith(
                        individualTodoItemViewStub);
        Espresso.onView(ViewMatchers.withId(R.id.addButton)).perform(
                ViewActions.click());
        Intents.intended(IntentMatchers.hasComponent(
                "com.example.mytodolist.IndividualTodoItemView"));
    }

    @Test
    public void testDeleteClickListenerInitlizedButton()
    {
        testTodoListActivity=todoListViewIntentsRule.getActivity();
        String[] testInputs={"test1", "test2"};
        testManager.insertTask(testInputs[0]);
        testManager.insertTask(testInputs[1]);
        
        //Button must be clicked to enable update button
        Espresso.onView(ViewMatchers.withText(testInputs[0])).perform(
                ViewActions.click()
        );
        Espresso.onView(ViewMatchers.withId(R.id.removeButton)).perform(
                ViewActions.click());
        RadioGroup todoListRadio=testTodoListActivity.findViewById(R.id.todoListRadio);
        int radioButtonCount=todoListRadio.getChildCount();
        Assert.assertEquals(1, radioButtonCount);
        task deletionTask=new task( testInputs[1], 1);
        testManager.deleteTask(deletionTask);
    }

    @Test
    public void testUpdateClickListenerInitilizedButton()
    {
        testTodoListActivity=todoListViewIntentsRule.getActivity();
        Instrumentation.ActivityResult individualTodoListItemViewActivity=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        Intents.intending(IntentMatchers.toPackage(
                "com.example.mytodolist.IndividualTodoItemView"));
        String[] testInputs={"test1", "test2"};
        testManager.insertTask(testInputs[0]);
        testManager.insertTask(testInputs[1]);
        //Button must be clicked to enable update button
        Espresso.onView(ViewMatchers.withText(testInputs[0])).perform(
                ViewActions.click()
        );
        Espresso.onView(ViewMatchers.withId(R.id.updateButton)).perform(
                ViewActions.click());
        //Includes two asserts one verifies the intent was to the correct class
        //the other ensures the intent added the correct extras
        Intents.intending(IntentMatchers.hasComponent(
                "com.example.mytodolist.IndividulaTodoItemView"));
        Intents.intending(IntentMatchers.hasExtra("UPDATE_SET", true));
    }


}
