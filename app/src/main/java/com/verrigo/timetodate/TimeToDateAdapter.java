package com.verrigo.timetodate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
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

    public boolean isDeletingMode() {
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
            holder.isSettingState = false;
            try {
                if (timeToDate.getDescription().equals("")) {
                    holder.detailsDescriptionTextView.setText("Нет описания");
                } else {
                    holder.detailsDescriptionTextView.setText(timeToDate.getDescription());
                }
            } catch (Exception ex) {
                holder.detailsDescriptionTextView.setText("Нет описания");
            }
            holder.subDetailsCardView.setVisibility(timeToDate.isExpanded() ? View.VISIBLE : View.GONE);
            String[] date = timeToDate.getDate().split("-");
            String year = date[0];
            StringBuilder sb = new StringBuilder();
            sb.append(year.charAt(year.length() - 2));
            sb.append(year.charAt(year.length() - 1));
            String yearTwoLast = sb.toString();
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);
            int hour = Integer.parseInt(date[3]);
            holder.detailsDateTextView.setText(String.format("%02d.%02d.%s\n%d ч.", day, month, yearTwoLast, hour));
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
        ViewHolder holder = (ViewHolder) v.getTag();
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            TimeToDate timeToDate = timeToDates.get(position);
            timeToDate.setExpanded(!timeToDate.isExpanded());
            notifyItemChanged(position);
            listener.onRecyclerItemClick(timeToDate);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        boolean isSettingState;
        final CardView subDetailsCardView;
        final TextView nameTextView;
        final TextView timeToDateTextView;

        final TextView detailsDescriptionTextView;
        final TextView detailsDateTextView;

        final ImageButton deleteTimeToDateImageButton;
        final View space;

        public ViewHolder(View itemView) {
            super(itemView);
            isSettingState = true;
            detailsDateTextView = itemView.findViewById(R.id.details_date_text_view);
            detailsDescriptionTextView = itemView.findViewById(R.id.details_description_text_view);
            subDetailsCardView = itemView.findViewById(R.id.sub_container_details_card_view);
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
