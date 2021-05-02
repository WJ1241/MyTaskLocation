package smith.mob.app.mytasklocation.Tasks;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TasksDAO
{
    // RETRIEVE a list of all tasks
    @Query("SELECT * FROM task")
    LiveData<List<Task>> getAll();

    // RETRIEVE a list of completed tasks
    @Query("SELECT * FROM task WHERE isPrivate = '1'")
    LiveData<List<Task>> getPrivate();

    // RETRIEVE a list of uncompleted tasks
    @Query("SELECT * FROM task WHERE isPrivate = '0'")
    LiveData<List<Task>> getPublic();

    // ADD a task in the Database
    @Insert
    void insert(Task task);

    // UPDATE when task is done
    @Update
    void updateTask(Task task);

    // DELETE task
    @Delete
    void deleteTask(Task task);
}
