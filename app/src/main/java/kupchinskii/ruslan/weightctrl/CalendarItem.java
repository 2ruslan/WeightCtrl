package kupchinskii.ruslan.weightctrl;


public class CalendarItem {
    public  CalendarItem(String pDay,  String pWeight, String pHips)
    {
        day     = pDay;
        weight   = pWeight;
        hips    = pHips;
    }

    public  CalendarItem()
    {
        this("","","");
    }
    String day;
    String weight;
    String hips;
}
