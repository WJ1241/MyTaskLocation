package smith.mob.app.mytasklocation.Tasks;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import smith.mob.app.mytasklocation.Location.MyLocationActivity;
import smith.mob.app.mytasklocation.Location.StoredLocation;
import smith.mob.app.mytasklocation.R;

/**
 * Class used to display Task Creation screen
 * @author William Smith, Christopher Bowers (Bowers, 2021), George John (John, 2018) & Google (Google, 2021)
 * @version 02/05/2021
 */
public class CreateTaskActivity extends AppCompatActivity
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE EditText variables to access views in XML:
    private EditText _titleView;
    private EditText _descView;

    // DECLARE a Spinner, name it '_spinner':
    private Spinner _spinner;

    // DECLARE an ImageView, name it '_taskImageView':
    private ImageView _taskImageView;

    // DECLARE a final int, name it 'REQUEST_IMAGE_CAPTURE', unique code to identify results from camera intent:
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    // DECLARE a Uri and name it 'imageUri':
    private Uri _imageUri;

    // DECLARE an HashMap<String, StoredLocation>, name it '_storedLocns':
    private HashMap<String, StoredLocation> _storedLocns;

    /// FIREBASE

    // DECLARE a FirebaseFirestore, name it '_fsDb':
    private FirebaseFirestore _fsDb;

    // DECLARE a List<String>, name it '_spinnerList':
    private List<String> _spinnerList;

    // DECLARE a ListenerRegistration, name it '_listenerRegistration':
    private ListenerRegistration _listenerRegistration;

    // DECLARE a String, name it '_crrntUser':
    private String _crrntUser;

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

        // SET activity to display layout in an XML file:
        setContentView(R.layout.activity_todo);

        // CALL getViews():
        getViews();

        // INSTANTIATE _storedLocns as new HashMap<String, StoredLocation>():
        _storedLocns = new HashMap<String, StoredLocation>();

        // INSTANTIATE _spinnerList as new ArrayList<String>():
        _spinnerList = new ArrayList<String>();

        //// FIREBASE FIRESTORE REFERENCE

        // GET instance of the FirebaseFirestore:
        _fsDb = FirebaseFirestore.getInstance();

        // CALL getLocations(), to initially set up spinner:
        getLocations();
    }

    /**
     * METHOD: Called to get result of camera activity
     * @param requestCode: code for user's given permission
     * @param resultCode: code to know if user has given permission
     * @param data: Intent from camera activity
     *
     * Learned from Worksheet 5 (Bowers, 2021)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // IF request was Camera Intent request, and if result was OK:
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            // SET taskImageView source to image URL:
            _taskImageView.setImageURI(_imageUri);
        }
    }

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Saves created task within local Room database and Firestore
     * @param view: Allows use of method from exterior view object
     *
     * Learned from Worksheets 2,3,4,5 (Bowers, 2021)
     */
    // Used when Save buttons are clicked:
    public void onSaveClick(View view) {
        Log.d("ToDoApp", "onSaveClick");

        //-----------------Store_EditText-----------------//

        // Stores View values in Strings:
        String _title = _titleView.getText().toString();
        String _description = _descView.getText().toString();
        String _locName = _storedLocns.get(_spinner.getSelectedItem().toString()).getLocName();
        double _longitude = _storedLocns.get(_spinner.getSelectedItem().toString()).getLongitude();
        double _latitude = _storedLocns.get(_spinner.getSelectedItem().toString()).getLatitude();

        // DECLARE a string, name it '_image', requires @Nullable tag to display default images from resource package, app crashes when giving resource path to database:
        @Nullable String _image = _imageUri.toString();

        //-------------------Firebase---------------------//

        // STORE reference to userID to keep task private:
        _crrntUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //--------------------Database--------------------//

        // CREATE an instance of the Database:
        TasksDB _db = TasksDB.getInstance(this);

        //-------------------CreateTask------------------//

        // CREATE instance of Task
        final Task _task = new Task();
        _task.title = _title;
        _task.description = _description;
        _task.locName = _locName;
        _task.image = _image;
        _task.longitude = _longitude;
        _task.latitude = _latitude;
        _task.userID = _crrntUser;
        _task.isPrivate = false;

        // CREATE separate thread to insert data into Database:
        Executor _executor = Executors.newSingleThreadExecutor();
        _executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                // add the task in the Database
                _db.tasksDAO().insert(_task);
            }
        });

        //------------------RetrieveTask------------------//

        // DECLARE a LiveData List of Task, name it 'tasks', gets all tasks inserted in Database:
        LiveData<List<Task>> tasks = _db.tasksDAO().getAll();

        // OBSERVE LiveData object, wait for change:
        tasks.observe(this, new Observer<List<Task>>()
        {
            @Override
            public void onChanged(List<Task> pTasks)
            {
                // Iterate over list
                for (Task task : pTasks)
                {
                    // Log title and description to console:
                    Log.d("ToDoApp", task.title + " : " + task.description);
                }
            }
        });

        //----------------FirebaseStorage----------------//

        // DECLARE & ASSIGN a String, name it '_onlineImgPath', give default image online, prevents users from accessing private images:
        String _onlineImgPath = "android.resource://smith.mob.app.mytasklocation/2131165344";

        // DECLARE a Task, name it '_fbTask', used to store task details in Firebase:
        Task _fbTask = new Task(_task.title, _task.description, _task.locName, _task.longitude, _task.latitude, _onlineImgPath, _crrntUser);

        _fsDb.collection("tasks")
                .document(_task.title + "_" + _crrntUser)
                .set(_fbTask);


        // RETURN back to previous task screen:
        finish();
    }

    /**
     * METHOD: Allows user to take picture and return value back to app to use within task creation
     * @param view: Allows use of method from exterior view object
     *
     * Learned from Worksheet 5 (Bowers, 2021)
     */
    // Used when camera elements are interacted with
    public void onCameraClick(View view)
    {
        Log.d("ToDoApp", "onCameraClick");

        // CREATE unique file name using the current time
        String _imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";

        // CREATE new file in the application file folder
        File _imageFile = new File(getFilesDir(), _imageFileName);

        // GET a URI for the file using a file provider
        _imageUri = FileProvider.getUriForFile(this, "imgprovider", _imageFile);

        // DECLARE an Intent, name it 'takePictureIntent':
        Intent _takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // LINK _imageUri to image captured by camera application:
        _takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, _imageUri);

        // CALL startActivityForResult, passing an Intent and an int as a parameter, activates camera application
        startActivityForResult(_takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    /**
     * METHOD: Opens MyLocationActivity, used by Create Location Button from CreateTaskActivity
     * @param view: Allows use of method from exterior view object
     */
    // Called when Open Map button is clicked
    public void onOpenMapBttnClick(View view)
    {
        // DECLARE an Intent, name it '_openMapIntent':
        Intent _openMapIntent  = new Intent(this, MyLocationActivity.class);

        // CALL startActivity(), passing an Intent as a parameter:
        startActivity(_openMapIntent);
    }

    //--------------------PRIVATE METHODS--------------------//

    /**
     * METHOD: Gets all views within current activity, stores them for modification:
     */
    private void getViews()
    {
        //--------------------EditText--------------------//

        // STORE View values as EditText:
        _titleView = findViewById(R.id.taskTitleView);
        _descView = findViewById(R.id.taskDescriptionView);

        // STORE View value as Spinner:
        _spinner = findViewById(R.id.taskLocationDropDownView);

        // STORE View value as ImageView:
        _taskImageView = findViewById(R.id.taskImageView);

        // ASSIGNMENT, get resource of default logo used before user created image:
        _imageUri = Uri.parse("android.resource://smith.mob.app.mytasklocation/" + R.drawable.ic_launcher_foreground);

        // SET default image for user if they do not take picture:
        _taskImageView.setImageURI(_imageUri);
    }

    /**
     * METHOD: Updates values in Spinner view in activity XML
     *
     * Learned how to create Array Adapters and use them to list items in a spinner (John, 2018)
     */
    private void spinnerValueUpdate()
    {
        for (String locnName : _storedLocns.keySet()) // FOR every location name in _storedLocns keys
        {
            // ADD locnName to _spinnerList:
            _spinnerList.add(locnName);

            Log.d("Location Displayed", locnName);
        }

        // DECLARE an ArrayAdapter<String>, name it '_stringAdapter', passing this class for context, set layout, and a list as parameters:
        ArrayAdapter<String> _stringAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _spinnerList);

        // SET drop down layout of individual items:
        _stringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // SET adapter of _spinner with _stringAdapter:
        _spinner.setAdapter(_stringAdapter);
    }

    /**
     * METHOD: Gets documents from 'locations' collection in Firestore, stores them locally to use in task spinner
     *
     * Learned from Firebase Documentation (Google, 2021)
     */
    private void getLocations()
    {
        // GET collection 'locations' from Firestore:
        _fsDb.collection("locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful()) // IF there are objects within collection 'locations':
                        {
                            for(QueryDocumentSnapshot doc : task.getResult()) // FOR every document within 'locations' collection
                            {
                                // DECLARE & ASSIGN a StoredLocation, name it '_tempLocn', store as StoredLocation:
                                StoredLocation _tempLocn = doc.toObject(StoredLocation.class);

                                // IF _storedLocns DOES NOT contain a Key of a location name:
                                if (!_storedLocns.containsKey(_tempLocn.getLocName()))
                                {
                                    // STORE _tempLocn in Map<String, StoredLocation>, using the name of location, and location object:
                                    _storedLocns.put(_tempLocn.getLocName(), _tempLocn);
                                }

                                Log.d("getLocations", "Documents Collected!");
                            }
                        }
                        else
                        {
                            Log.d("getLocations", "ERROR: Failed to get Documents!");
                        }

                        // CALL spinnerValueUpdate(), to update values in spinner:
                        spinnerValueUpdate();
                    }
                });
    }
}