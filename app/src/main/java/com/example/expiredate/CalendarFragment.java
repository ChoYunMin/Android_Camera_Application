package com.example.expiredate;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import com.example.expiredate.SundayDecorator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {
    MaterialCalendarView materialCalendarView;
    View v;

    public CalendarFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        materialCalendarView = (MaterialCalendarView)v.findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        String[] result = {"2019,03,18","2019,04,18","2019,05,18","2019,06,18"};

        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            //if (isFinishing()) {
            //    return;
            //}


            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays, getActivity()));
            return;
        }
    }
}
