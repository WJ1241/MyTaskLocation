package smith.mob.app.mytasklocation.Tasks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import smith.mob.app.mytasklocation.Location.MainMenuActivity;
import smith.mob.app.mytasklocation.R;

/**
 * Class used to display List of Tasks that a user has created
 * @author William Smith & Christopher Bowers (Bowers, 2021)
 * @version 02/05/2021
 */
public class TaskListActivity extends AppCompatActivity
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE a AllTasksFragment, call it '_allTasksFragment':
    private AllTasksFragment _allTasksFragment;

    // DECLARE a PublicTaskFragment, call it '_publicTaskFragment':
    private PublicTaskFragment _publicTaskFragment;

    // DECLARE a UncompletedTaskFragment, call it '_privateTaskFragment':
    private PrivateTaskFragment _privateTaskFragment;


    //--------------------OVERRIDE METHODS--------------------//

    /**
     * METHOD: Called when class is first loaded to initialise objects
     * @param savedInstanceState: Used to restore user back to previous state in event of error when changing activity
     *
     * Learned from Worksheet 7 (Bowers, 2021)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // CALL onCreate from superclass, passing saveInstanceState as a parameter:
        super.onCreate(savedInstanceState);

        // HIDE action bar at top of app:
        getSupportActionBar().hide();

        // SET XML:
        setContentView(R.layout.activity_to_do_list_layout);

        //// INSTANTIATE FRAGMENTS

        // INSTANTIATE _allTasksFragment as new AllTasksFragment():
        _allTasksFragment = new AllTasksFragment();

        // INSTANTIATE _publicTaskFragment as new PublicTaskFragment():
        _publicTaskFragment = new PublicTaskFragment();

        // INSTANTIATE _privateTaskFragment as new PrivateTaskFragment():
        _privateTaskFragment = new PrivateTaskFragment();

        // CALL loadFragment, passing _allTasksFragment as a parameter:
        loadFragment(_allTasksFragment); // Used to load default fragment displaying all tasks

        //// NAVIGATION BAR

        // DECLARE a BottomNavigationView, call it '_bottomNavigationView':
        BottomNavigationView _bottomNavigationView = findViewById(R.id.bottomNavigationView);

        _bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {

                        // SWITCH state used for determining which fragment to load using resource IDs:
                        switch(item.getItemId())
                        {
                            case R.id.page_all:

                                // CALL loadFragment, passing _allTasksFragment as a parameter:
                                loadFragment(_allTasksFragment);

                                // RETURN a value of true:
                                return true;

                            case R.id.page_public:

                                // CALL loadFragment, passing _publicTaskFragment as a parameter:
                                loadFragment(_publicTaskFragment);

                                // RETURN a value of true:
                                return true;

                            case R.id.page_private:

                                // CALL loadFragment, passing _privateTaskFragment as a parameter:
                                loadFragment(_privateTaskFragment);

                                // RETURN a value of true:
                                return true;
                        }

                        // RETURN a value of false;
                        return false;
                    }
                });
    }

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Allows user to create new task, opens CreateTaskActivity
     * @param view: Allows use of method from exterior view object
     */
    public void onNewTaskClicked(View view)
    {
        // Log to test button functionality
        Log.d("ToDoAPP", "onNewTaskClicked");

        // DECLARE an Intent, call it '_taskIntent', starts ToDoActivity:
        Intent _taskIntent = new Intent(this, CreateTaskActivity.class);
        startActivity(_taskIntent);
    }

    /**
     * METHOD: Allows user to return to the main menu, opens MainMenuActivity
     * @param view: Allows use of method from exterior view object
     */
    public void onReturnToMenuClick(View view)
    {
        // DECLARE an Intent, name it '_rtnToMenuIntent':
        Intent _rtnToMenuIntent  = new Intent(this, MainMenuActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_rtnToMenuIntent);
    }

    //--------------------PRIVATE METHODS--------------------//

    /**
     * METHOD: Loads fragment object passed through Fragment parameter
     * @param fragment: holds reference to chosen Fragment class to display in list
     *
     * Learned from Worksheet 7 (Bowers, 2021)
     */
    private void loadFragment(Fragment fragment)
    {
        // DECLARE a FragmentTransaction, call it _fragTransaction, used to begin transaction:
        FragmentTransaction _fragTransaction = getSupportFragmentManager().beginTransaction();

        // REPLACE fragment using loaded fragment from parameter:
        _fragTransaction.replace(R.id.fragmentHolder, fragment);

        // CONFIRM all changes to the fragment:
        _fragTransaction.commit();
    }
}