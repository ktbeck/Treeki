package com.treeki.treekii;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {
    private static final String TAG = "Stats";
    private FirebaseUser user;
    //    public Activity this_activity;
    String date;
    String mood;
    String month;
    String day;
    String year;
    double avg;
    private ArrayList<Integer> mood_;
    private ArrayList<Date> dates_;
    private ArrayList<Date> high;
    private ArrayList<Date> low;
    int moodInt;
    SimpleDateFormat sdf = new SimpleDateFormat("M/d");
    SimpleDateFormat daySDF = new SimpleDateFormat("d");
    SimpleDateFormat monthSDF = new SimpleDateFormat("MMMM");
    String highString = "";
    String lowString = "";
    long count = 2;
    int bestMood;
    int worstMood;
    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH) + 1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        date = month + "-" + day + "-" + year;
        user = FirebaseAuth.getInstance().getCurrentUser();
        Date now = new Date();
        String mon = monthSDF.format(now);
        Log.i(TAG, "mon - " + mon);
        getSupportActionBar().hide();
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText(mon);
    }

    protected void onResume() {
        super.onResume();
        mood_ = new ArrayList<>();
        dates_ = new ArrayList<>();
        ref.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        long numChildren = dataSnapshot.getChildrenCount();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) { //if child /date
                            ++count;
                            date = childSnapshot.getKey();
                            mood = childSnapshot.child("mood").getValue(String.class); //get mood
                            if (mood != null) {
                                moodInt = Integer.valueOf(mood);
                                String month_ = date.split("-")[0];
                                String day_ = date.split("-")[1];
                                if (month_.equals(month)) {
                                    mood_.add(moodInt);

                                    Calendar date = Calendar.getInstance();
                                    date.set(Calendar.YEAR, Integer.parseInt(year));
                                    date.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                                    date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day_));
                                    Date utilDate = date.getTime();
                                    dates_.add(utilDate);

                                    for (int i = 0; i < dates_.size(); i++) {
                                        String dayOfDates = daySDF.format(dates_.get(i));
                                        dayOfDates.replaceFirst("^0*", "");
                                        if (dayOfDates.length() == 2) {
                                            dates_.add(dates_.get(i));
                                            dates_.remove(i);
                                            mood_.add(mood_.get(i));
                                            mood_.remove(i);
                                        }
                                    }

                                    if (count >= numChildren) {
                                        avg = averageArr(mood_);
                                        high = bestDay(mood_, dates_);
                                        low = worstDay(mood_, dates_);
                                        bestMood = highestMood(mood_);
                                        worstMood = lowestMood(mood_);
                                        int highCount = 0;
                                        int lowCount = 0;
                                        for (int i = 0; i < high.size(); i++) {
                                            if (highCount > 0 && highCount != high.size()) {
                                                highString += ", ";
                                            }
                                            highCount++;
                                            String format = sdf.format(high.get(i));
                                            highString += format;
                                        }
                                        for (int i = 0; i < low.size(); i++) {
                                            if (lowCount > 0 && lowCount != low.size()) {
                                                lowString += ", ";
                                            }
                                            lowCount++;
                                            String format = sdf.format(low.get(i));
                                            lowString += format;
                                        }

                                    }
                                }

                                TextView text = (TextView) findViewById(R.id.avg_mood);
                                TextView textHigh = (TextView) findViewById(R.id.high_mood);
                                TextView textLow = (TextView) findViewById(R.id.low_mood);
                                DecimalFormat df = new DecimalFormat("#.##");
                                String a = df.format(avg);
                                text.setText("Monthly Average is currently: " + a);
                                textHigh.setText("Your highest mood of " + bestMood + " was on these days: " + highString + ".");
                                textLow.setText("Your lowest mood of " + worstMood + " was on these days: " + lowString + ".");
                                if (count >= numChildren) {
                                    GraphView graph = (GraphView) findViewById(R.id.graph);
                                    DataPoint[] dp = new DataPoint[mood_.size()];
                                    for (int i = 1; i < mood_.size() + 1; i++) {
                                        if (mood_.get(i - 1) != null) {
                                            String dayOfDates = daySDF.format(dates_.get(i - 1));
                                            dayOfDates.replaceFirst("^0*", "");
                                            int dayVal = Integer.parseInt(dayOfDates);
                                            int currMood = mood_.get(i - 1);
                                            Log.i(TAG, "currmood " + currMood);
                                            DataPoint check = new DataPoint(dayVal, currMood);
                                            dp[i - 1] = check;
                                            Log.i(TAG, "values I should have" + dayVal + currMood);
                                        }
                                    }
                                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);
                                    graph.getViewport().setMinX(1);
                                    graph.getViewport().setMaxX(Integer.parseInt(day));
                                    graph.getViewport().setMinY(0);
                                    graph.getViewport().setMaxY(10);

                                    graph.getGridLabelRenderer().setNumHorizontalLabels(Integer.parseInt(day));
                                    graph.getGridLabelRenderer().setNumVerticalLabels(6);
                                    graph.getViewport().setYAxisBoundsManual(true);
                                    graph.getViewport().setXAxisBoundsManual(true);
                                    graph.getGridLabelRenderer().setHumanRounding(true);
                                    graph.addSeries(series);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "Error", databaseError.toException());
                    }
                }
        );
    }

    private int highestMood(ArrayList<Integer> mood){
        Integer high = 0;
        if (!mood.isEmpty()) {
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) > high) {
                    high = mood.get(i);
                }
            }
        }
        return high;
    }
    private int lowestMood(ArrayList<Integer> mood){
        Integer low = 11;
        if (!mood.isEmpty()) {
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) < low) {
                    low = mood.get(i);
                }
            }
        }
        return low;
    }

    private double averageArr(ArrayList<Integer> mood) {
        Integer sum = 0;
        if (!mood.isEmpty()) {
            for (Integer i : mood) {
                sum += i;

            }
            return sum.doubleValue() / mood.size();
        }
        return sum;
    }

    private ArrayList<Date> worstDay(ArrayList<Integer> mood, ArrayList<Date> dates) {
        Integer low = 11;
        ArrayList<Date> array = new ArrayList<>();
        if (!mood.isEmpty()) {
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) < low) {
                    low = mood.get(i);
                }
            }
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) == low) {
                    array.add(dates.get(i));
                }
            }
            return array;
        } else {
            return array;
        }
    }

    private ArrayList<Date> bestDay(ArrayList<Integer> mood, ArrayList<Date> dates) {
        Integer high = 0;
        ArrayList<Date> array = new ArrayList<>();
        if (!mood.isEmpty()) {
//            Log.i(TAG, "test print");
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) > high) {
                    high = mood.get(i);
                }
            }
            for (int i = 0; i < mood.size(); i++) {
                if (mood.get(i) == (high)) {
//                    Log.i(TAG, "high" + mood.get(i));
                    array.add(dates.get(i));
                }
            }
            return array;
        } else {
            return array;
        }
    }

    public static boolean contains(ArrayList<Integer> arr, int dayVal, int moodVal) {
        for (int k = 0; k < arr.size(); k += 2) {
            if (arr.get(k) == dayVal && arr.get(k + 1) == moodVal) {
                return true;
            }
        }
        return false;
    }
}