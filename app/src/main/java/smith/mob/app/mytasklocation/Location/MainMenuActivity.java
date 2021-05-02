package smith.mob.app.mytasklocation.Location;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import smith.mob.app.mytasklocation.R;
import smith.mob.app.mytasklocation.Tasks.TaskListActivity;

/**
 * Class used to display Main Menu screen once a user has signed into Firebase
 */
public class MainMenuActivity extends AppCompatActivity
{
    //// FIELD VARIABLES

    // DECLARE a TextView, name it '_displayNameView':
    private TextView _displayNameView;

    // DECLARE a FirebaseUser, name it '_crrntUser':
    private FirebaseUser _crrntUser;

    //// PROTECTED METHODS

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

        // SET activity to display layout in an XML file:
        setContentView(R.layout.activity_main_menu);

        // STORE reference to current user:
        _crrntUser = FirebaseAuth.getInstance().getCurrentUser();

        // CALL setDisplayNameView():
        setDisplayNameView();
    }


    //// PUBLIC METHODS

    /**
     * METHOD: Signs User out of Firebase, used by Sign Out Button from Main Menu Screen
     * @param view: Allows use of method from exterior view object
     * @return void
     */
    public void onSignOutBttnClick(View view)
    {
        // GET instance of AuthUI:
        AuthUI.getInstance()

                // CALL signOut() to sign user out of application database:
                .signOut(this)

                // CALL addOnCompleteListener to call a method when user has been signed out:
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        // CALL goToSignInScreen() to allow user to log in again or change account:
                        goToSignInScreen();
                    }
                });
    }

    /**
     * METHOD: Opens MyLocationActivity, used by Open Map Button from Main Menu Screen
     * @param view: Allows use of method from exterior view object
     * @return void
     */
    public void onOpenMapBttnClick(View view)
    {
        // DECLARE an Intent, name it '_openMapIntent':
        Intent _openMapIntent  = new Intent(this, MyLocationActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_openMapIntent);
    }


    /**
     * METHOD: Opens ToDoListActi, used by Open Map Button from Main Menu Screen
     * @param view: Allows use of method from exterior view object
     * @return void
     */
    public void onTasksBttnClick(View view)
    {
        // DECLARE an Intent, name it '_tasksIntent':
        Intent _tasksIntent  = new Intent(this, TaskListActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_tasksIntent);
    }


    //// PRIVATE METHODS

    /**
     * METHOD: Sets Value of Display Name view to display current user's name
     * @return void
     */
    private void setDisplayNameView()
    {
        // Stores _displayNameView values as EditText:
        _displayNameView = findViewById(R.id.displayNameView);

        // SET text field to display user's name:
        _displayNameView.setText(_crrntUser.getDisplayName());
    }

    /**
     * METHOD: Opens SignInActivity, returns user to first screen to fully sign out
     * @return void
     */
    // Called when user signs out of Firebase Auth UI
    private void goToSignInScreen()
    {
        // Log to test button functionality
        Log.d("ToDoAPP", "goToSignInScreen");

        // DECLARE an Intent, name it '_signInIntent', starts ToDoActivity:
        Intent _signInIntent = new Intent(this, SignInActivity.class);
        startActivity(_signInIntent);
    }
}