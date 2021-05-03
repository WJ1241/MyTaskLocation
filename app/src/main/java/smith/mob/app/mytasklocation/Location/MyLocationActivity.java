package smith.mob.app.mytasklocation.Location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import smith.mob.app.mytasklocation.R;
import smith.mob.app.mytasklocation.Tasks.Task;
import smith.mob.app.mytasklocation.Tasks.TasksDB;

/**
 * Class which creates an activity to display a user's current location
 * @author William Smith, Christopher Bowers (Bowers, 2021), CodingInFlow (CodingInFlow, 2018) & Mark Ingram (Ingram, 2011)
 * @version 02/05/2021
 */
public class MyLocationActivity extends AppCompatActivity
{
    //--------------------FIELD VARIABLES--------------------//

    /// DECIMAL FORMATTER, USED FOR SHORTENING DOUBLES

    // DECLARE & INITIALISE a DecimalFormat, name it '_twoDecFormat', cap to 2 decimal places:
    private DecimalFormat _twoDecFormat = new DecimalFormat("#.##");

    /// MAP VIEW

    // DECLARE a MapView, name it '_mapView':
    private MapView _mapView;

    // DECLARE an IMapController, name it '_mapController':
    private IMapController _mapController;

    // DECLARE a GeoPoint, name it '_startPoint':
    private GeoPoint _startPoint;

    /// PERMISSIONS

    // DECLARE a boolean, name it '_permission', used for location access:
    private boolean _permission;

    // DECLARE a boolean, name it '_trackingEnabled':
    private boolean _trackingEnabled;

    // DECLARE an int, name it 'LOCATION_REQUEST_CODE':
    private int LOCATION_REQUEST_CODE;

    /// LOCATION UPDATES

    // DECLARE a long, name it '_minTimeBtwnUpdt':
    private long _minTimeBtwnUpdt;

    // DECLARE a float, name it '_minDistBtwnUpdt':
    private float _minDistBtwnUpdt;

    // DECLARE a LocationListener, name it '_locnListener':
    private LocationListener _locnListener;

    // DECLARE a LocationManager, name it '_locnManager':
    private LocationManager _locnManager;

    // DECLARE a Marker, name it '_crrntLocnMrkr':
    private Marker _crrntLocnMrkr;

    /// STORED LOCATIONS

    // DECLARE a Map<String, StoredLocation>, name it '_storedLocns':
    private Map<String, StoredLocation> _storedLocns;

    /// STORED TASKS

    // DECLARE a Map<String, Task>, name it '_storedTasks':
    private Map<String, Task> _storedTasks;

    /// NOTIFICATIONS

    // DECLARE a double, name it '_triggerDistance':
    private double _triggerDistance;

    // DECLARE a NotificationManagerCompat, name it '_notificationManager':
    private NotificationManagerCompat _notificationManager;

    // DECLARE & SET a final String, name it NOTIFICATION_KEY, and give a value of "MyLocation":
    private final String NOTIFICATION_KEY = "MyLocation";

    // DECLARE & SET a final int, name it 'NOTIFICATION_INTENT_CODE', give value of '0':
    private final int NOTIFICATION_INTENT_CODE = 0;

    /// LOCAL ROOM DATABASE

    // DECLARE a TasksDB, name it '_db':
    private TasksDB _db;


    /// FIREBASE

    // DECLARE a FirebaseFirestore, name it '_fsDb':
    private FirebaseFirestore _fsDb;

    // DECLARE a ListenerRegistration, name it '_listenerRegistrationLoc':
    private ListenerRegistration _listenerRegistrationLoc;

    // DECLARE a ListenerRegistration, name it '_listenerRegistrationTask':
    private ListenerRegistration _listenerRegistrationTask;

    // DECLARE a String, name it '_crrntUser':
    private String _crrntUser;

    //--------------------OVERRIDE METHODS--------------------//

