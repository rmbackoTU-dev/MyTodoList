package com.example.mytodolist;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import java.lang.Thread.*;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.app.Instrumentation;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import com.example.mytodolist.model.databaseHelper;
import com.example.mytodolist.MainPage;
import com.example.mytodolist.model.databaseManager;

import androidx.test.espresso.intent.matcher.*;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class individualTodoItemViewTest {

    public Button addToList;
    public Context testContext;
    public databaseManager testManager;
    public IndividualTodoItemView testIndividualTodoItemView;
    public Intent testIntent;
    public final long  DEFAULT_SLEEP_TIME=500;
    public Handler mainHandler;
    //Set Time out of functions occurring outside main thread to 10 seconds
    public final long timeOutAmount=10;
    public final TimeUnit timeOutUnit=TimeUnit.SECONDS;

    @Rule
    public IntentsTestRule<IndividualTodoItemView> individualTest
            = new IntentsTestRule<> (IndividualTodoItemView.class);
    public IntentsTestRule <todoListView> todoListViewTest =
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
        testIndividualTodoItemView=individualTest.getActivity();


    }

    @After
    public void tearDown()
    {
        //We need to make sure the radio button is cleared out for the next test
        Log.i("TEAR_DOWN_INFO", "Running tear down");
        //RadioGroup clearTodoListRadio=testIndividualTodoItemView.findViewById(R.id.todoListRadio);
        try {

        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }


        testManager.close();
        testIndividualTodoItemView.finish();
    }

    @Test
    public void testNodeCoverageTextListenerAdd()
    {
        try{
            testIntent.putExtra("UPDATE_SET",false);
            testIndividualTodoItemView.startActivity(testIntent);
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("Fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);

            //addToList.setEnabled(true);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
}

}
