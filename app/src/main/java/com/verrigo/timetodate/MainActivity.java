package com.verrigo.timetodate;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    TimeToDateAdapter adapter;
    TimeToDateDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new TimeToDateAdapter(new OnRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(TimeToDate timeToDate) {

            }
        });
        dbHelper = new TimeToDateDatabaseHelper(this);
        RecyclerView recyclerView = findViewById(R.id.time_to_date_recycler_view);
        FloatingActionButton fab = findViewById(R.id.create_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CreateTimeToDateActivity.createIntent(MainActivity.this));
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.setTimeToDates(dbHelper.dbParseListTimeToDates());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem createMenuButton = menu.findItem(R.id.action_create);
//        MenuItem deleteMenuButton = menu.findItem(R.id.action_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                switchDeletingMode(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void switchDeletingMode(MenuItem item) {
        adapter.switchDeletingMode();
        if (adapter.getIsDeletingMode()) {
            item.setIcon(R.drawable.ic_delete_switched_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_delete_white_24dp);
        }
    }
}
