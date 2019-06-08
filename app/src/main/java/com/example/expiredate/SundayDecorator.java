package com.example.expiredate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class SundayDecorator implements DayViewDecorator {
    private final Calendar calendar = Calendar.getInstance();
    //private final Drawable highlightDrawable;
    private CalendarDay date;

    public SundayDecorator() {
        //highlightDrawable = new ColorDrawable(context.getResources().getColor("#ff00000",context.getTheme()));
//            date = CalendarDay.today();
        date = CalendarDay.from(calendar);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        return weekDay == Calendar.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}
