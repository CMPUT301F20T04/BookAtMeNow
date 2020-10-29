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
        //solo.assertCurrentActivity("Wrong Activity", ProfileActivity.class);
        /**
         * produces this error:
         * java.lang.NullPointerException: Provided document path must not be null.
         * at com.google.firebase.firestore.util.Preconditions.checkNotNull(Preconditions.java:147)
         * at com.google.firebase.firestore.CollectionReference.document(CollectionReference.java:103)
         * at ca.ualberta.cmput301f20t04.bookatmenow.DBHandler.getUser(DBHandler.java:137)
         * at ca.ualberta.cmput301f20t04.bookatmenow.LoginActivity$1$1.onSuccess(LoginActivity.java:44)
         * at ca.ualberta.cmput301f20t04.bookatmenow.LoginActivity$1$1.onSuccess(LoginActivity.java:40)
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
