package com.verrigo.timetodate;

import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Time;
import java.util.List;

/**
 * Created by Verrigo on 29.07.2018.
 */

public class TimeToDateAdapter extends RecyclerView.Adapter<TimeToDateAdapter.ViewHolder> implements View.OnClickListener {

    private String CHANNEL_ID = "channelId";
    private int mId = 1;

    private boolean isDeletingMode = false;

    public List<TimeToDate> getTimeToDates() {
        return timeToDates;
    }

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
        holder.subDetailsCardView.setVisibility(timeToDate.isExpanded() ? View.VISIBLE : View.GONE);
        if (holder.subDetailsCardView.getVisibility() == View.VISIBLE) {
            holder.detailsDescriptionTextView.setText(descriptionTextToSet(timeToDate.getDescription()));
            holder.detailsDateTextView.setText(dateTextToSet(timeToDate.getDate()));
        }
        holder.nameTextView.setText(timeToDate.getName());
        holder.timeToDateTextView.setText(TimeToDate.currentLeftTime(timeToDate.getDate()));
        holder.mainCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeToDate.setExpanded(!timeToDate.isExpanded());
                notifyItemChanged(position);
                listener.onRecyclerItemClick(timeToDate);
            }
        });
        holder.subDetailsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new EditTimeToDateActivity().createIntent(context, timeToDate.getName(), timeToDate.getDate(), timeToDate.getDescription(), timeToDate.get_id()));
            }
        });

//        holder.itemView.setTag(holder);
//        holder.itemView.setOnClickListener(this);
        // excess code starts
//        holder.deleteTimeToDateImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                timeToDateForDeleting = timeToDate;
//
//            }
//        });
//        if (deleteAnimator != null) {
//            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.space.getLayoutParams();
//            if (deleteAnimator.isRunning()) {
//                deleteAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        lp.leftMargin = (int) animation.getAnimatedValue();
//                        holder.space.requestLayout();
//                    }
//                });
//            } else {
//                lp.leftMargin = (int) deleteAnimator.getAnimatedValue();
//                holder.space.requestLayout();
//            }
//        }
        // excess code ends


    }


    @Override
    public int getItemCount() {
        return timeToDates.size();
    }

    public void setDeletingModeOnPosition(int position) {
//        Toast.makeText(context, "sss" + position, Toast.LENGTH_SHORT).show();
        timeToDates.get(position).setDeletingMode(true);
        notifyItemChanged(position);
    }

    public void deleteTimeToDateOnPosition(int position) {
        new DeleteFromDatabase(position, getTimeToDates()).execute();
    }

    private String dateTextToSet(String dateToSplit) {
        String[] date = dateToSplit.split(" ");
        String rawDate = date[0];
        String rawTime = date[1];
        DateTimeFormatter formatter = DateTimeFormat.forPattern(TimeToDate.DATE_FORMAT);
        DateTimeFormatter sformatter = DateTimeFormat.forPattern(TimeToDate.TIME_FORMAT);
        LocalDate date1 = LocalDate.parse(rawDate, formatter);
        LocalTime time = LocalTime.parse(rawTime, sformatter);
        String year = Integer.toString(date1.getYear());

        StringBuilder sb = new StringBuilder();
        sb.append(year.charAt(year.length() - 2));
        sb.append(year.charAt(year.length() - 1));

        String yearTwoLast = sb.toString();
        int month = date1.getMonthOfYear();
        int day = date1.getDayOfMonth();
        int hour = time.getHourOfDay();
        int mins = time.getMinuteOfHour();
        return !(hour == 0 && mins == 0) ? String.format("%02d.%02d.%s\n%02d:%02d", day, month, yearTwoLast, hour, mins) : String.format("%02d.%02d.%s", day, month, yearTwoLast);
    }

    private String descriptionTextToSet(String description) {
        try {
            if (description.equals("")) {
                return "Нет описания";
            } else {
                return description;
            }
        } catch (Exception ex) {
            return "Нет описания";
        }
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

        final CardView mainCardView;
        final CardView subDetailsCardView;

        final TextView nameTextView;
        final TextView timeToDateTextView;
        final TextView detailsDescriptionTextView;

        final TextView detailsDateTextView;
        final ImageButton deleteTimeToDateImageButton;

        final View space;

        public ViewHolder(View itemView) {
            super(itemView);
            mainCardView = itemView.findViewById(R.id.main_container_card_view);
            detailsDateTextView = itemView.findViewById(R.id.details_date_text_view);
            detailsDescriptionTextView = itemView.findViewById(R.id.details_description_text_view);
            subDetailsCardView = itemView.findViewById(R.id.sub_container_details_card_view);
            space = itemView.findViewById(R.id.space);
            nameTextView = itemView.findViewById(R.id.event_name_text_view);
            timeToDateTextView = itemView.findViewById(R.id.time_to_date_text_view);
            deleteTimeToDateImageButton = itemView.findViewById(R.id.delete_time_to_date_button);
        }

    }

    public void createNotification(String eventName, int numOfDays) {

    }

    public class DeleteFromDatabase extends AsyncTask<Void, Void, Boolean> {


        final int position;

        List<TimeToDate> timeToDates;

        DeleteFromDatabase(int position, List<TimeToDate> currentList) {
            this.position = position;
            this.timeToDates = currentList;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                TimeToDateDatabaseHelper dbHelper = new TimeToDateDatabaseHelper(context);
                dbHelper.deleteTimeToDateRecord(timeToDates.get(position).get_id());
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Toast.makeText(context, "Database is unavailable", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                setTimeToDates(new TimeToDateDatabaseHelper(context).dbParseListTimeToDates());
                notifyItemRemoved(position);
            }
        }

    }


    //    public void switchDeletingMode() {
//        isDeletingMode = !isDeletingMode;
//        int startMargin;
//        int endMargin;
//        if (deleteAnimator != null && deleteAnimator.isRunning()) {
//            startMargin = (int) deleteAnimator.getAnimatedValue();
//            deleteAnimator.cancel();
//        } else {
//            startMargin = isDeletingMode ? HIDDEN_MARGIN : 0;
//        }
//        endMargin = isDeletingMode ? 0 : HIDDEN_MARGIN;
//        deleteAnimator = ValueAnimator.ofInt(startMargin, endMargin)
//                .setDuration(500);
//        deleteAnimator.setInterpolator(new FastOutSlowInInterpolator());
//        deleteAnimator.start();
//        notifyDataSetChanged();
//    }
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
}
