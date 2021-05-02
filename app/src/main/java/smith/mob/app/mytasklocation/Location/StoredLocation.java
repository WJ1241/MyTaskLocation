package smith.mob.app.mytasklocation.Location;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

/**
 * Class which creates a location object to be stored in Firestore
 * @author William Smith & Christopher Bowers (Bowers, 2021)
 * @version 02/05/2021
 */
public class StoredLocation implements Serializable
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE a String, name it '_locName':
    private String _locName;

    // DECLARE a string, name it '_userID':
    private String _userID;

    // DECLARE a double, name it '_latitude':
    private double _latitude;

    // DECLARE a double, name it '_longitude':
    private double _longitude;

    // DECLARE a boolean, name it '_notificationActive':
    private boolean _notificationActive;

    // DECLARE a boolean, name it '_notificationRequired':
    private boolean _notificationRequired;


    //--------------------CONSTRUCTORS--------------------//

    /**
     * Constructor for objects of type StoredLocation
     */
    public StoredLocation()
    {
        // NOT USED, Only declared as Firebase Firestore cannot deserialise
        // StoredLocation without a no argument constructor
    }

    /**
     * CONSTURCTOR: Creates an object of type StoredLocation
     * @param locName: name of user's location
     * @param latitude: latitude of user's location
     * @param longitude: longitude of user's location
     * @param userID: current user's unique ID
     *
     * Learned from Worksheet 9 (Bowers, 2021)
     */
    public StoredLocation(String locName, double latitude, double longitude, String userID)
    {
        // SET value of _locName as locName:
        _locName = locName;

        // SET value of _userID as userID:
        _userID = userID;

        // SET value of _latitude as latitude:
        _latitude = latitude;

        // SET value of _longitude as longitude:
        _longitude = longitude;

        // SET value of _notificationActive to false, app launches with no active notifications:
        _notificationActive = false;

        // SET value of _notificationRequired to true, set to false if user does chooses no notifications:
        _notificationRequired = true;
    }

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Get current value of _locName and return to caller
     * @return _locName: value of _locName
     */
    @PropertyName("_locName")
    public String getLocName()
    {
        // RETURN value of _locName:
        return _locName;
    }

    /**
     * METHOD: Get current value of _userID and return to caller
     * @return _userID: value of _userID
     */
    @PropertyName("_userID")
    public String getUserID()
    {
        // RETURN value of _userID:
        return _userID;
    }

    /**
     * METHOD: Get current value of _notificationActive and return to caller
     * @return _notificationActive: value of _notificationActive
     */
    @PropertyName("_notificationActive")
    public boolean getNotificationActive()
    {
        // RETURN value of _notificationActive:
        return _notificationActive;
    }

    /**
     * METHOD: Get current value of _notificationRequired and return to caller
     * @return _notificationRequired: value of _notificationRequired
     */
    @PropertyName("_notificationRequired")
    public boolean getNotificationRequired()
    {
        // RETURN value of _notificationRequired:
        return _notificationRequired;
    }

    /**
     * METHOD: Get current value of _latitude and return to caller
     * @return _latitude: value of _latitude
     */
    @PropertyName("_latitude")
    public double getLatitude()
    {
        // RETURN value of _latitude:
        return _latitude;
    }

    /**
     * METHOD: Get current value of _longitude and return to caller
     * @return _longitude: value of_longitude
     */
    @PropertyName("_longitude")
    public double getLongitude()
    {
        // RETURN value of _longitude:
        return _longitude;
    }

    /**
     * METHOD: Set value of _notificationActive to incoming value
     * @param bool: incoming value from caller of method
     */
    @PropertyName("_notificationActive")
    public void setNotificationActive(boolean bool)
    {
        // SET value of _notificationActive to same value as bool:
        _notificationActive = bool;
    }

    /**
     * METHOD: Set value of _notificationRequired to incoming value
     * @param bool: incoming value from caller of method
     */
    @PropertyName("_notificationRequired")
    public void setNotificationRequired(boolean bool)
    {
        // SET value of _notificationRequired to same value as bool:
        _notificationRequired = bool;
    }
}