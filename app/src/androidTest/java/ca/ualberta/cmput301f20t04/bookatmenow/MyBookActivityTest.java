package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.widget.Button;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MyBookActivityTest {
    private Solo solo;

    /**
     * Test class for MainActivity. Robotium test framework is used.
     */
    @Rule
    public ActivityTestRule<MyBookActivity> rule =
            new ActivityTestRule<MyBookActivity>(MyBookActivity.class, true, true);

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
    public void clickScan() {
        solo.assertCurrentActivity("MyBookActivity", MyBookActivity.class);
        solo.clickOnButton("Scan In");
        solo.assertCurrentActivity("ScanBook", ScanBook.class);
    }

    @Test
    public void clickCancel() {
        solo.assertCurrentActivity("MyBookActivity", MyBookActivity.class);
        solo.clickOnButton("CANCEL");
    }

    // require owner view
    @Test
    public void clickRemove() {}
    @Test
    public void clickSave() {}
    @Test
    public void clickPending() {}

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
