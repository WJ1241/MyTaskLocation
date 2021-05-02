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

public class SignInActivity extends AppCompatActivity
{
    //// FIELD VARIABLES

    // DECLARE an int, name it '_signInCode':
    private final int _signInCode = 123;


    //// OVERRIDE METHODS

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

    @Override
    protected void onResume()
    {
        // CALL onResume() from superclass:
        super.onResume();

        // CALL checkUserStatus() to keep checking if user is signed in:
        checkUserStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // CALL onActivityResult from superclass, passing two ints and an Intent as parameters:
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == _signInCode) // IF requestCode
        {
            // DECLARE & ASSIGN an IdpResponse, name it '_response', give value of Intent:
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) // IF user has signed in successfully
            {
                // DECLARE a FirebaseUser, name it 'user':
                FirebaseUser _user = FirebaseAuth.getInstance().getCurrentUser();
            }
        }
    }


    //// PRIVATE METHODS

    private void checkUserStatus()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) // IF user IS signed in
        {
            // CALL goToMainActivity() to allow user to access app:
            goToMainActivity();
        }
        /*else if (FirebaseAuth.getInstance().getCurrentUser() == null) // IF user IS NOT signed in
        {
            // CALL createSignInIntent() to allow user to sign in:
            createSignInIntent();
        }*/
    }

    // Opens Firebase Auth UI to allow user to sign in
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

    public void goToMainActivity()
    {
        // Log to test button functionality
        Log.d("ToDoAPP", "goToMainActivity");

        // DECLARE an Intent, name it '_mainActIntent', starts MainMenuActivity:
        Intent _mainActIntent = new Intent(this, MainMenuActivity.class);
        startActivity(_mainActIntent);
    }
}