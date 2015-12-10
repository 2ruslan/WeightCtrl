package kupchinskii.ruslan.weightctrl;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarAdapter extends ArrayAdapter<String> {

    private static class item{
        public  item()
        {
            day = "";
            weight = "";
            hips = "";
        }
        String day;
        String weight;
        String hips;
    }

    private static item[] Items = new item[35];

    private static final String[] mContacts = { "Рыжик", "Барсик", "Мурзик",
            "Мурка", "Васька", "Полосатик", "Матроскин", "Лизка", "Томосина",
            "Бегемот", "Чеширский", "Дивуар", "Тигра", "Лаура" };

    Context mContext;

    // Конструктор
    public CalendarAdapter(Context context, int textViewResourceId, int year, int month) {
        super(context, textViewResourceId, mContacts);
        this.mContext = context;
        initMonth(year, month);
    }

    private void initMonth(int year, int month){
        GregorianCalendar bday = new GregorianCalendar(year, month, 1);

        int dayCount = bday.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDay = bday.getFirstDayOfWeek();

        for(int i = 0; i < firstDay;i++)
        {
            item ni = new item();
            ni.day = "";
            ni.hips = "";
            ni.weight = "";
            Items[i] = ni;
        }

        for(int i = 0; i<dayCount;i++)
        {
            item ni = new item();
            ni.day = String.valueOf(i);
            ni.hips = "";
            ni.weight = "";
            Items[firstDay + i] = ni;
        }

        for(int i = firstDay + dayCount; i < 35;i++)
        {
            item ni = new item();
            ni.day = "";
            ni.hips = "";
            ni.weight = "";
            Items[i] = ni;
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

            day.setText(Items[position].day);



        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    // возвращает содержимое выделенного элемента списка
    public String getItem(int position) {
        return mContacts[position];
    }

}