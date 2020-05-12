package com.example.mytodolist;


import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mytodolist.model.databaseHelper;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(AndroidJUnit4.class)
public class databaseTest {

    public databaseHelper helper;
    public Context testContext;

    public void setup()
    {
        testContext= ApplicationProvider.getApplicationContext();
        helper=new databaseHelper(testContext);
    }

    public void teardown()
    {

    }

    public void testCreateDatabase()
    {

    }
}
