package com.example.mytodolist;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import java.lang.Thread.*;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.example.mytodolist.model.task;

import androidx.test.espresso.intent.matcher.*;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
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
    public ActivityTestRule<IndividualTodoItemView> individualActivityRule=
            new ActivityTestRule<>(IndividualTodoItemView.class);

    @Before
    public void setup()
    {
        mainHandler= new Handler(Looper.getMainLooper());
        testContext= ApplicationProvider.getApplicationContext();
        testManager=new databaseManager(testContext);
        testManager.open();
        testIntent=new Intent(testContext, IndividualTodoItemView.class);
        Instrumentation.ActivityResult externalResult=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        //Stub all external intents
        Intents.intending(Matchers.not(
                IntentMatchers.isInternal())).respondWith(
                externalResult);


    }

    @After
    public void tearDown()
    {
        testManager.close();
        testIndividualTodoItemView.finish();
    }

    @Test
    public void testNodeCoverageTextListenerAdd()
    {
        try{
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("Fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Button addToListButtonTest=
                    testIndividualTodoItemView.addToListButton;
            Assert.assertTrue(addToListButtonTest.isEnabled());


            //addToList.setEnabled(true);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
}

    @Test
    public void testNodeCoverageTextListenerUpdate()
    {
        try{
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", true);
            task testTask=new task("test1", 0);
            currentIntent.putExtra(todoListView.TODO_OBJ, testTask);;
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("Fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Button addToListButtonTest=
                    testIndividualTodoItemView.addToListButton;
            Assert.assertTrue(addToListButtonTest.isEnabled());


            //addToList.setEnabled(true);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testISPCoverageOnClick(){
        Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
        currentIntent.putExtra("UPDATE_SET", false);
        individualActivityRule.launchActivity(currentIntent);
        testIndividualTodoItemView=individualActivityRule.getActivity();
        testIndividualTodoItemView.addToListClick.onClick(null);

    }

    @Test
    public void testAddOnClick()
    {
        try{
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("Fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"
            ));
            Intents.intended(IntentMatchers.hasExtra("UPDATE_RECORD_SET", false));


            //addToList.setEnabled(true);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test
    public void testUpdateOnClick()
    {
        try{
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", true);
            task testTask=new task("test1", 0);
            currentIntent.putExtra(todoListView.TODO_OBJ, testTask);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("Fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"
            ));
            Intents.intended(IntentMatchers.hasExtra("UPDATE_RECORD_SET", true));


            //addToList.setEnabled(true);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIspNullAddItemToDatabase() {
        Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
        currentIntent.putExtra("UPDATE_SET", false);
        individualActivityRule.launchActivity(currentIntent);
        testIndividualTodoItemView=individualActivityRule.getActivity();
        testIndividualTodoItemView.addItemToDatabase(null);
    }

    @Test
    public void testIspStringAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("fruit"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("fruit");
            Assert.assertEquals(1,testCursor.getCount());
            task itemAdded = new task("fruit",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspCharAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("c"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("c");
            Assert.assertEquals(1,testCursor.getCount());
            task itemAdded = new task("c",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspInt0AddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("0"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("0");
            Assert.assertEquals(1,testCursor.getCount());
            task itemAdded = new task("0",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspIntNegativeAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("-1"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("-1");
            Assert.assertEquals(1,testCursor.getCount());
            task itemAdded = new task("-1",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspIntPositiveAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();;
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("1"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("1");
            Assert.assertEquals(1,testCursor.getCount());
            task itemAdded = new task("1",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspEmptyAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();;
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(""));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("");
            //expected to fail on all non visible characters
            Assert.assertEquals(0,testCursor.getCount());
            task itemAdded = new task("",0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    @Test
    public void testIspMultipleStringLinesAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();;
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("//"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("//");
            Assert.assertEquals(1, testCursor.getCount());
            task itemAdded = new task("//", 0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

        @Test
        public void testIspStringArrayAddItemToDatabase() {
            try {
                String[] stringArray= new String[1];
                String inputString = Arrays.toString(stringArray);
                Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
                Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
                currentIntent.putExtra("UPDATE_SET", false);
                individualActivityRule.launchActivity(currentIntent);
                testIndividualTodoItemView=individualActivityRule.getActivity();
                Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
                Espresso.onView(editBox).perform(ViewActions.click());
                Thread.sleep(500);
                Espresso.onView(editBox).perform(ViewActions.typeText(inputString));
                Thread.sleep(500);
                Espresso.closeSoftKeyboard();
                Thread.sleep(500);
                Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                        ViewActions.click()
                );
                Cursor testCursor = testManager.getItemByText(inputString);
                Assert.assertEquals(1, testCursor.getCount());
                task itemAdded = new task(inputString, 0);
                testManager.deleteTask(itemAdded);

            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

    @Test
    public void testIspTabAddItemToDatabase() {
        try {
            Context targetContext= InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent currentIntent=new Intent(targetContext, IndividualTodoItemView.class);
            currentIntent.putExtra("UPDATE_SET", false);
            individualActivityRule.launchActivity(currentIntent);
            testIndividualTodoItemView=individualActivityRule.getActivity();
            Matcher editBox = ViewMatchers.withId(R.id.todoEditBox);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("\t"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(ViewMatchers.withId(R.id.addToList)).perform(
                    ViewActions.click()
            );
            Cursor testCursor = testManager.getItemByText("\t");
            //Should fail to parse only non-visible characters
            Assert.assertEquals(0, testCursor.getCount());
            task itemAdded = new task("\t", 0);
            testManager.deleteTask(itemAdded);

        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }}


