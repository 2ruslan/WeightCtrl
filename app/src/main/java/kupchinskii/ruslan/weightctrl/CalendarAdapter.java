package kupchinskii.ruslan.weightctrl;

import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    static Calendar calendar;
    private static List<CalendarItem> Items = new ArrayList<CalendarItem>();

    private int FontDaySize = -1;
    private int FontValSize = -1;

    Context mContext;

    static public String getMonthName(){
        String res = (String)android.text.format.DateFormat.format("LLLL", calendar ) + ", " + String.valueOf(Year);
        if(res.contains("LLLL")) {
            String month_date = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            res = res.replace("LLLL", month_date);
        }
        return res;
    }

    // Конструктор
    public CalendarAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId, Items);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        FontDaySize = Math.min(( (int)pixelsToSp(context, (float)size.x) / 34), 11);
        FontValSize = Math.min(( (int)pixelsToSp(context, (float)size.x) / 36), 9);

        this.mContext = context;
        initMonth(Calendar.getInstance().get(Calendar.YEAR) , Calendar.getInstance().get(Calendar.MONTH));


    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    boolean isInit = false;
    private void initMonth(int year, int month){

        Year = year;
        Month = month;
        calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayWeek = calendar.getFirstDayOfWeek();
        int dayMonth = calendar.get(Calendar.DAY_OF_WEEK);

        String[] shortWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();

        Items.clear();

        for(int i= firstDayWeek; i <= 7; i++)
            Items.add(new CalendarItem(shortWeekDays[i], "", "", false, null));
        for(int i= 1; i <= firstDayWeek - 1; i++)
             Items.add(new CalendarItem(shortWeekDays[i], "", "", false, null));

        int emptyDays = dayMonth - (firstDayWeek != 1 ? 2 : 1);
        if (emptyDays < 0)
            emptyDays = 7 + emptyDays;
        for(int i = 1; i <= emptyDays; i++)
             Items.add(new CalendarItem());

        int offset = Items.size() - 1;
        Calendar c = Calendar.getInstance();
        for(int i = 1; i<=dayCount;i++) {
            c.set(Year, Month,i);
            Items.add(new CalendarItem((i > 9 ? "" : " ") + String.valueOf(i), "", "", true, c.getTime()));
        }

        Cursor crr =  DBHelper.GetReusults(Year, Month);
        crr.moveToFirst();
        for (int i = 0; i < crr.getCount(); i++) {
            int hips = crr.getInt(crr.getColumnIndex(DB.C_RES_HIPS));
            float weight = crr.getFloat(crr.getColumnIndex(DB.C_RES_WEIGHT));
            Date onDate = DB.getData(crr.getString(crr.getColumnIndex(DB.C_RES_ONDATE)));
            calendar.setTime(onDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            Items.get(day+offset).hips = "(" + String.valueOf(hips) + ")";
            Items.get(day+offset).Hips = String.valueOf(hips);
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
            boolean isEmpty = (Items.get(position).weight == null || Items.get(position).weight.equals(""))
                    && (Items.get(position).Hips == null || Items.get(position).Hips.equals(""));

            if (isEmpty) {
                gridView = inflater.inflate(R.layout.item_calendar_empt, null);
            }
            else {
                gridView = inflater.inflate(R.layout.item_calendar, null);

                TextView wgt = (TextView) gridView.findViewById(R.id.ic_weight);
                wgt.setText(Items.get(position).weight);
                if (FontValSize != -1 )
                    wgt.setTextSize(TypedValue.COMPLEX_UNIT_SP,  FontValSize);

                TextView hps = (TextView) gridView.findViewById(R.id.ic_hips);
                hps.setText(Items.get(position).hips);
                if (FontValSize != -1 )
                    hps.setTextSize(TypedValue.COMPLEX_UNIT_SP,  FontValSize);
            }

            TextView day = (TextView) gridView.findViewById(R.id.ic_day);
            day.setText(Items.get(position).day);
            if (FontDaySize != -1 && !isEmpty)
                day.setTextSize(TypedValue.COMPLEX_UNIT_SP,  FontDaySize);

            if (!Items.get(position).isDay)
                gridView.setBackground( getContext().getResources().getDrawable(R.drawable.calendar_item_background_empty));

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