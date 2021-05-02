package smith.mob.app.mytasklocation.Tasks;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import smith.mob.app.mytasklocation.R;

/**
 * Class which allows modification to tasks stored in local Room Database
 * @author William Smith, Christopher Bowers (Bowers, 2021) & Google (Google, 2021)
 * @version 02/05/2021
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder>
{
    //--------------------FIELD VARIABLES--------------------//

    // DECLARE a reference to the Task class as a List, call it 'tasks'
    private List<Task> _tasks;

    // DECLARE a TasksDB, call it 'db':
    private TasksDB _db;

    /// FIREBASE

    // DECLARE a FirebaseFirestore, name it '_fsDb':
    private FirebaseFirestore _fsDb;

    // DECLARE a String, name it '_crrntUser':
    private String _crrntUser;


    //--------------------VIEWHOLDER CLASS--------------------//

    /**
     * Class which holds refernce to views in recycler view in XML
     * @author William Smith, Christopher Bowers (Bowers, 2021)
     * @version 02/05/2021
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // DECLARE text, image and checkbox views:
        TextView _titleView;
        TextView _descView;
        TextView _locnView;
        ImageView _imageView;
        CheckBox _checkBox;

        //---------------VIEWHOLDER CONSTRUCTOR--------------//

        /**
         * Constructor for objects of type ViewHolder
         * @param itemView: Holds reference to exterior XML view objects
         */
        public ViewHolder(@NonNull View itemView)
        {
            // Pass View as a parameter through superclass constructor:
            super(itemView);

            // ASSIGN values to text and image views, and get reference to elements within 'task_layout' XML:
            _titleView = itemView.findViewById(R.id.taskTitleDisplay);
            _descView = itemView.findViewById(R.id.taskDescDisplay);
            _locnView = itemView.findViewById(R.id.taskLocnDisplay);
            _imageView = itemView.findViewById(R.id.taskImageDisplay);
            _checkBox = itemView.findViewById(R.id.taskPrivateCheckBox);

            // GET instance of Firebase Firestore:
            _fsDb = FirebaseFirestore.getInstance();

            // GET Unique id of user:
            _crrntUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
    }

    //--------------------PUBLIC METHODS--------------------//

    /**
     * METHOD: Sets Task list in Adapter class to same list in Room Database
     * @param tasks: holds reference to list of all tasks
     *
     *  Learned from Worksheet 6 (Bowers, 2021)
     */
    public void setTaskList(List<Task> tasks)
    {
        // SET value of _tasks to tasks:
        _tasks = tasks;

        // CALL notifyDataSetChanged():
        notifyDataSetChanged();
    }

    /**
     * METHOD: Sets instance of Database in Adapter class to TasksDB object
     * @param db: holds reference to TasksDB object
     *
     *  Learned from Worksheet 6 (Bowers, 2021)
     */
    public void setDatabase(TasksDB db)
    {
        // SET value of _db to db:
        _db = db;

        // CALL notifyDataSetChanged():
        notifyDataSetChanged();
    }

    /**
     * METHOD: Deletes task from local Room Database and Firestore
     * @param position: position of current task listed in Fragment view
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     * Learned from Firebase Documentation (Google, 2021)
     */
    public void deleteTask(int position)
    {
        // DECLARE a final Task, call it '_task', used to get specified location in task list:
        final Task _task = _tasks.get(position);

        // REMOVE task from Database
        // Separate Thread from UI thread, to allow multi-tasking
        Executor _executor = Executors.newSingleThreadExecutor();
        _executor.execute(new Runnable()
        {
           @Override
           public void run()
           {
               if (_task.userID.equals(_crrntUser)) // IF task User ID matches crrnt user, prevents other users from deleting task
               {
                   // CALL delete method from TasksDAO interface:
                   _db.tasksDAO().deleteTask(_task);

                   // CALL delete method from Firebase Firestore on selected task (Google, 2021):
                   _fsDb.collection("tasks").document(_task.title + "_" + _crrntUser)
                           .delete()
                           .addOnSuccessListener(new OnSuccessListener<Void>()
                           {
                               @Override
                               public void onSuccess(Void aVoid)
                               {
                                   Log.d("MyTask&Location", "DocumentSnapshot successfully deleted!");
                               }
                           })
                           .addOnFailureListener(new OnFailureListener()
                           {
                               @Override
                               public void onFailure(@NonNull Exception e)
                               {
                                   Log.w("MyTask&Location", "Error deleting document");
                               }
                           });
               }
           }
        });


    }

    //--------------------OVERRIDE METHODS--------------------//

    /**
     * METHOD: Inflates a layout to be used in a ViewHolder and returns to caller
     * @param parent: parent of all of views in a group
     * @param viewType: The type of view being passed using unique ID
     * @return View: Inflated layout to be used to hold tasks
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // DECLARE & INSTANTIATE a LayoutInflater, call it '_layoutInflater':
        LayoutInflater _layoutInflater = LayoutInflater.from(parent.getContext());

        // DECLARE a View, call it '_view':
        View _view = _layoutInflater.inflate(R.layout.task_layout, parent, false);

        // RETURN instance of ViewHolder, passing the new inflated 'view' as a parameter:
        return new ViewHolder(_view);
    }

    /**
     * METHOD: Updates task in local Room Database and Firestore
     * @param holder: identification of which ViewhHolder object is being affected
     * @param position: position of task on screen to be modified
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // DECLARE a final Task, call it '_task', get position from parameter:
        final Task _task = _tasks.get(position);

        // SET text image, and checkbox views, with values from 'Task' Database:
        holder._titleView.setText(_task.title);
        holder._descView.setText(_task.description);
        holder._locnView.setText(_task.locName);
        holder._imageView.setImageURI(Uri.parse(_task.image));
        holder._checkBox.setChecked(_task.isPrivate);

        // ADD a listener for modifications to the CheckBox
        holder._checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // UPDATE the task
                _task.isPrivate = isChecked;

                // MODIFY task in Database
                // Put on a separate thread, to allow multi-tasking
                Executor _myExecutor = Executors.newSingleThreadExecutor();

                _myExecutor.execute(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (_task.userID.equals(_crrntUser)) // IF task User ID matches crrnt user, prevents other users from deleting task
                        {
                            // CALL update method from TasksDAO interface:
                            _db.tasksDAO().updateTask(_task);

                            // UPDATE location in Firebase Firestore:
                            _fsDb.collection("tasks")
                                    .document(_task.title + "_" + _crrntUser)
                                    .set(_task);
                        }
                    }
                });
            }
        });
    }

    /**
     * METHOD: Returns a value of how many tasks are stored locally in Room database
     * @return _tasks.size(): returns how many task objects are in Room database list
     *
     * Learned from Worksheet 6 (Bowers, 2021)
     */
    @Override
    public int getItemCount()
    {
        // IF there are no tasks within the _tasks list:
        if(_tasks == null)
        {
            // RETURN a value of 0:
            return 0;
        }

        // RETURN how many indices that have values within the list:
        return _tasks.size();
    }
}
