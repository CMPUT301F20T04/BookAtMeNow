package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.provider.ContactsContract;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    private Solo solo;

    /**
     * Test class for LoginActivity. All the UI tests are written here. Robotium test framework is
     used
     */
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<LoginActivity>(LoginActivity.class, true, true);

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

    @Test
    public void successfulLoginTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "CustomUsername");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "password");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    @Test
    public void wrongUsernameTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "NotUser");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "password");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
    }

    @Test
    public void wrongPasswordTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "CustomUsername");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "wrong");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    @Test
    public void clickCreateAccount() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.clickOnButton("Create Account");
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
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
