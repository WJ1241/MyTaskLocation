package smith.mob.app.mytasklocation.Location;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class StoredLocation implements Serializable
{
    //// FIELD VARIABLES

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


    // CONSTRUCTORS

    public StoredLocation()
    {
        // NOT USED, Only declared as Firebase Firestore cannot deserialise
        // StoredLocation without a no argument constructor
    }

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

    //// GET METHODS

    @PropertyName("_locName")
    public String getLocName()
    {
        // RETURN value of _locName:
        return _locName;
    }

    @PropertyName("_userID")
    public String getUserID()
    {
        // RETURN value of _userID:
        return _userID;
    }

    @PropertyName("_notificationActive")
    public boolean getNotificationActive()
    {
        // RETURN value of _notificationActive:
        return _notificationActive;
    }

    @PropertyName("_notificationRequired")
    public boolean getNotificationRequired()
    {
        // RETURN value of _notificationRequired:
        return _notificationRequired;
    }

    @PropertyName("_latitude")
    public double getLatitude()
    {
        // RETURN value of _latitude:
        return _latitude;
    }

    @PropertyName("_longitude")
    public double getLongitude()
    {
        // RETURN value of _longitude:
        return _longitude;
    }

    //// SET METHODS

    @PropertyName("_notificationActive")
    public void setNotificationActive(boolean bool)
    {
        // SET value of _notificationActive to same value as bool:
        _notificationActive = bool;
    }

    @PropertyName("_notificationRequired")
    public void setNotificationRequired(boolean bool)
    {
        // SET value of _notificationRequired to same value as bool:
        _notificationRequired = bool;
    }
}