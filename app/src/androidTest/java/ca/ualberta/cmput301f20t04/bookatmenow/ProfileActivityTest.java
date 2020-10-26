package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileActivityTest {
    private Solo solo;

    /**
     * Test class for ProfileActivity. All the UI tests are written here. Robotium test framework is
     used
     */
    @Rule
    public ActivityTestRule<ProfileActivity> rule =
            new ActivityTestRule<ProfileActivity>(ProfileActivity.class, true, true);

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
     * Check that Cancel button brings user back to Home screen
     */
    @Test
    public void clickCancel() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnButton("Cancel");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Check that logout brings user to login screen
     */
    @Test
    public void clickLogout() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnButton("Logout");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
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
