package com.example.mytodolist;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import java.lang.Thread.*;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextWatcher;
import android.widget.TextView;
import android.app.Instrumentation;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import com.example.mytodolist.model.databaseHelper;
import com.example.mytodolist.MainPage;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class welcomeTest {

    public Intent testIntent;
    public databaseHelper helper;
    public Context testContext;
    public MainPage testActivity;



    @Rule
    public IntentsTestRule<MainPage> welcometest
            = new IntentsTestRule<> (MainPage.class);

    @Before
    public void setup()
    {
        testContext= ApplicationProvider.getApplicationContext();
        helper=new databaseHelper(testContext);
        testIntent = new Intent(testContext, MainPage.class);
        Instrumentation.ActivityResult externalResult=
                new Instrumentation.ActivityResult(Activity.RESULT_OK, testIntent);
        //Stub all external intents
         Intents.intending(Matchers.not(
         IntentMatchers.isInternal())).respondWith(
         externalResult);

         testActivity = welcometest.getActivity();
         testActivity.startActivity(testIntent);
    }

    @After
    public void teardown()
    {

    }

    @Test
    public void testNodeCoverageTextListener()
    {
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("SashaRyan"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
          Matcher greetingText = ViewMatchers.withId(R.id.greeting);
           Espresso.onView(greetingText).check(matches
                    (withText("Greetings SashaRyan")));
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void testNodeCoverageOnClickView(){
        try{
            Matcher editButton = ViewMatchers.withId(R.id.viewListButton);
            Espresso.onView(editButton).perform(ViewActions.click());
            Thread.sleep(500);
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"));
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }



    @Test
    public void testEdgeCoverageMainPage(){
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Matcher editButton = ViewMatchers.withId(R.id.viewListButton);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("SashaRyan"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Espresso.onView(greetingText).check(matches
                    (withText("Greetings SashaRyan")));
            Thread.sleep(500);
            Espresso.onView(editButton).perform(ViewActions.click());
            Thread.sleep(500);
            Intents.intended(IntentMatchers.hasComponent(
                    "com.example.mytodolist.todoListView"));

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test(expected = NullPointerException.class)
    public void ispNullTest()
    {
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(null));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test
    public void ispIntTestPositive() {
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(Integer.toString(1)));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings 1")));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispBooleanTrueTest() {
        try{
            boolean b1=true;
            String s1 = Boolean.toString(b1);
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(s1));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings " + s1)));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispBooleanFalseTest() {
        try{
            boolean b1=false;
            String s1 = Boolean.toString(b1);
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(s1));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings " + s1)));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispIntTestNegative() {
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(Integer.toString(-1)));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings -1")));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispIntTestZero() {
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(Integer.toString(0)));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings 0")));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }
    @Test
    public void ispCharTest(){
        try{
            String s=Character.toString('a');

            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(s));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings a")));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    @Test
    public void ispStringTest(){
            try{
                Matcher editBox = ViewMatchers.withId(R.id.nameField);
                Espresso.onView(editBox).perform(ViewActions.click());
                Thread.sleep(500);
                Espresso.onView(editBox).perform(ViewActions.typeText("String"));
                Thread.sleep(500);
                Espresso.closeSoftKeyboard();
                Thread.sleep(500);
                Matcher greetingText = ViewMatchers.withId(R.id.greeting);
                Espresso.onView(greetingText).check(matches(withText("Greetings String")));
                Thread.sleep(500);

            }catch(InterruptedException ie){
                ie.printStackTrace();
            }

        }

    @Test
    public void ispNewLineTest(){
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("//"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches(withText("Greetings //")));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispStringArrayTest(){
        try{
            String[] stringArray = {"test1" , "test2"};
            String arrayToString = Arrays.toString(stringArray);
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText(arrayToString));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            Espresso.onView(greetingText).check(matches((withText("Greetings [test1, test2]" ))));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }

    @Test
    public void ispTabTest(){
        try{
            Matcher editBox = ViewMatchers.withId(R.id.nameField);
            Espresso.onView(editBox).perform(ViewActions.click());
            Thread.sleep(500);
            Espresso.onView(editBox).perform(ViewActions.typeText("\t"));
            Thread.sleep(500);
            Espresso.closeSoftKeyboard();
            Thread.sleep(500);
            Matcher greetingText = ViewMatchers.withId(R.id.greeting);
            //TextView will not display strings with only non-visable strings
            Espresso.onView(greetingText).check(matches(not(withText("Greetings \\t"))));
            Thread.sleep(500);

        }catch(InterruptedException ie){
            ie.printStackTrace();
        }

    }



    @Test
    public void ispEmptyTest(){

            try{
                String s = "";
                Matcher editBox = ViewMatchers.withId(R.id.nameField);
                Espresso.onView(editBox).perform(ViewActions.click());
                Thread.sleep(500);
                Espresso.onView(editBox).perform(ViewActions.typeText(s));
                Thread.sleep(500);
                Espresso.closeSoftKeyboard();
                Thread.sleep(500);
                Matcher greetingText = ViewMatchers.withId(R.id.greeting);
                //TextView will not display strings with only non-visable strings
                Espresso.onView(greetingText).check(matches(not(withText("Greetings "))));
                Thread.sleep(500);

            }catch(InterruptedException ie){
                ie.printStackTrace();
            }

        }
    }



