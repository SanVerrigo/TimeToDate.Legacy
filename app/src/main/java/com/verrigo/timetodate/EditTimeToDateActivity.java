package com.verrigo.timetodate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class EditTimeToDateActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final int REQUEST_CODE_EDIT_DESCRIPTION = 0;

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String TIME_FORMAT = "HH:mm";

    private TextView dateTextView;
    private TextView timeTextView;
    private EditText nameEditText;
    private String descriptionText = null;
    private TextView descriptionTextView;
    private TextView descriptionTitleView;
    private View setDescriptionButton;

    private final String EXTRA_HOURS = EnterDescriptionActivity.EXTRA_HOURS;
    private final String EXTRA_DESCRIPTION = EnterDescriptionActivity.EXTRA_DESCRIPTION;
    private final String EXTRA_DATE = EnterDescriptionActivity.EXTRA_DATE;
    private final String EXTRA_NAME = EnterDescriptionActivity.EXTRA_NAME;
    private final String EXTRA_ID = "extrasId";

    public Intent createIntent(Context context, String name, String date, String description, int _id) {
        Intent intent = new Intent(context, EditTimeToDateActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_ID, _id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_time_to_date);
        Intent intent = getIntent();
        final int _id = intent.getExtras().getInt(EXTRA_ID);
        descriptionText = intent.getStringExtra(EXTRA_DESCRIPTION);

        View setDateButton = findViewById(R.id.editing_choose_date_block);
        View chooseTimeBlock = findViewById(R.id.editing_choose_time_block);
        setDescriptionButton = findViewById(R.id.editing_enter_description_button);
        descriptionTextView = findViewById(R.id.editing_description_text);
        descriptionTitleView = findViewById(R.id.editing_description_title);
        dateTextView = findViewById(R.id.editing_current_date_text_view);
        timeTextView = findViewById(R.id.editing_time_text_view);
        nameEditText = findViewById(R.id.editing_name_edit_text);
        FloatingActionButton acceptButton = findViewById(R.id.editing_accept_button);

        setDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new EnterDescriptionActivity().createIntent(
                        EditTimeToDateActivity.this,
                        descriptionTextView.getText().toString()), REQUEST_CODE_EDIT_DESCRIPTION);
            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate date = getDateFromField();
                int day = date.getDayOfMonth();
                int month = date.getMonthOfYear() - 1;
                int year = date.getYear();

                Dialog picker = new DatePickerDialog(EditTimeToDateActivity.this,
                        EditTimeToDateActivity.this,
                        year, month, day);
                picker.setTitle("Выберите дату");
                picker.show();
            }
        });

        chooseTimeBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalTime time = getTimeFromField();
                int hours = time.getHourOfDay();
                int minutes = time.getMinuteOfHour();

                Dialog picker = new TimePickerDialog(EditTimeToDateActivity.this,
                        EditTimeToDateActivity.this,
                        hours,
                        minutes,
                        true);
                picker.setTitle("Выберите время");
                picker.show();

            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalDate localDate = getDateFromField();
                    int year = localDate.getYear();
                    int month = localDate.getMonthOfYear();
                    int day = localDate.getDayOfMonth();

                    LocalTime time = getTimeFromField();
                    int hours = time.getHourOfDay();
                    int mins = time.getMinuteOfHour();
                    if (hours >= 0 && hours <= 23) {
                        String editName = nameEditText.getText().toString();
                        String name = nameEditText.getText().toString();
                        StringBuilder nameBuilder = new StringBuilder(name);
                        boolean correctName = false;
                        for (int i = 0; i < editName.length(); i++) {
                            if (!(editName.charAt(i) == ' ')) {
                                correctName = true;
                                break;
                            } else {
                                nameBuilder.deleteCharAt(0);
                            }
                        }
                        name = nameBuilder.toString();
                        descriptionText = descriptionTextView.getText().toString();
                        if (name.length() <= 0 && !correctName) {
                            Toast.makeText(getApplicationContext(), "Please, enter a name", Toast.LENGTH_SHORT).show();
                        } else {
                            TimeToDateDatabaseHelper dbHelper = new TimeToDateDatabaseHelper(getApplicationContext());
                            String finalDate = String.format("%02d.%02d.%04d %02d:%02d", day, month, year, hours, mins);
                            dbHelper.changeTimeToDateWithId(_id, name, descriptionText, finalDate);
                            startActivity(new Intent(EditTimeToDateActivity.this, MainActivity.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please, enter correct hours", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Please, enter correct date", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fillIntentData();

        if (savedInstanceState != null) {
            nameEditText.setText(savedInstanceState.getString(EXTRA_NAME));
            dateTextView.setText(savedInstanceState.getString(EXTRA_DATE));
            timeTextView.setText(savedInstanceState.getString(EXTRA_HOURS));
        }
    }

    private void fillIntentData() {
        Intent intent = getIntent();
        descriptionText = intent.getStringExtra(EXTRA_DESCRIPTION);
        nameEditText.setText(intent.getStringExtra(EXTRA_NAME));
        String[] dateToCorrect = intent.getStringExtra(EXTRA_DATE).split(" ");
        String extraDate = dateToCorrect[0];
        if (TextUtils.isEmpty(extraDate)) {
            dateTextView.setText(new LocalDate().toString(DATE_FORMAT));
        } else {
            dateTextView.setText(extraDate);
        }
        timeTextView.setText(dateToCorrect[1]);
        updateDescription(descriptionText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_DESCRIPTION && resultCode == RESULT_OK) {
            descriptionText = data.getStringExtra(EnterDescriptionActivity.RESULT_DESCRIPTION);
            getIntent().putExtra(EXTRA_DESCRIPTION, descriptionText);
            updateDescription(descriptionText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_NAME, nameEditText.getText().toString());
        outState.putString(EXTRA_DATE, dateTextView.getText().toString());
        outState.putString(EXTRA_HOURS, timeTextView.getText().toString());
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        LocalDate date = new LocalDate(year, month + 1, dayOfMonth);
        dateTextView.setText(date.toString(DATE_FORMAT));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        LocalTime time = new LocalTime(hourOfDay, minute);
        timeTextView.setText(time.toString(TIME_FORMAT));
    }

    private void updateDescription(String descriptionText) {
        if (TextUtils.isEmpty(descriptionText)) {
            descriptionTitleView.setText("Добавить описание");
            descriptionTextView.setVisibility(View.GONE);
        } else {
            descriptionTitleView.setText("Описание");
            descriptionTextView.setVisibility(View.VISIBLE);
        }
        descriptionTextView.setText(descriptionText);
    }

    private LocalTime getTimeFromField() {
        String rawTime = timeTextView.getText().toString();
        return LocalTime.parse(rawTime);
    }

    private LocalDate getDateFromField() {
        String rawDate = dateTextView.getText().toString();
        DateTimeFormatter formatter = DateTimeFormat.forPattern(TimeToDate.DATE_FORMAT);
        return LocalDate.parse(rawDate, formatter);
    }
}
