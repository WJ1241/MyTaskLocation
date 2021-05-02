package smith.mob.app.mytasklocation.Tasks;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class},  version = 1)
public abstract class TasksDB extends RoomDatabase
{
    // Method to return a reference of TasksDAO interface:
    public abstract TasksDAO tasksDAO();

    // DECLARE & ASSIGNMENT, static final String, call it 'DB_NAME', and give value of "tasks_database_name":
    private static final String DB_NAME = "tasks_database_name";

    // DECLARE a static TasksDB, call it '_db':
    private static TasksDB _db;


    // RETURN new Database if one does not exist:
    public static TasksDB getInstance(Context context)
    {
        // If a Database instance already exists it returns the existing instance:
        if (_db == null)
        {
            // CALL buildDatabaseInstance method, create instance of Database:
            _db = buildDatabaseInstance(context);
        }

        // Return an database instance:
        return _db;
    }

    // Create instance of Database
    private static TasksDB buildDatabaseInstance(Context context)
    {
        return Room.databaseBuilder(context, TasksDB.class, DB_NAME).build();
    }

}
