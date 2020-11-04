package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {
    private Solo solo;

    /**
     * Test class for MainActivity. Robotium test framework is used.
     */
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Check that Home and MyBooks are the same activity
     */
    @Test
    public void clickMyBooksHomeAdd() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("My Books");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Home");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("My Books");

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Add");

        solo.waitForActivity("MyBookActivity");
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
    }

    /**
     * Check nav to Profile
     */
    @Test
    public void clickProfile() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Profile");
        solo.waitForActivity("ProfileActivity");
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    /**
     * Check filter button.
     * TODO: check dialog actions when search works
     */
    @Test
    public void clickFilter() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Filter");
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
