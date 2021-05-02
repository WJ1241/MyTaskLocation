package smith.mob.app.mytasklocation.Tasks;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Class which stores Task objects locally on user's device
 * @author William Smith & Christopher Bowers (Bowers, 2021)
 * @version 02/05/2021
 */
@Database(entities = {Task.class},  version = 1)
public abstract class TasksDB extends RoomDatabase
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE & ASSIGNMENT, static final String, call it 'DB_NAME', and give value of "tasks_database_name":
    private static final String DB_NAME = "tasks_database_name";

    // DECLARE a static TasksDB, call it '_db':
    private static TasksDB _db;

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: returns a reference to a TasksDAO object
     * @return TasksDAO: reference to Tasks Data Access Object
     *
     * Learned from Worksheet 3 (Bowers, 2021)
     */
    public abstract TasksDAO tasksDAO();

    /**
     * METHOD: returns a reference to a TasksDB object if one DOES NOT exist already
     * @return TasksDB: reference to Tasks Room Database object
     *
     * Learned from Worksheet 3 (Bowers, 2021)
     */
    public static TasksDB getInstance(Context context)
    {
        // IF a Database instance already exists it returns the existing instance:
        if (_db == null)
        {
            // CALL buildDatabaseInstance method, create instance of Database:
            _db = buildDatabaseInstance(context);
        }

        // RETURN an database instance:
        return _db;
    }

    /**
     * METHOD: Builds instance of database in user's local storage
     * @return TasksDB: reference to Tasks Room Database object
     *
     * Learned from Worksheet 3 (Bowers, 2021)
     */
    private static TasksDB buildDatabaseInstance(Context context)
    {
        // RETURN an instance of TasksDB as a Room database:
        return Room.databaseBuilder(context, TasksDB.class, DB_NAME).build();
    }

}
