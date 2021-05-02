package smith.mob.app.mytasklocation.Tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import smith.mob.app.mytasklocation.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UncompletedTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UncompletedTaskFragment extends Fragment {

    //// CUSTOM CODE

    //// FIELD VARIABLES

    // DECLARE a RecyclerView, call it '_recyclerView':
    RecyclerView _recyclerView;

    // DECLARE a TaskListAdapter, call it '_taskListAdapter':
    TaskListAdapter _taskListAdapter;

    // DECLARE a TaskDB, call it '_db':
    TasksDB _db;


    //// OVERRIDE METHODS

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // CALL onViewCreated from superclass, passing view and savedInstanceState as parameters:
        super.onViewCreated(view, savedInstanceState);

        // GET Instance of Database:
        _db = TasksDB.getInstance(view.getContext());

        // BIND RecyclerView:
        _recyclerView = view.findViewById(R.id.taskListRecyclerView);

        // SET RecyclerView layout as new LinearLayoutManager:
        _recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // INSTANTIATE _taskListAdapter as new TaskListAdapter:
        _taskListAdapter = new TaskListAdapter();

        // SET _recyclerView adapter as _taskListAdapter:
        _recyclerView.setAdapter(_taskListAdapter);

        // DECLARE & INSTANTIATE an ItemTouchHelper.SimpleCallback, call it '_touchHelperCallback':
        ItemTouchHelper.SimpleCallback _touchHelperCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
                {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target)
                    {
                        // RETURN a value of false:
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
                    {
                        // DECLARE an integer, call it '_swipedPosition':
                        int _swipedPosition = viewHolder.getAdapterPosition();

                        // CALL method from TaskListAdapter which will delete class using the position where the user swipes the screen:
                        _taskListAdapter.deleteTask(_swipedPosition);
                    }
                };

        // DECLARE & INSTANTIATE an ItemTouchHelper, passing as _touchHelperCallback as a parameter, call it '_itemTouchHelper':
        ItemTouchHelper _itemTouchHelper = new ItemTouchHelper(_touchHelperCallback);

        // CALL attachToRecyclerView() to attach the touch helper object to the XML Recycler View:
        _itemTouchHelper.attachToRecyclerView(_recyclerView);
    }


    @Override
    public void onResume()
    {
        super.onResume();

        // Retrieves UNCOMPLETED tasks stored in Database
        LiveData<List<Task>> _tasks = _db.tasksDAO().getPrivate();

        // OBSERVE the LiveData object, wait for modification
        _tasks.observe(this, new androidx.lifecycle.Observer<List<Task>>()
        {
            @Override
            public void onChanged(List<Task> tasks)
            {
                // CALL method from TaskListAdapter, and pass _db as a parameter:
                _taskListAdapter.setDatabase(_db);

                // CALL method from TaskListAdapter, and pass tasks as a parameter:
                _taskListAdapter.setTaskList(tasks);
            }
        });
    }



    //// BOILER PLATE CODE

    //// FIELD VARIABLES


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UncompletedTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UncompletedTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UncompletedTaskFragment newInstance(String param1, String param2) {
        UncompletedTaskFragment fragment = new UncompletedTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uncompleted_task, container, false);
    }
}