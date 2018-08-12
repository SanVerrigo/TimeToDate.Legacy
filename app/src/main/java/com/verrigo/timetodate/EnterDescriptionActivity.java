package com.verrigo.timetodate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EnterDescriptionActivity extends AppCompatActivity {

    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_HOURS = "hours";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DATE = "date";

    EditText editText;





    public Intent createIntent(Context context, String description, int hours, String name, String date) {
        Intent intent = new Intent(context, this.getClass());
        intent.putExtra(EXTRA_DESCRIPTION, description);
        intent.putExtra(EXTRA_HOURS, hours);
        intent.putExtra(EXTRA_NAME, name);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_description);



        editText = findViewById(R.id.description_edit_text);
        final TextView textView = findViewById(R.id.count_of_symbols_text_view);

        final int maxLength = getResources().getInteger(R.integer.enter_description_max_width_integer);


        try {
            editText.setText(getIntent().getExtras().getString(EXTRA_DESCRIPTION));
            textView.setText(String.format("%d/%d", getIntent().getExtras().getString(EXTRA_DESCRIPTION).length(), maxLength));
        } catch (Exception ex) {
            textView.setText(String.format("%d/%d", 0, maxLength));
            Toast.makeText(this, "lul", Toast.LENGTH_SHORT).show();
        }

        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(EXTRA_DESCRIPTION));
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textView.setText(String.format("%d/%d", start + count, maxLength));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        FloatingActionButton applyDescriptionButton = findViewById(R.id.apply_description_text_button);
        applyDescriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                startActivity(new CreateTimeToDateActivity().createIntentAndSetDescription(EnterDescriptionActivity.this, editText.getText().toString(),
                        bundle.getInt(EXTRA_HOURS),
                        bundle.getString(EXTRA_NAME),
                        bundle.getString(EXTRA_DATE)));
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_DESCRIPTION, editText.getText().toString());
    }
}
