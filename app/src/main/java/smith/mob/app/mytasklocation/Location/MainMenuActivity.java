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
import smith.mob.app.mytasklocation.Tasks.ToDoListActivity;

public class MainMenuActivity extends AppCompatActivity {

    //// FIELD VARIABLES

    // DECLARE a TextView, name it '_displayNameView':
    private TextView _displayNameView;

    // DECLARE a FirebaseUser, name it '_crrntUser':
    private FirebaseUser _crrntUser;

    //// PROTECTED METHODS

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

    // Called when Open Map button is clicked
    public void onOpenMapBttnClick(View view)
    {
        // DECLARE an Intent, name it '_openMapIntent':
        Intent _openMapIntent  = new Intent(this, MyLocationActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_openMapIntent);
    }

    // Called when Tasks button is clicked
    public void onTasksBttnClick(View view)
    {
        // DECLARE an Intent, name it '_tasksIntent':
        Intent _tasksIntent  = new Intent(this, ToDoListActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_tasksIntent);
    }


    //// PRIVATE METHODS

    // Gets all views within current activity, stores them for modification:
    private void setDisplayNameView()
    {
        // Stores _displayNameView values as EditText:
        _displayNameView = findViewById(R.id.displayNameView);

        // SET text field to display user's name:
        _displayNameView.setText(_crrntUser.getDisplayName());
    }

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