package com.matthewwen.todoplanner.ui.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.matthewwen.todoplanner.ApiRequest;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.ui.dialog.dialogPicker.DatePickerFragment;
import com.matthewwen.todoplanner.ui.dialog.dialogPicker.TimePickerFragment;

import java.util.Objects;

public class NewSectionDialog extends DialogFragment {

    private Toolbar toolbar;

    private static final String TAG = NewSectionDialog.class.getName();

    public static void display(FragmentManager fragmentManager) {
        NewSectionDialog dialog = new NewSectionDialog();
        dialog.show(fragmentManager, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        assert dialog != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.dialog_newsection, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.check);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.check_menu) {
                    final TextInputEditText name = view.findViewById(R.id.section_name_et);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ApiRequest.create_section(getContext(), Objects.requireNonNull(name.getText()).toString(), -1);
                        }
                    }).start();
                    dismiss();
                }
                return false;
            }
        });
        MaterialButton dateButton = view.findViewById(R.id.date_picker);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getParentFragmentManager(), "datePicker");
            }
        });
        MaterialButton timeButton = view.findViewById(R.id.time_picker);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getParentFragmentManager(), "timePicker");
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.setTitle("Creating Section");
    }


}
