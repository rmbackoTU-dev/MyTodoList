package com.example.mytodolist;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsNot.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class fullAppTest {

    @Rule
    public ActivityTestRule<MainPage> mActivityTestRule = new ActivityTestRule<>(MainPage.class);

    @Test
    public void fullAppTest() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.nameField), withText("Name"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Ryan"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.nameField), withText("Ryan"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatEditText2.perform(closeSoftKeyboard());

        ViewInteraction textView=onView(allOf(withId(R.id.greeting),
                withText("Greetings Ryan"))).check(matches(withText("Greetings Ryan")));

        ViewInteraction imageView=onView(allOf(withId(R.id.todoLogo)));
        imageView.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.viewListButton), withText("View List"), isDisplayed()));
        appCompatButton.perform(click());


        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addButton), withText("Add"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
              allOf(withId(R.id.todoEditBox), isDisplayed()));
        appCompatEditText3.perform(replaceText("Fruit"), closeSoftKeyboard());

        ViewInteraction editText = onView(allOf(withId(R.id.todoEditBox), withText("Fruit"), isDisplayed()));
        editText.check(matches(withText("Fruit")));

       ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.addToList), withText("Add To List"), isDisplayed()));
       appCompatButton3.perform(click());

       ViewInteraction textView2 = onView(
               allOf(withId(R.id.itemList)));
        textView2.check(matches(withText(containsString("Fruit"))));

       ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.removeButton), withText("Remove"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatEditText4 = onView(
               allOf(withId(R.id.deleteEditBox), isDisplayed()));
        appCompatEditText4.perform(replaceText("Fruit"), closeSoftKeyboard());

        ViewInteraction button = onView(
                allOf(withId(R.id.deleteItemButton), isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton5 = onView(allOf(withId(R.id.deleteItemButton),
        withText("Delete Item"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.itemList),isDisplayed()));
        textView3.check(matches(withText(not("Fruit"))));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
