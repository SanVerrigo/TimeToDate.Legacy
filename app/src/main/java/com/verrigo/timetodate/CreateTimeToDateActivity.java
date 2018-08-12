package com.verrigo.timetodate;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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

public class CreateTimeToDateActivity extends AppCompatActivity implements
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

    public Intent createIntent(Context context) {
        Intent intent = new Intent(context, CreateTimeToDateActivity.class);
        return intent;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_time_to_date);

        View setDateButton = findViewById(R.id.choose_date_block);
        View chooseTimeBlock = findViewById(R.id.choose_time_block);
        setDescriptionButton = findViewById(R.id.enter_description_button);
        descriptionTextView = findViewById(R.id.description_text);
        descriptionTitleView = findViewById(R.id.description_title);
        dateTextView = findViewById(R.id.current_date_text_view);
        timeTextView = findViewById(R.id.time_text_view);
        nameEditText = findViewById(R.id.name_edit_text);
        FloatingActionButton acceptButton = findViewById(R.id.accept_button);

        setDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new EnterDescriptionActivity().createIntent(
                        CreateTimeToDateActivity.this,
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

                Dialog picker = new DatePickerDialog(CreateTimeToDateActivity.this,
                        CreateTimeToDateActivity.this,
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

                Dialog picker = new TimePickerDialog(CreateTimeToDateActivity.this,
                        CreateTimeToDateActivity.this,
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
                    // todo use minutes later on too
                    if (hours >= 0 && hours <= 23) {
                        String name = nameEditText.getText().toString();
                        if (name.length() <= 0) {
                            Toast.makeText(getApplicationContext(), "Please, enter a name", Toast.LENGTH_SHORT).show();
                        } else {
                            String finalDate = String.format("%04d-%02d-%02d-%02d", year, month, day, hours);
                            TimeToDateDatabaseHelper dbHelper = new TimeToDateDatabaseHelper(getApplicationContext());
                            dbHelper.addNewTimeToDate(new TimeToDate(name, finalDate, descriptionText));
                            startActivity(new Intent(CreateTimeToDateActivity.this, MainActivity.class));
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
        }

    }

    private void fillIntentData() {
        Intent intent = getIntent();

        descriptionText = intent.getStringExtra(EXTRA_DESCRIPTION);
        nameEditText.setText(intent.getStringExtra(EXTRA_NAME));
        String extraDate = intent.getStringExtra(EXTRA_DATE);
        if (TextUtils.isEmpty(extraDate)) {
            dateTextView.setText(new LocalDate().toString(DATE_FORMAT));
        } else {
            dateTextView.setText(extraDate);
        }

        LocalTime time = LocalTime.now().withHourOfDay(0).withMinuteOfHour(0);
        timeTextView.setText(time.toString(TIME_FORMAT));
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
        DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
        return LocalDate.parse(rawDate, formatter);
    }
}
