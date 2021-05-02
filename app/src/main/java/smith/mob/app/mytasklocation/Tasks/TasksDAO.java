package smith.mob.app.mytasklocation.Tasks;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object interface for accessing Tasks stored locally in Room Database
 * @author William Smith & Christopher Bowers (Bowers 2021)
 * @version 02/05/2021
 */
@Dao
public interface TasksDAO
{
    //----------------METHODS----------------//

    /**
     * METHOD: Queried to allow access to all tasks in local Room database
     * @return LiveData<List<Task>>: ALL task objects within local Room database
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @Query("SELECT * FROM task")
    LiveData<List<Task>> getAll();

    /**
     * METHOD: Queried to allow access to private tasks in local Room database
     * @return LiveData<List<Task>>: PRIVATE task objects within local Room database
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @Query("SELECT * FROM task WHERE isPrivate = '1'")
    LiveData<List<Task>> getPrivate();

    /**
     * METHOD: Queried to allow access to public tasks in local Room database
     * @return LiveData<List<Task>>: PUBLIC task objects within local Room database
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @Query("SELECT * FROM task WHERE isPrivate = '0'")
    LiveData<List<Task>> getPublic();

    /**
     * METHOD: Adds new task to local Room database
     * @param task: Task object to be added to the local Room database
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    // ADD a task in the Database
    @Insert
    void insert(Task task);

    /**
     * METHODS: Updates Task object for if task is public or private to other users
     * @param task: Task object to be modified
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    // UPDATE when task is done
    @Update
    void updateTask(Task task);

    /**
     * METHODS: Deletes task object from local room database
     * @param task: Task object to be deleted
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    // DELETE task
    @Delete
    void deleteTask(Task task);
}
