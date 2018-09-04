package com.verrigo.timetodate;

/**
 * Created by Verrigo on 21.08.2018.
 */

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;

class SwipeController extends Callback {

   TimeToDateAdapter adapter;
    boolean swipeBack;
    private float x1;
    private float x2;
    static final int MIN_DISTANCE = 150;
    private boolean firstTime = true;

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        adapter = (TimeToDateAdapter) recyclerView.getAdapter();
        return makeMovementFlags(0, LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            adapter.deleteTimeToDateOnPosition(viewHolder.getAdapterPosition());
        }
    }



//    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//        if (swipeBack) {
//            swipeBack = false;
//            return 0;
//        }
//        return super.convertToAbsoluteDirection(flags, layoutDirection);
//    }


    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ACTION_STATE_SWIPE) {
//                adapter.setDeletingModeOnPosition(viewHolder.getAdapterPosition());
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
//
//        recyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        x1 = event.getX();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        x2 = event.getX();
//                        float deltaX = x2 - x1;
//                        if (Math.abs(deltaX) > MIN_DISTANCE)
//                        {
//
//                        }
//                        else
//                        {
//                            // consider as something else - a screen tap for example
//                        }
//                        break;
//                }
//                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//                return false;
//            }
//        });
    }
}
