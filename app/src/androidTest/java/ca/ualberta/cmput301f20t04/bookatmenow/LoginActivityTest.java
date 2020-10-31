package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void successfulLoginLogoutTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "CustomUsername");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "password");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Profile");
        solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        solo.clickOnButton("Logout");
        solo.waitForActivity("LoginActivity"); // check if everything else finished
    }

    @Test
    public void wrongUsernameTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "NotUser");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "password");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    @Test
    public void wrongPasswordTest() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.login_user), "CustomUsername");
        solo.enterText((EditText) solo.getView(R.id.login_pw), "wrong");
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
    }

    /**
     * Error:
     * android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@8e6f5e4 is not valid; is your activity running?
     * at android.view.ViewRootImpl.setView(ViewRootImpl.java:1068)
     * at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:409)
     * at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:109)
     * at android.app.Dialog.show(Dialog.java:340)
     * at android.app.AlertDialog$Builder.show(AlertDialog.java:1131)
     * at ca.ualberta.cmput301f20t04.bookatmenow.LoginActivity$3$1$1$1.onSuccess(LoginActivity.java:93)
     * at ca.ualberta.cmput301f20t04.bookatmenow.LoginActivity$3$1$1$1.onSuccess(LoginActivity.java:83)
     * at com.google.android.gms.tasks.zzn.run(com.google.android.gms:play-services-tasks@@17.1.0:4)
     * at android.os.Handler.handleCallback(Handler.java:938)
     * at android.os.Handler.dispatchMessage(Handler.java:99)
     * at com.google.android.gms.internal.tasks.zzb.dispatchMessage(com.google.android.gms:play-services-tasks@@17.1.0:6)
     * at android.os.Looper.loop(Looper.java:223)
     * at android.app.ActivityThread.main(ActivityThread.java:7656)
     * at java.lang.reflect.Method.invoke(Native Method)
     * at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:592)
     * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:947)
     *
     * Test running failed: Instrumentation run failed due to 'Process crashed.'
     */
    @Test
    public void clickCreateAccount() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
        solo.clickOnButton("Create Account");
        solo.waitForActivity("ProfileActivity");
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
