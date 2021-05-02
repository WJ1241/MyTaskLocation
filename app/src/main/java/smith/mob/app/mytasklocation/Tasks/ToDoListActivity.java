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

public class ToDoListActivity extends AppCompatActivity
{
    // FIELD VARIABLES

    // DECLARE a AllTasksFragment, call it '_allTasksFragment':
    private AllTasksFragment _allTasksFragment;

    // DECLARE a CompletedTaskFragment, call it '_completedTaskFragment':
    private CompletedTaskFragment _completedTaskFragment;

    // DECLARE a UncompletedTaskFragment, call it '_uncompletedTaskFragment':
    private UncompletedTaskFragment _uncompletedTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // CALL onCreate from superclass, passing saveInstanceState as a parameter:
        super.onCreate(savedInstanceState);

        // HIDE action bar at top of app:
        getSupportActionBar().hide();

        // SET XML
        setContentView(R.layout.activity_to_do_list_layout);

        //// INSTANTIATE FRAGMENTS

        // INSTANTIATE _allTasksFragment as new AllTasksFragment():
        _allTasksFragment = new AllTasksFragment();

        // INSTANTIATE _completedTaskFragment as new CompletedTaskFragment():
        _completedTaskFragment = new CompletedTaskFragment();

        // INSTANTIATE _uncompletedTaskFragment as new UncompletedTaskFragment():
        _uncompletedTaskFragment = new UncompletedTaskFragment();

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

                                // CALL loadFragment, passing _completedTaskFragment as a parameter:
                                loadFragment(_completedTaskFragment);

                                // RETURN a value of true:
                                return true;

                            case R.id.page_private:

                                // CALL loadFragment, passing _uncompletedTaskFragment as a parameter:
                                loadFragment(_uncompletedTaskFragment);

                                // RETURN a value of true:
                                return true;
                        }

                        // RETURN a value of false;
                        return false;
                    }
                });
    }

    public void onNewTaskClicked(View view)
    {
        // Log to test button functionality
        Log.d("ToDoAPP", "onNewTaskClicked");

        // DECLARE an Intent, call it '_taskIntent', starts ToDoActivity:
        Intent _taskIntent = new Intent(this, ToDoActivity.class);
        startActivity(_taskIntent);
    }

    // Called when Return To Menu Button is clicked
    public void onReturnToMenuClick(View view)
    {
        // DECLARE an Intent, name it '_rtnToMenuIntent':
        Intent _rtnToMenuIntent  = new Intent(this, MainMenuActivity.class);

        // Call startActivity(), passing an Intent as a parameter:
        startActivity(_rtnToMenuIntent);
    }

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