    /**
     * METHOD: Called when class is first loaded to initialise objects
     * @param savedInstanceState: Used to restore user back to previous state in event of error when changing activity
     *
     * Learned from Worksheet 8,9,10 (Bowers, 2021)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // CALL onCreate() from superclass, passing a Bundle as a parameter:
        super.onCreate(savedInstanceState);

        // HIDE action bar at top of app:
        getSupportActionBar().hide();

        // SET User Agent Value as Package Name to allow OSMDroid to see app as being unique:
        Configuration.getInstance().setUserAgentValue(getPackageName());

        // INFLATE layouts, creates Views:
        setContentView(R.layout.activity_my_location);

        /// TRACKING

        // SET _trackingEnabled to true:
        _trackingEnabled = true;

        // SET value of LOCATION_REQUEST_CODE to 1:
        LOCATION_REQUEST_CODE = 1;

        /// MAP VIEW

        // SET value of _mapView as the 'map' view in XML:
        _mapView = findViewById(R.id.map);

        // SET type of Tiles to be used:
        _mapView.setTileSource(TileSourceFactory.MAPNIK);

        // SET Multi-Touch controls to true to allow pinch-zoom:
        _mapView.setMultiTouchControls(true);

        /// MAP CONTROL, GEO POINT

        // GET controller from MapView, store reference as IMapController:
        _mapController = _mapView.getController();

        // SET zoom level of _mapController to 8:
        _mapController.setZoom(8.0);

        // INSTANTIATE _geoPoint as new GeoPoint, passing two doubles as parameters:
        _startPoint = new GeoPoint(52.8583, -2.2944);

        // SET _mapController's centre as location of _geoPoint:
        _mapController.setCenter(_startPoint);

        /// MINIMUM VALUES BETWEEN UPDATES

        // SET value of _minTimeBtwnUpdt to 10 seconds:
        _minTimeBtwnUpdt = 10000; // needed in milliseconds

        // SET value of _minDistBtwnUpdt to 0.5 metres:
        _minDistBtwnUpdt = 0.5f; // measure in metres

        /// STORED LOCATIONS

        // INSTANTIATE _storedLocns as new HashMap<String, StoredLocation>():
        _storedLocns = new HashMap<String, StoredLocation>();

        /// STORED TASKS

        // INSTANTIATE _storedTasks as new HashMap<String, Task>():
        _storedTasks = new HashMap<String, Task>();

        //// NOTIFICATIONS

        // SET value of _triggerDistance to 500:
        _triggerDistance = 500; // measured in metres

        // INITIALISE _notificationManager, using the application's context to create the manager:
        _notificationManager = NotificationManagerCompat.from(getApplicationContext());

        // CALL createNotificationChannel(), allows notifications on newer devices:
        createNotificationChannel();

        //// LOCATION LISTENER CREATION

        // CALL createLocListener(), only needs to be called once:
        createLocListener();

        ////// LOCAL ROOM DATABASE

        // CREATE an instance of the Database:
        _db = TasksDB.getInstance(this);

        ////// FIREBASE

        //// FIREBASE FIRESTORE REFERENCE

        // GET instance of the FirebaseFirestore:
        _fsDb = FirebaseFirestore.getInstance();

        //// MAP EVENTS OVERLAY

        // CALL createMapEventsOverlay():
        createMapEventsOverlay();

        //// NOTIFY USER (HELP)

        // CREATE a small message using Toast, to notify user when they are close to a stored location:
        Toast.makeText(getApplicationContext(), "Hold Down on Location to Store!", Toast.LENGTH_LONG).show();
    }

    /**
     * METHOD: Called constantly when class is in use by an activity
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    @Override
    protected void onResume()
    {
        // CALL onResume() from superclass:
        super.onResume();

        // CALL onResume() on MapView:
        _mapView.onResume();

        // CALL startTracking():
        startTracking();

        // CALL listenForFirestoreChngLoc():
        listenForFirestoreChngLoc();

        // CALL listenForFirestoreChngTask():
        listenForFirestoreChngTask();

        // ASSIGNMENT, store unique ID of user to be used for differentiating user markers:
        _crrntUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * METHOD: Called when class is not in use by an activity
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    @Override
    protected void onPause()
    {
        // CALL onPause() from superclass:
        super.onPause();

        // CALL onPause() on MapView:
        _mapView.onPause();

        if (_permission) // IF user has given permission for location
        {
            // CALL removeUpdates() on _locnManager using _locnListener:
            _locnManager.removeUpdates(_locnListener);

            // Call remove() on _listenerRegistrationTask to stop checking for modifications when app is inactive:
            _listenerRegistrationTask.remove();

            // Call remove() on _listenerRegistrationLoc to stop checking for modifications when app is inactive:
            _listenerRegistrationLoc.remove();
        }
    }

    /**
     * METHOD: Creates new Intent to display notification to user
     * @param intent: Reference to new intent from app
     *
     * Learned from Worksheet 9 (Bowers, 2021)
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        // CALL onNewIntent() from superclass, passing an Intent as a parameter:
        super.onNewIntent(intent);

        // DECLARE a String, name it '_taskName',
        // retrieve location name from intent when notification is created:
        String _taskName = intent.getStringExtra(NOTIFICATION_KEY);

        // ITERATE through Dictionary of StoredLocation to find current location name,
        // use to make dialog to display task name:
        for (Task storedTask : _storedTasks.values())
        {
            if (storedTask.title.equals(_taskName))
            {
                // CALL showNotificationDialog(), passing a Task as a parameter:
                showNotificationDialog(storedTask);
            }
        }
    }

    /**
     * METHOD: Called to check if user has given permission to access location
     * @param requestCode: Used to make sure app has permission to access user location
     * @param permissions: Array of permissions that the app requires from user
     * @param grantResults: Array of results to let app access user's location
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == LOCATION_REQUEST_CODE) // IF requestCode matches LOCATION_REQUEST_CODE:
        {
            // IF there is a permission, AND IF the permission has been granted:
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d("MyLocation", "Permission Granted!");

                // CALL updateLocation, to request location updates:
                updateLocation();
            }
            else
            {
                Log.d("MyLocation", "Permission Denied!");

                // No functionality, as app will crash if location updates are requested when permission is denied
            }
        }
    }


    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Opens MainMenuActvity, used by Back Button from Main Menu Screen
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
     * METHOD: Starts tracking user's location
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    private void startTracking()
    {
        if (_trackingEnabled) // IF tracking of user is enabled
        {
            // CHECK _permission's state:
            _permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;

            if (_permission) // IF user has given location permission
            {
                Log.d("MyLocation", "Permission already granted!");

                // CALL updateLocation, to request location updates:
                updateLocation();
            }
            else if (!_permission) // IF user has NOT given location permission
            {
                Log.d("MyLocation", "Requesting Permission!");

                // DECLARE a String array, name it '_arrayOfPermissions', SET value to user's location permission:
                String[] _arrayOfPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                // CALL requestPermissions, passing a String array and an int as parameters:
                requestPermissions(_arrayOfPermissions, LOCATION_REQUEST_CODE);
            }
        }
    }

    /**
     * METHOD: Updates user's location on map view
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    // Called to update location of user
    @SuppressLint("MissingPermission")
    private void updateLocation()
    {
        // INITIALISE _locnManager, store LOCATION_SERVICE as a LocationManager:
        _locnManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check _locnManager so that the GPS provider is enabled,
        // including permissions, and device capability to find user's location via GPS:
        if (_locnManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            // CALL requestLocationUpdates() on LocationManager, passing GPS provider,
            // min time and dist between update, and a LocationListener as parameters:
            _locnManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, _minTimeBtwnUpdt,
            _minDistBtwnUpdt, _locnListener);
        }
    }

    /**
     * METHOD: Modifies marker location of user and removes old marker
     *
     * Learned from Worksheet 8 (Bowers, 2021)
     */
    private void createLocListener()
    {
        // INSTANTIATE _locnListener as new LocationListener():
        _locnListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location locn)
            {
                // IF current location marker is on screen:
                if (_crrntLocnMrkr != null)
                {
                    // REMOVE marker from screen:
                    _mapView.getOverlays().remove(_crrntLocnMrkr);
                }

                // CREATE a GeoPoint, name it '_currentLocation' that holds current location of device:
                GeoPoint _crrntLocn = new GeoPoint(
                        locn.getLatitude(),
                        locn.getLongitude());

                // INSTANTIATE _crrntLocnMrkr as new Marker() passing _mapView as a parameter:
                _crrntLocnMrkr = new Marker(_mapView);

                // SET marker position as the same as _crrntLocn:
                _crrntLocnMrkr.setPosition(_crrntLocn);

                // SET title of marker:
                _crrntLocnMrkr.setTitle("Current Location");

                // SET description of marker:
                _crrntLocnMrkr.setSubDescription("Longitude: " + locn.getLongitude() + ". Latitude: " + locn.getLatitude() + ".");

                // CALL invalidate() to map is redrawn with a marker:
                _mapView.invalidate();

                // ADD a marker overlay to the _mapView, using _crrntLocnMrkr:
                _mapView.getOverlays().add(_crrntLocnMrkr);

                /// NOTIFICATIONS

                // FOR loop iterating over all Task objects in _storedTasks Dictionary:
                for (Task storedTask : _storedTasks.values())
                {
                    // DECLARE & INSTANTIATE a GeoPoint, name it '_geoPoint':
                    GeoPoint _geoPoint = new GeoPoint(storedTask.latitude, storedTask.longitude);

                    // DECLARE a double, name it '_distToTask':
                    double _distToTask = _crrntLocn.distanceToAsDouble(_geoPoint);

                    // IF actual distance to task is less than the trigger distance:
                    if (_distToTask < _triggerDistance)
                    {
                        /// TOAST (MINI NOTIFICATION)

                        // CREATE a small message using Toast, to notify user when they are close to a stored task:
                        Toast.makeText(getApplicationContext(), "You are " + _twoDecFormat.format(_distToTask) + " metres from "
                                + storedTask.title, Toast.LENGTH_LONG).show();

                        /// APP NOTIFICATION

                        // IF there isn't an active notification for the Stored Location,
                        // AND IF notifications are required:
                        if (!storedTask.notificationActive && storedTask.notificationRequired)
                        {
                            // DECLARE & INITIALISE an int, name it '_notificationID',
                            // used to store unique ID, getting Hash code from task name:
                            int _notificationID = storedTask.title.hashCode();

                            // DECLARE a Notification, name it '_notification', uses Stored Task and distance:
                            Notification _notification = createNotification(storedTask, _distToTask);

                            // CALL notify() on Notification Manager, passing unique ID and notification object as parameters:
                            _notificationManager.notify(_notificationID, _notification);

                            // SET notificationActive to true:
                            storedTask.notificationActive = true; // Prevents multiple notifications being created for same location
                        }
                    }
                    else
                    {
                        // SET notificationActive to false:
                        storedTask.notificationActive = false; // User not close to location to need notification, set to inactive
                    }
                }
            }
        };
    }

    /**
     * METHOD: Creates a notification to be displayed to the user about their nearby task
     * @param storedTask: Task object which contains details such as title, description and location values
     * @param dist: Current distance of user from their task
     * @return Notification: Used for displaying an intent to the user, so that they turn task notifications on or off
     *
     * Learned from Worksheet 9 (Bowers, 2021)
     * learned how to apply Big Picture Style to a notification (CodingInFlow, 2018)
     * Learned how to convert Uri file path to a Bitmap (Ingram, 2011)
     */
    // Creates notification to alert user when close to a location
    private Notification createNotification(Task storedTask, double dist)
    {
        // DECLARE an Intent, name it '_intent', used to get current activity:
        Intent _intent = getIntent();

        // USE location name for intent, so activity recognises which location the user is near:
        _intent.putExtra(NOTIFICATION_KEY, storedTask.title);

        // DECLARE a PendingIntent, name it '_pendingIntent':
        PendingIntent _pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_INTENT_CODE,
                _intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // DECLARE a Notification, name it '_notification':
        Notification _notification;

        // DECLARE a Bitmap, name it '_bmImg':
        // Default Image if user did not take picture:
        Bitmap _bmImg = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_test_image);

        // TRY creation of Bitmap image, getBitmap() must be tried for an exception
        try
        {
            if (storedTask.image != null) // IF there is an image path associated with a task
            {
                // INITIALISE _bmImg, name it '_bmImg', store reference to storedTask's image:
                _bmImg = MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse(storedTask.image)); // Requires Local image to display location, online image is different (Ingram, 2011)
            }
        }
        catch (IOException e)
        {
            // PRINT stack trace of exception to console:
            e.printStackTrace();
        }


        // INSTANTIATE _notification as new NotificationCompat.Builder(),
        // setting details such as icons, title and description:
        _notification = new NotificationCompat.Builder(this, NOTIFICATION_KEY)
                .setSmallIcon(R.drawable.ic_baseline_task_user) // SET image used for icon
                .setContentTitle("My Task & Location Update: " + storedTask.title) // SET title for task
                .setContentText("Task Description: " + storedTask.description) // SET context for notification
                .setContentIntent(_pendingIntent) // SET ContentIntent to _pendingIntent
                .setAutoCancel(true) // SET auto cancel to true, so user can dismiss notification when interacting with notification
                .setLargeIcon(_bmImg) // SET Large Icon of notification
                .setStyle(new NotificationCompat.BigPictureStyle() // CREATE BigPictureStyle() to allow user to see picture in detail (CodingInFlow, 2018)
                        .bigPicture(_bmImg) // SET image shown when notification is enlarged
                        .bigLargeIcon(null)) // SET as null to not show image twice*/
                .build(); // CONFIRM all settings set above

        // RETURN value of _notification:
        return _notification;
    }

    /**
     * METHOD: Creates Notification channel, so that user can be alerted with a notification when near task
     *
     * Learned from Worksheet 9 (Bowers, 2021)
     */
    // Creates notification channel to alert user
    private void createNotificationChannel()
    {
        // DECLARE a String, name it '_channelName':
        String _channelName = "MyLocationChannel";

        // DECLARE an int, name it '_channelImportance':
        int _channelImportance = NotificationManager.IMPORTANCE_DEFAULT;

        // DECLARE & INSTANTIATE an NotificationChannel, name it '_channel':
        NotificationChannel _channel = new NotificationChannel(NOTIFICATION_KEY, _channelName, _channelImportance);

        // SET Description of NotificationChannel:
        _channel.setDescription("My Location Updates");

        // CREATE a NotificationChannel within _notificationManager:
        _notificationManager.createNotificationChannel(_channel);
    }

    /**
     * METHOD: Creates dialog to display to user allowing them to choose whether they would like more notifications for a task
     * @param storedTask: Task object which contains details such as title, description and location values
     *
     * Learned from Worksheet 9 (Bowers, 2021)
     */
    private void showNotificationDialog(Task storedTask)
    {
        // SET _notificationActive to false, as user has dismissed notification:
        storedTask.notificationActive = false;

        // DECLARE an AlertDialog, name it '_alertDialog':
        AlertDialog _alertDialog;

        // INSTANTIATE _alertDialog as new AlertDialog.Builder, passing this class as a parameter:
        _alertDialog = new AlertDialog.Builder(this)
                .setTitle(storedTask.title) // SET dialog title
                .setMessage("You are close to " + storedTask.title +
                        ". Would you like to continue receiving notifications " +
                        "for this Task in the future?") // SET dialog description

                // SET Positive Button with a value of 'Yes', user would like more notifications from location:
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        // SET value of notificationActive to false:
                        storedTask.notificationActive = false;

                        // SET value of _notificationRequired to true:
                        storedTask.notificationRequired = true;

                        // UPDATE location in Firebase Firestore:
                        _fsDb.collection("tasks")
                                .document(storedTask.title)
                                .set(storedTask);
                    }
                })

                // SET Positive Button with a value of 'No', user would NOT like more notifications from location:
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        // SET value of notificationActive to false:
                        storedTask.notificationActive = false;

                        // SET value of _notificationRequired to false:
                        storedTask.notificationRequired = false;

                        // UPDATE location in Firebase Firestore:
                        _fsDb.collection("tasks")
                                .document(storedTask.title)
                                .set(storedTask);
                    }
                })

        .create(); // Used to finalise AlertDialog

        _alertDialog.show(); // Display AlertDialog
    }

    /**
     * METHOD: Used to display all tasks and their locations on the map view, modifies when new task is added to Firestore
     *
     * Learned from Worksheet 10 (Bowers, 2021)
     */
    private void listenForFirestoreChngTask()
    {
        // DECLARE & INSTANTIATE a CollisionReference, name it '_collection', get reference to 'tasks' directory:
        CollectionReference _collection = _fsDb.collection("tasks");

        // DECLARE & INSTANTIATE a ListenerRegistrationTask, name it '_listenerRegistration' as a SnapshotListener to when FireBase Firestore is updated:
        _listenerRegistrationTask = _collection.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                if (error == null) // POSITIVE: No Error
                {
                    Log.d("MyTask&Location", "Tasks Collection Modified!");
                }
                else if (error != null) // NEGATIVE: Error in app
                {
                    Log.d("MyTask&Location", "Tasks Collection NOT Modified! Listener NOT working!");

                    // STOP method from processing code below:
                    return;
                }

                // ITERATE over each document snapshot passed within 'value':
                for (QueryDocumentSnapshot doc : value)
                {
                    // DECLARE & INSTANTIATE a Task, name it '_tempTask':
                    Task _tempTask = doc.toObject(Task.class);

                    if (_tempTask.userID.equals(_crrntUser) || !_tempTask.isPrivate) // IF location was created by user or is private, allows control on who sees reminder
                    {
                        // IF _storedTasks DOES NOT contain a Key of a location name:
                        if (!_storedTasks.containsKey(_tempTask.title))
                        {
                            // STORE _tempLocn in Map<String, StoredLocation>, using the name of location, and location object:
                            _storedTasks.put(_tempTask.title, _tempTask);
                        }
                    }
                }

                // ITERATE over all objects stored as Values in Key Value Pair in Map<String, StoredLocation>:
                for (Task storedTask : _storedTasks.values())
                {
                    // DECLARE & INSTANTIATE a GeoPoint, name it '_geoPoint', passing latitude and longitude as parameters:
                    GeoPoint _geoPoint = new GeoPoint(storedTask.latitude, storedTask.longitude);

                    // DECLARE & INSTANTIATE a Marker, name it '_marker', passing _mapView as a parameter:
                    Marker _marker = new Marker(_mapView);

                    // SET position of the marker to position, passing _geoPoint as a parameter:
                    _marker.setPosition(_geoPoint);

                    if (storedTask.userID.equals(_crrntUser)) // IF storedTask was created by current user
                    {
                        // SET icon of _marker to ic_location_symbol:
                        _marker.setIcon(getDrawable(R.drawable.ic_baseline_task_user));
                    }
                    else if (!storedTask.userID.equals(_crrntUser)) // IF storedTask was NOT created by current user
                    {
                        // SET icon of _marker to ic_location_symbol:
                        _marker.setIcon(getDrawable(R.drawable.ic_baseline_task_non_user));
                    }

                    // SET title of marker:
                    _marker.setTitle(storedTask.title);

                    // SET description of marker:
                    _marker.setSubDescription(storedTask.description);

                    // CALL add() on _mapView's overlay, passing _marker as a parameter:
                    _mapView.getOverlays().add(_marker);

                    // CALL invalidate() method on _mapView:
                    _mapView.invalidate();
                }
            }
        });
    }

    /**
     * METHOD: Used to display all locations on the map view, modifies when new location is added to Firestore
     *
     * Learned from Worksheet 10 (Bowers, 2021)
     */
    private void listenForFirestoreChngLoc()
    {
        // DECLARE & INSTANTIATE a CollisionReference, name it '_collection', get reference to 'locations' directory:
        CollectionReference _collection = _fsDb.collection("locations");

        // DECLARE & INSTANTIATE a ListenerRegistrationLoc, name it '_listenerRegistration' as a SnapshotListener to when FireBase Firestore is updated:
        _listenerRegistrationLoc = _collection.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                if (error == null) // POSITIVE: No Error
                {
                    Log.d("MyLocation", "Location Collection Modified!");
                }
                else if (error != null) // NEGATIVE: Error in app
                {
                    Log.d("MyLocation", "Location Collection NOT Modified! Listener NOT working!");

                    // STOP method from processing code below:
                    return;
                }

                // ITERATE over each document snapshot passed within 'value':
                for (QueryDocumentSnapshot doc : value)
                {
                    // DECLARE & INSTANTIATE a StoredLocation, name it '_tempLocn':
                    StoredLocation _tempLocn = doc.toObject(StoredLocation.class);

                    // IF _storedLocns DOES NOT contain a Key of a location name:
                    if (!_storedLocns.containsKey(_tempLocn.getLocName()))
                    {
                        // STORE _tempLocn in Map<String, StoredLocation>, using the name of location, and location object:
                        _storedLocns.put(_tempLocn.getLocName(), _tempLocn);
                    }
                }

                // ITERATE over all objects stored as Values in Key Value Pair in Map<String, StoredLocation>:
                for (StoredLocation storedLocn : _storedLocns.values())
                {
                    // DECLARE & INSTANTIATE a GeoPoint, name it '_geoPoint', passing latitude and longitude as parameters:
                    GeoPoint _geoPoint = new GeoPoint(storedLocn.getLatitude(), storedLocn.getLongitude());

                    // DECLARE & INSTANTIATE a Marker, name it '_marker', passing _mapView as a parameter:
                    Marker _marker = new Marker(_mapView);

                    // SET position of the marker to position, passing _geoPoint as a parameter:
                    _marker.setPosition(_geoPoint);

                    // SET icon of _marker to ic_location_symbol:
                    _marker.setIcon(getDrawable(R.drawable.ic_location_symbol_24));

                    // CALL add() on _mapView's overlay, passing _marker as a parameter:
                    _mapView.getOverlays().add(_marker);

                    // CALL invalidate() method on _mapView:
                    _mapView.invalidate();
                }
            }
        });
    }

    /**
     * METHOD: Used to display all tasks and their locations on the map view, modifies when new task is added to Firestore
     *
     * Learned from Worksheet 10 (Bowers, 2021)
     */
    private void createMapEventsOverlay()
    {
        // DECLARE & INSTANTIATE a MapEventsOverlay, name it '_mapEventsOverlay', listens for user touch events:
        MapEventsOverlay _mapEventsOverlay = new MapEventsOverlay(new MapEventsReceiver()
        {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p)
            {
                Log.d("MyLocation", "Tap at " + p.toString());

                // RETURN false:
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p)
            {
                Log.d("MyLocation", "Press at " + p.toString());

                // CALL addNewLocationDialog, passing 'p' as a parameter:
                addNewLocationDialog(p);

                // RETURN false:
                return false;
            }
        });

        // CALL add on _mapView Overlay, passing _mapEventsOverlay as a parameter:
        _mapView.getOverlays().add(_mapEventsOverlay);
    }

    /**
     * METHOD: Displays a dialog which allows a user to create a location to be store in the Firestore
     *
     * Learned from Worksheet 10 (Bowers, 2021)
     */
    private void addNewLocationDialog(GeoPoint geoPoint)
    {
        Log.d("MyLocation", "Long at " + geoPoint);

        // DECLARE a EditText, name it '_locnEditText':
        EditText _locnEditText = new EditText(this);

        new AlertDialog.Builder(this)

                // SET title of dialog:
                .setTitle("Create New Location")

                // SET view of Alert to _locnEditText for user input:
                .setView(_locnEditText)

                // CREATE 'POSITIVE' button:
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // DECLARE & INITIALISE a String, name it '_locnName', set value to user input from _locnEditText:
                        String _locnName = _locnEditText.getText().toString();

                        // DECLARE & INSTANTIATE a StoredLocation, name it '_newLocn', passing _locnName,
                        // geoPoint.getLatitude(), geoPoint.getLongitude() as parameters:
                        StoredLocation _newLocn = new StoredLocation(_locnName,
                                geoPoint.getLatitude(), geoPoint.getLongitude(), _crrntUser);

                        // STORE new location in Firebase Firestore:
                        _fsDb.collection("locations")
                                .document(_newLocn.getLocName())
                                .set(_newLocn);
                    }
                })

                // CREATE 'NEGATIVE' button:
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // DO NOTHING
                    }
                })

                // FINALISE creation of Alert:
                .create()

                // DISPLAY Alert to user:
                .show();
    }
}