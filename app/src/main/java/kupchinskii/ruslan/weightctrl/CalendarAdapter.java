package kupchinskii.ruslan.weightctrl;

import android.content.Context;
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

public class CalendarAdapter extends ArrayAdapter<CalendarItem> {


    private static List<CalendarItem> Items = new ArrayList<CalendarItem>();

    Context mContext;

    // Конструктор
    public CalendarAdapter(Context context, int textViewResourceId, int year, int month) {
        super(context, textViewResourceId, Items);
        this.mContext = context;
        initMonth(year, month);
    }

    public static int getDayOfWeek(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        int dow = c.get(Calendar.DAY_OF_WEEK);
        return dow;
    }

    private void initMonth(int year, int month){

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        int dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayWeek = calendar.getFirstDayOfWeek();
        int dayMonth = calendar.get(Calendar.DAY_OF_WEEK);


        String[] shortWeekDays = DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays();



         for(int i= firstDayWeek; i <= 7; i++)
            Items.add(new CalendarItem(shortWeekDays[i], "", ""));
         for(int i= 1; i <= firstDayWeek - 1; i++)
             Items.add(new CalendarItem(shortWeekDays[i], "", ""));

         for(int i = 0; i < dayMonth - firstDayWeek;i++)
             Items.add(new CalendarItem());

         for(int i = 1; i<=dayCount;i++) {
             Items.add(new CalendarItem( (i > 9 ? "" : " " ) + String.valueOf(i), "105.7", "(104)"));
         }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(mContext);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_calendar, null);

            // set image based on selected text
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

        return gridView;
    }

    // возвращает содержимое выделенного элемента списка
    public CalendarItem getItem(int position) {
        return Items.get(position);
    }

}