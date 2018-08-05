package com.verrigo.timetodate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Verrigo on 29.07.2018.
 */

public class TimeToDateAdapter extends RecyclerView.Adapter<TimeToDateAdapter.ViewHolder> implements View.OnClickListener {

    private boolean isDeletingMode = false;
    private List<TimeToDate> timeToDates;
    private TimeToDate timeToDateForDeleting;
    private OnRecyclerItemClickListener listener;
    private Context context;
    private ValueAnimator deleteAnimator;
    private final static int HIDDEN_MARGIN = UiUtils.dpToPx(28);

    public boolean getIsDeletingMode() {
        return isDeletingMode;
    }

    public void setTimeToDates(List<TimeToDate> list) {
        timeToDates = list;
        notifyDataSetChanged();
        runTimer();
    }

    public void switchDeletingMode() {
        isDeletingMode = !isDeletingMode;
        int startMargin;
        int endMargin;
        if (deleteAnimator != null && deleteAnimator.isRunning()) {
            startMargin = (int) deleteAnimator.getAnimatedValue();
            deleteAnimator.cancel();
        } else {
            startMargin = isDeletingMode ? HIDDEN_MARGIN : 0;
        }
        endMargin = isDeletingMode ? 0 : HIDDEN_MARGIN;
        deleteAnimator = ValueAnimator.ofInt(startMargin, endMargin)
                .setDuration(500);
        deleteAnimator.setInterpolator(new FastOutSlowInInterpolator());
        deleteAnimator.start();
        notifyDataSetChanged();
    }

    public void runTimer() {
        final Handler handlerForRunTimer = new Handler();
        handlerForRunTimer.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                handlerForRunTimer.postDelayed(this, 1000);
            }
        });
    }


    public TimeToDateAdapter(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.time_to_date_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final TimeToDate timeToDate = timeToDates.get(position);
        holder.nameTextView.setText(timeToDate.getName());
        holder.timeToDateTextView.setText(TimeToDate.currentLeftTime(timeToDate.getDate()));
        holder.deleteTimeToDateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeToDateForDeleting = timeToDate;
                new DeleteFromDatabase(position).execute();
            }
        });

        if (deleteAnimator != null) {
            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.space.getLayoutParams();
            if (deleteAnimator.isRunning()) {
                deleteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        lp.leftMargin = (int) animation.getAnimatedValue();
                        holder.space.requestLayout();
                    }
                });
            } else {
                lp.leftMargin = (int) deleteAnimator.getAnimatedValue();
                holder.space.requestLayout();
            }
        }
        holder.itemView.setTag(holder);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return timeToDates.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            TimeToDate timeToDate = timeToDates.get(viewHolder.getAdapterPosition());
            listener.onRecyclerItemClick(timeToDate);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView timeToDateTextView;
        final ImageButton deleteTimeToDateImageButton;
        final View space;

        public ViewHolder(View itemView) {
            super(itemView);
            space = itemView.findViewById(R.id.space);
            nameTextView = itemView.findViewById(R.id.event_name_text_view);
            timeToDateTextView = itemView.findViewById(R.id.time_to_date_text_view);
            deleteTimeToDateImageButton = itemView.findViewById(R.id.delete_time_to_date_button);
        }
    }

    public class DeleteFromDatabase extends AsyncTask<Void, Void, Boolean> {

        final int position;

        DeleteFromDatabase(int position) {
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                TimeToDateDatabaseHelper dbHelper = new TimeToDateDatabaseHelper(context);
                dbHelper.deleteTimeToDateRecord(timeToDateForDeleting.get_id(), context);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Toast.makeText(context, "Successfully ted!", Toast.LENGTH_SHORT).show();
                setTimeToDates(new TimeToDateDatabaseHelper(context).dbParseListTimeToDates());
                notifyItemRemoved(position);
            } else {
            }
        }
    }
}
