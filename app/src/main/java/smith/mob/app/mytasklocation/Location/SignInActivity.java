package smith.mob.app.mytasklocation.Location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import smith.mob.app.mytasklocation.R;

/**
 * Class used to display Main Menu screen once a user has signed into Firebase
 * @author William Smith & Google (Google, 2021)
 * @version 02/05/2021
 */
public class SignInActivity extends AppCompatActivity
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE an int, name it '_signInCode':
    private final int _signInCode = 123;


    //--------------------OVERRIDE METHODS--------------------//

    /**
     * METHOD: Called when class is first loaded to initialise objects
     * @param savedInstanceState: Used to restore user back to previous state in event of error when changing activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // CALL onCreate() from superclass, passing a Bundle as a parameter:
        super.onCreate(savedInstanceState);

        // HIDE action bar at top of app:
        getSupportActionBar().hide();

        // INFLATE layouts, creates Views:
        setContentView(R.layout.activity_sign_in);
    }

    /**
     * METHOD: Called constantly when class is in use by an activity
     */
    @Override
    protected void onResume()
    {
        // CALL onResume() from superclass:
        super.onResume();

        // CALL checkUserStatus() to keep checking if user is signed in:
        checkUserStatus();
    }

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Opens Firebase UI sign in, allows user to sign in into Firestore
     * @param view: Allows use of method from exterior view object
     *
     * Learned from Firebase Documentation (Google, 2021)
     */
    public void createSignInIntent(View view)
    {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) // IF user IS NOT signed in, double check to make sure
        {
            // DECLARE a List<AuthUI.IdpConfig>, name it '_providers':
            List<AuthUI.IdpConfig> _providers = Arrays.asList(
                    // BUILD Config for Email Sign in:
                    new AuthUI.IdpConfig.EmailBuilder().build());

            // CALL startActivityForResult() to launch sign-in intent, passing an instance of AuthUI and _signInCode as parameters:
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(_providers)
                            .setTheme(R.style.Theme_MyTaskLocation)
                            .setIsSmartLockEnabled(false)
                            .build(),
                    _signInCode);
        }
    }

    //--------------------PRIVATE METHODS--------------------//

    /**
     * METHOD: Called to check if User has signed into Firebase, if they have change activity
     */
    private void checkUserStatus()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) // IF user IS signed in
        {
            // CALL goToMainActivity() to allow user to access app:
            goToMainActivity();
        }
    }

    /**
     * METHOD: Opens MainMenuActivity to allow user to access app features
     */
    public void goToMainActivity()
    {
        // Log to test button functionality
        Log.d("ToDoAPP", "goToMainActivity");

        // DECLARE an Intent, name it '_mainActIntent', starts MainMenuActivity:
        Intent _mainActIntent = new Intent(this, MainMenuActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_mainActIntent);
    }
}