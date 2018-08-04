package com.verrigo.timetodate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Verrigo on 29.07.2018.
 */

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    TextView dateTextView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dateTextView = getActivity().findViewById(R.id.current_date_text_view);
//        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);

        /* Это работает, так как в CreateTimeToDateActivity мы ставим дату изначально из календаря */

        String[] data = dateTextView.getText().toString().split("-");
        int day = Integer.parseInt(data[0]);
        int month = Integer.parseInt(data[1]) - 1;
        int year = Integer.parseInt(data[2]);

        Dialog picker = new DatePickerDialog(getActivity(), this, year, month, day);
        picker.setTitle("Выберите дату");
        return picker;

    }

    @Override
    public void onStart() {
        super.onStart();
        Button button = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
        button.setText("Готово");
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        dateTextView.setText(String.format("%s-%s-%s", dayOfMonth, month + 1, year));
    }
}
