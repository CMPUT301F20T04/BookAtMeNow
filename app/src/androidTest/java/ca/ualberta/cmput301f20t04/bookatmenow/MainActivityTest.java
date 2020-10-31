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
     * Test class for MainActivity. All the UI tests are written here. Robotium test framework is
     used
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
     * Check that buttons are visible / invisible
     */
    @Test
    public void clickMyBooks() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        assertEquals(solo.getView(R.id.home).getVisibility(), View.GONE);;
        assertEquals(solo.getView(R.id.add).getVisibility(), View.GONE);;
        solo.clickOnButton("My Books");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        assertEquals(solo.getView(R.id.home).getVisibility(), View.VISIBLE);;
        assertEquals(solo.getView(R.id.add).getVisibility(), View.VISIBLE);;
    }

    /**
     * Check nav to Profile
     */
    @Test
    public void clickProfile() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Profile");
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void clickFilter() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Filter");
        // check dialog actions when search works
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
