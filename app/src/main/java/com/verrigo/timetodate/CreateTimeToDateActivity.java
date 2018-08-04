package com.verrigo.timetodate;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    EditText hoursEditText;
    TextView dateTextView;
    EditText nameEditText;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CreateTimeToDateActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_time_to_date);
        Button button = findViewById(R.id.choose_the_date_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

        hoursEditText = findViewById(R.id.hour_amount_edit_text);
        dateTextView = findViewById(R.id.current_date_text_view);
        nameEditText = findViewById(R.id.name_edit_text);

        Calendar calendar = Calendar.getInstance();
        dateTextView.setText(String.format("%d-%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)));

        FloatingActionButton acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<String> date = Arrays.asList(dateTextView.getText().toString().split("-"));
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
                            dbHelper.addNewTimeToDate(new TimeToDate(name, finalDate));
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
    }
}
