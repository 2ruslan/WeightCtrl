package kupchinskii.ruslan.weightctrl;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.text.DateFormatSymbols;

public class CalendarAdapter extends ArrayAdapter<CalendarItem> {

    static int Year;
    static int Month;
    private static List<CalendarItem> Items = new ArrayList<CalendarItem>();

    Context mContext;

    static public String getMonthName(){
        return (String)android.text.format.DateFormat.format("LLLL", Month) + ", " + String.valueOf(Year);
    }

    // Конструктор
    public CalendarAdapter(Context context, int textViewResourceId, int year, int month) {
        super(context, textViewResourceId, Items);
        this.mContext = context;
        initMonth(year, month);
    }

    boolean isInit = false;
    private void initMonth(int year, int month){

        Year = year;
        Month = month;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayWeek = calendar.getFirstDayOfWeek();
        int dayMonth = calendar.get(Calendar.DAY_OF_WEEK);

        String[] shortWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();

        Items.clear();

        for(int i= firstDayWeek; i <= 7; i++)
            Items.add(new CalendarItem(shortWeekDays[i], "", ""));
        for(int i= 1; i <= firstDayWeek - 1; i++)
             Items.add(new CalendarItem(shortWeekDays[i], "", ""));

        int emptyDays = dayMonth - (firstDayWeek != 1 ? 2 : 1);
        if (emptyDays < 0)
            emptyDays = 7 + emptyDays;
        for(int i = 1; i <= emptyDays; i++)
             Items.add(new CalendarItem());

        int offset = Items.size();
        for(int i = 1; i<=dayCount;i++)
             Items.add(new CalendarItem( (i > 9 ? "" : " " ) + String.valueOf(i), "", ""));

        Cursor crr =  DBHelper.GetReusults(Year, Month);
        crr.moveToFirst();
        for (int i = 0; i < crr.getCount(); i++) {
            int hips = crr.getInt(crr.getColumnIndex(DB.C_RES_HIPS));
            float weight = crr.getFloat(crr.getColumnIndex(DB.C_RES_WEIGHT));
            Date onDate = DB.getData(crr.getString(crr.getColumnIndex(DB.C_RES_ONDATE)));
            calendar.setTime(onDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            Items.get(day+offset).hips = "(" + String.valueOf(hips) + ")";
            Items.get(day+offset).weight = String.format("%s",weight);

            crr.moveToNext();
        }

        isInit = false;

        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (!isInit) {
            gridView = inflater.inflate(R.layout.item_calendar, null);

            TextView day = (TextView) gridView
                    .findViewById(R.id.ic_day);

            day.setText(Items.get(position).day);

            TextView wgt = (TextView) gridView
                    .findViewById(R.id.ic_weight);

            wgt.setText(Items.get(position).weight);

            TextView hps = (TextView) gridView
                    .findViewById(R.id.ic_hips);

            hps.setText(Items.get(position).hips);


        } else {
            gridView = (View) convertView;
        }

        if (Items.size() == position)
            isInit = true;

        return gridView;
    }

    // возвращает содержимое выделенного элемента списка
    public CalendarItem getItem(int position) {
        return Items.get(position);
    }

    public void NexMonth(){
        if(++Month > 11)
        {
            Year++;
            Month = 0;
        }
        initMonth(Year, Month);
    }

    public void PrevMonth(){
        if(--Month < 0)
        {
            Year--;
            Month = 11;
        }
        initMonth(Year, Month);
    }



}