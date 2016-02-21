package kupchinskii.ruslan.weightctrl;


import java.util.Date;

public class CalendarItem {
    public  CalendarItem(String pDay,  String pWeight, String pHips, boolean pIsDay, Date dt)
    {
        day     = pDay;
        weight   = pWeight;
        hips    =  pHips;
        isDay = pIsDay;
        date = dt;
    }

    public  CalendarItem()
    {
        this("","","", false, null);
    }
    String day;
    String weight;
    String hips;
    String Hips;
    boolean isDay;
    Date  date;
}
