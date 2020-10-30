package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfileActivityTest {
    private Solo solo;
    DBHandler db = new DBHandler();

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
    public void clickCancelTest() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnButton("Cancel");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Check that logout brings user to login screen
     */
    @Test
    public void clickLogoutTest() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnButton("Logout");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    @Test
    public void fullUserTest() {
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.enterText((EditText) solo.getView(R.id.username), "Me");
        solo.enterText((EditText) solo.getView(R.id.password), "mypw");
        solo.enterText((EditText) solo.getView(R.id.password_confirm), "mypw");
        solo.enterText((EditText) solo.getView(R.id.phone), "7801234567");
        solo.enterText((EditText) solo.getView(R.id.email), "me@mymail.com");
        solo.enterText((EditText) solo.getView(R.id.address), "my address");
        solo.clickOnButton("Save");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
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
