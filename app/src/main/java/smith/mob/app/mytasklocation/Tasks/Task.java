package smith.mob.app.mytasklocation.Tasks;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Task implements Serializable
{
    @PrimaryKey(autoGenerate = true)
    // DECLARE an int, name it 'uid':
    public int uid;

    @ColumnInfo(name = "title")
    // DECLARE an string, name it 'title':
    public String title;

    @ColumnInfo(name = "description")
    // DECLARE an string, name it 'description':
    public String description;

    @ColumnInfo(name = "locName")
    // DECLARE an string, name it 'locName':
    public String locName;

    @ColumnInfo(name = "longitude")
    // DECLARE an string, name it 'longitude':
    public double longitude;

    @ColumnInfo(name = "latitude")
    // DECLARE an string, name it 'latitude':
    public double latitude;

    @ColumnInfo(name = "userID")
    // DECLARE an string, name it 'userID':
    public String userID;

    @ColumnInfo(name = "image")
    // DECLARE an string, name it 'image':
    public String image;

    @ColumnInfo(name = "isPrivate")
    // DECLARE an boolean, name it 'isPrivate':
    public boolean isPrivate;

    @ColumnInfo(name = "notificationActive")
    // DECLARE an boolean, name it 'notificationsActive':
    public boolean notificationActive;

    @ColumnInfo(name = "notificationRequired")
    // DECLARE an boolean, name it 'notificationsRequired':
    public boolean notificationRequired;


    // CONSTRUCTOR - Used for Room:
    public Task()
    {

    }


    // CONSTRUCTOR - Used for Firebase:
    public Task(String title, String description, String locName, double longitude, double latitude, String image, String userID)
    {
        // SET value of this.title as title:
        this.title = title;

        // SET value of this.description as description:
        this.description = description;

        // SET value of this.locName as locName:
        this.locName = locName;

        // SET value of this.longitude as longitude:
        this.longitude = longitude;

        // SET value of this.latitude as latitude:
        this.latitude = latitude;

        // SET value of this.image as image:
        this.image = image;

        // SET value of this.userID as userID:
        this.userID = userID;

        // SET value of _notificationActive to false, app launches with no active notifications:
        notificationActive = false;

        // SET value of _notificationRequired to true, set to false if user does chooses no notifications:
        notificationRequired = true;
    }
}
