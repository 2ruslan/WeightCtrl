package kupchinskii.ruslan.weightctrl;


import java.util.Date;

public class CalendarItem {
    public  CalendarItem(String pDay,  String pWeight, String pHips, boolean pIsDay, Date dt, boolean pIsToday)
    {
        day     = pDay;
        weight   = pWeight;
        hips    =  pHips;
        isDay = pIsDay;
        date = dt;
        isToday = pIsToday;
    }

    public  CalendarItem()
    {
        this("","","", false, null, false);
    }
    String day;
    String weight;
    String hips;
    String Hips;
    boolean isDay;
    Date  date;
    boolean isToday;

}
