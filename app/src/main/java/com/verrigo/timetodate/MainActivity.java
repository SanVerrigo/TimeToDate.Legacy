package com.verrigo.timetodate;

import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TimeToDateAdapter adapter;
    TimeToDateDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.time_to_date_recycler_view);
        adapter = new TimeToDateAdapter(new OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(TimeToDate timeToDate) {
//                TransitionManager.beginDelayedTransition(recyclerView);
//                adapter.notifyDataSetChanged();
            }
        });
        dbHelper = new TimeToDateDatabaseHelper(this);
        FloatingActionButton fab = findViewById(R.id.create_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new CreateTimeToDateActivity().createIntent(MainActivity.this));
            }
        });
        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new SwipeController()).attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.setTimeToDates(dbHelper.dbParseListTimeToDates());
        runTimer();
    }

    public void setTimeToDatesTextViewsOfHoldersFromList(List<TimeToDate> timeToDates, List<TextView> textViews) {
        for (int i = 0; i < textViews.size(); i++) {
            textViews.get(i).setText(TimeToDate.currentLeftTime(timeToDates.get(i).getDate()));
        }
    }

    public void runTimer() {
        final Handler handlerForRunTimer = new Handler();
        handlerForRunTimer.post(new Runnable() {
            @Override
            public void run() {
                List<TimeToDate> timeToDatesToSet = adapter.getTimeToDates();
                List<TextView> textViewsToSet = adapter.getTimeToDatesTextViewsOfHolders();
                setTimeToDatesTextViewsOfHoldersFromList(timeToDatesToSet, textViewsToSet);
                handlerForRunTimer.postDelayed(this, 100);
            }
        });
    }


}
