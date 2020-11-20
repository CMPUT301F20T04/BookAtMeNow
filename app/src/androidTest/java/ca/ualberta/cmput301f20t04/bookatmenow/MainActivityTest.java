package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
     * Check that tabs are the same activity (except requests).
     */
    @Test
    public void clickMenuItems() {
        solo.clickOnMenuItem("MY BOOKS");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnMenuItem("BORROWED");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnMenuItem("ALL BOOKS");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Checks that Requested menu item goes to my requests activity.
     */
    @Test
    public void clickRequested() {
        solo.clickOnMenuItem("REQUESTED");
        solo.assertCurrentActivity("Wrong Activity", MyRequests.class);
    }

    /**
     * Checks that addBook button goes to my book activity.
     */
    @Test
    public void addBook() {
        solo.clickOnMenuItem("MY BOOKS");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        FloatingActionButton add = (FloatingActionButton) solo.getView(R.id.floating_add);
        solo.clickOnView(add);
        solo.waitForActivity("MyBookActivity");
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
    }

    /**
     * Check filter button.
     * TODO: check dialog actions when search works
     */
    @Test
    public void clickFilter() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
//        solo.clickOnButton("Filter");
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
