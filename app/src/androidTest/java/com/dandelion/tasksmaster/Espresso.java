package com.dandelion.tasksmaster;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class Espresso {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testAddTask() {
        onView(withId(R.id.addTaskBtn)).perform(click());
        onView(withId(R.id.taskTitleField)).perform(typeText("Sleeping"),closeSoftKeyboard() );
        onView(withId(R.id.taskBodyField)).perform(typeText("all the night"),closeSoftKeyboard() );
        onView(withId(R.id.taskStateField)).perform(typeText("not yet"),closeSoftKeyboard() );
        onView(withId(R.id.saveTaskBtn)).perform(click());
        onView(withText("Sleeping")).check(matches(isDisplayed()));
        onView(withText("all the night")).check(matches(isDisplayed()));
        onView(withText("not yet")).check(matches(isDisplayed()));
    }

    @Test
    public void testSettings() {
        onView(withId(R.id.SettingsBtn)).perform(click());
        onView(withId(R.id.usernameField)).perform(typeText("Nawal"));
        onView(withId(R.id.usernameSave)).perform(click());
        closeSoftKeyboard();
        onView(withText("Nawal's Tasks")).check(matches(isDisplayed()));
    }

    @Test
    public void allTaskTest() {
        onView(withId(R.id.allTasksBtn)).perform(click());
        onView(withId(R.id.allTaskData)).check(matches(isDisplayed()));
    }

    @Test
    public void checkTaskButton() {
        onView(withId(R.id.addTaskBtn)).perform(click());
        onView(withId(R.id.taskTitleField)).check(matches(isDisplayed()));
        onView(withId(R.id.taskBodyField)).check(matches(isDisplayed()));
        onView(withId(R.id.taskStateField)).check(matches(isDisplayed()));
        closeSoftKeyboard();
        onView(withId(R.id.saveTaskBtn)).perform(click());
    }

}
