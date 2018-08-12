package com.verrigo.timetodate;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CreateTimeToDateActivity extends AppCompatActivity {

    private EditText hoursEditText;
    private TextView dateTextView;
    private EditText nameEditText;
    private String descriptionText = null;
    private  Button setDescriptionButton;

    private final String EXTRA_HOURS = EnterDescriptionActivity.EXTRA_HOURS;
    private final String EXTRA_DESCRIPTION = EnterDescriptionActivity.EXTRA_DESCRIPTION;
    private final String EXTRA_DATE = EnterDescriptionActivity.EXTRA_DATE;
    private final String EXTRA_NAME = EnterDescriptionActivity.EXTRA_NAME;

    public Intent createIntent(Context context) {
        Intent intent = new Intent(context, CreateTimeToDateActivity.class);
        return intent;
    }

    public Intent createIntentAndSetDescription(Context context, String description, int hours, String name, String date) {
        Intent intent = new Intent(context, CreateTimeToDateActivity.class);
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_HOURS, hours);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        try {
            Intent intent = getIntent();
            intent.getExtras().getString(EnterDescriptionActivity.EXTRA_DESCRIPTION);
            if (intent != null) {
                descriptionText = intent.getExtras().getString(EXTRA_DESCRIPTION);
                hoursEditText.setText(Integer.toString(intent.getExtras().getInt(EXTRA_HOURS)));
                nameEditText.setText(intent.getExtras().getString(EXTRA_NAME));
                dateTextView.setText(intent.getExtras().getString(EXTRA_DATE));
                ImageButton goToDescriptionButton = findViewById(R.id.see_description_button);
                if (descriptionText.equals("")) {
                    setDescriptionButton.setText(R.string.add_description_string);
                    goToDescriptionButton.setVisibility(View.GONE);
                } else {
                    setDescriptionButton.setText(R.string.edit_description);
                    goToDescriptionButton.setVisibility(View.VISIBLE);
                    goToDescriptionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int hours;
                            if (hoursEditText.getText().toString().equals("")){
                                hours = 0;
                            } else {
                                hours = Integer.parseInt(hoursEditText.getText().toString());
                            }
                            startActivity(new EnterDescriptionActivity().createIntent(CreateTimeToDateActivity.this,
                                    descriptionText,
                                    hours,
                                    nameEditText.getText().toString(),
                                    dateTextView.getText().toString()));
                        }
                    });
                }
            }
        } catch (Exception ex) {

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_time_to_date);


        Button setDateButton = findViewById(R.id.choose_the_date_button);
        setDescriptionButton = findViewById(R.id.enter_description_button);

        setDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hours;
                if (hoursEditText.getText().toString().equals("")){
                   hours = 0;
            } else {
                    hours = Integer.parseInt(hoursEditText.getText().toString());
                }
                startActivity(new EnterDescriptionActivity().createIntent(CreateTimeToDateActivity.this,
                        descriptionText,
                        hours,
                        nameEditText.getText().toString(),
                        dateTextView.getText().toString()));
            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        hoursEditText = findViewById(R.id.hour_amount_edit_text);
        hoursEditText.setText("");
        hoursEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                     if (Integer.parseInt(hoursEditText.getText().toString()) > 23 && !hoursEditText.getText().equals("")) {
                        hoursEditText.setText("23");
                    }
                }catch (Exception ex) {

                }
            }
        });
        dateTextView = findViewById(R.id.current_date_text_view);
        nameEditText = findViewById(R.id.name_edit_text);

        Calendar calendar = Calendar.getInstance();
        dateTextView.setText(String.format("%d-%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));

        FloatingActionButton acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<String> date = Arrays.asList(dateTextView.getText().toString().split(String.format("-")));
                    int year = Integer.parseInt(date.get(2));
                    int month = Integer.parseInt(date.get(1));
                    int day = Integer.parseInt(date.get(0));
                    int hours = Integer.parseInt(hoursEditText.getText().toString());
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

        if (savedInstanceState != null) {
            hoursEditText.setText(hoursEditText.getText().equals("") ? "" : savedInstanceState.getString(EXTRA_HOURS));
            nameEditText.setText(savedInstanceState.getString(EXTRA_NAME));
            dateTextView.setText(savedInstanceState.getString(EXTRA_DATE));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_HOURS, hoursEditText.getText().toString());
        outState.putString(EXTRA_NAME, nameEditText.getText().toString());
        outState.putString(EXTRA_DATE, dateTextView.getText().toString());
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }
}
