package com.matthewwen.todoplanner.ui.create;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.matthewwen.todoplanner.ApiRequest;
import com.matthewwen.todoplanner.PhoneDatabase;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.object.TodoTasks;
import com.matthewwen.todoplanner.rv.MenuAdapter;
import com.matthewwen.todoplanner.ui.dialog.NewSectionDialog;
import com.matthewwen.todoplanner.ui.dialog.dialogPicker.DatePickerFragment;
import com.matthewwen.todoplanner.ui.dialog.dialogPicker.TimePickerFragment;

import java.util.ArrayList;
import java.util.Objects;

public class CreateFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateViewModel createViewModel = ViewModelProviders.of(this).get(CreateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_create, container, false);

        Button button = root.findViewById(R.id.create_section_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("PrivateResource")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    NewSectionDialog.display(getFragmentManager());
                }
            }
        });

        MaterialToolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.check);

        final TextInputEditText editText = root.findViewById(R.id.name_et);

        final AutoCompleteTextView editTextFilledExposedDropdown = root.findViewById(R.id.filled_exposed_dropdown);
        final MenuAdapter myAdapter = new MenuAdapter(Objects.requireNonNull(getContext()),
                R.layout.dropdown_menu_section);
        editTextFilledExposedDropdown.setAdapter(myAdapter);

        editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAdapter.setId(position);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.check_menu) {
                    String name = Objects.requireNonNull(editText.getText()).toString();
                    final TodoTasks newTodo = new TodoTasks(-1, name, 0,0,myAdapter.id);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ApiRequest.create_task(getContext(), newTodo);
                        }
                    }).start();
                    PhoneDatabase.insertTask(null, newTodo);
                    editText.setText("");
                    Toast.makeText(getContext(), "Created Task", Toast.LENGTH_LONG);
                }
                return false;
            }
        });

        new AsyncTask<Void, Void, ArrayList<Section>>() {
            @Override
            public ArrayList<Section> doInBackground(Void... voids) {
                return ApiRequest.get_section(getContext());
            }
            @Override
            protected void onPostExecute(ArrayList<Section> Sections) {
                super.onPostExecute(Sections);
                myAdapter.setList(Sections);
            }
        }.execute();

        MaterialButton dueTimeButton = root.findViewById(R.id.time_picker);
        dueTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getParentFragmentManager(), "timePicker");
            }
        });

        MaterialButton dueDateButton = root.findViewById(R.id.date_picker);
        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getParentFragmentManager(), "datePicker");
            }
        });

        createViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        return root;
    }
}
