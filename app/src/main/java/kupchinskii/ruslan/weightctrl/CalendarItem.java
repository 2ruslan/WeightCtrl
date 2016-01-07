package kupchinskii.ruslan.weightctrl;


public class CalendarItem {
    public  CalendarItem(String pDay,  String pWeight, String pHips, boolean pIsDay)
    {
        day     = pDay;
        weight   = pWeight;
        hips    = pHips;
        isDay = pIsDay;
    }

    public  CalendarItem()
    {
        this("","","", false);
    }
    String day;
    String weight;
    String hips;
    boolean isDay;
}
