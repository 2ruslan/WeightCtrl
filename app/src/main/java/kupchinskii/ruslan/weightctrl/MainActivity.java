package kupchinskii.ruslan.weightctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
public class MainActivity extends Activity {

    private Integer str2int(String val){
        Integer res = null;
        if(val != null && !val.equals(""))
            res = Integer.parseInt(val);
        return  res;
    }

    //region Growth
    TextView _crlGrowth;
    private Integer GetGrowth() {
        return  str2int(_crlGrowth.getText().toString());
    }
    private void SetGrowth(Integer val) {
        if(val != null)
            _crlGrowth.setText(String.valueOf(val));
        else
            _crlGrowth.setText(null);
    }
    //endregion Growth

    //region Weight
    TextView _crlWeight;
    private Integer GetWeight() {
        Double res = null;
        String val = _crlWeight.getText().toString();
        if(val != null && !val.equals(""))
            res = Double.parseDouble(val);

        if(res != null)
            res *=  10;
        return (int)Math.round(res);
    }
    private void SetWeight(Integer val) {
        if(val != null)
            _crlWeight.setText(String.valueOf(val / 10.0));
        else
            _crlWeight.setText(null);
    }
    //endregion Weight

    //region Birthday
    TextView _crlBD;
    Date _dateBD;
    private Date GetBD() {
        return _dateBD;
    }
    private void SetBD(Date val) {
        _dateBD = val;
        if(val != null)
            _crlBD.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(val));
        else
            _crlBD.setText(null);
    }
    //endregion Birthday

    //region Hips
    TextView _crlHips;
    private Integer GetHips() {
        return str2int(_crlHips.getText().toString());
    }
    private void SetHips(Integer val) {
        if(val != null)
            _crlHips.setText(String.valueOf(val));
        else
            _crlHips.setText(null);
    }
    //endregion Hips


  //  SeekBar sbWeight;

    TextView _crlResW;
    TextView _crlResH;

    private GraphicalView chartViewWeight;
    private GraphicalView chartViewHips;

    LinearLayout layoutW;
    LinearLayout layoutH;

    CalendarAdapter calendarAdapter;

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _crlGrowth = (TextView) findViewById(R.id.et_gh);
        _crlWeight = (TextView) findViewById(R.id.et_wg);
        _crlHips = (TextView) findViewById(R.id.et_hp);
        _crlBD = (TextView) findViewById(R.id.et_bd);

        _crlResW = (TextView) findViewById(R.id.et_resw);
        _crlResH = (TextView) findViewById(R.id.et_resh);

        layoutW = (LinearLayout) findViewById(R.id.charWeight);
        layoutH = (LinearLayout) findViewById(R.id.charHips);

        DB.initDB(this);
        initCalendar();
        refresh();
    }

    private void refresh(){
        RefreshCurrent();
        refreshChart();
    }


    //region calendar
    GridView g;
    private void initCalendar()
    {
        g = (GridView) findViewById(R.id.gvCalendar);
        calendarAdapter = new CalendarAdapter(getApplicationContext(),R.layout.item_calendar);
        g.setAdapter(calendarAdapter);
        initMonthName();
    }

    public void OnClickNextMonth(View view){
        calendarAdapter.NexMonth();
        initMonthName();
    }

    public void OnClickPrevMonth(View view){
        calendarAdapter.PrevMonth();
        initMonthName();
    }

    private void initMonthName(){
        TextView tm = (TextView)findViewById(R.id.monthName);
        tm.setText(calendarAdapter.getMonthName());
    }

    //endregion calendar

    private void refreshChart(){
        chartViewWeight = ChartHelper.buildChartWeight(getBaseContext());
        layoutW.removeAllViews();
        layoutW.addView(chartViewWeight, new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        chartViewHips = ChartHelper.buildChartHips(getBaseContext());
        layoutH.removeAllViews();
        layoutH.addView(chartViewHips, new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

    }

    private void RefreshCurrent() {
        DBHelper.result res = DBHelper.GetReusultLast();
        SetGrowth(res.growth);
        SetWeight(res.weight);
        SetHips(res.hips);
        SetBD(res.birthday);

        String template = getString(R.string.res_html);
        try {
            String resw = template.replace("{0}", String.valueOf(res.imt))
                                  .replace("{1}", String.valueOf(res.imt0))
                                  .replace("{2}", String.valueOf(res.imt1))
                                  .replace("{3}", String.valueOf(res.hips))
                                  .replace("{4}", String.valueOf(res.growth / 2))
                                  ;
            _crlResW.setText(Html.fromHtml(resw));
            _crlResW.setMovementMethod(LinkMovementMethod.getInstance());
        }
        catch (Exception ex)
        {
            _crlResW.setText(ex.getMessage());
        }

        //_crlResH.setText("твой:" + );
    }

    public void OnClickSave(View view){
        DBHelper.SaveResults(GetGrowth(), GetHips(), GetWeight());
        DBHelper.SaveSettings(GetBD());
        refresh();
        initCalendar();
    }

    public void OnClickBirthday(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.title_birthday));
        final DatePicker input = new DatePicker(this);
        input.setCalendarViewShown(false);

        alert.setView(input);

        alert.setPositiveButton(getString(R.string.title_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(input.getYear(), input.getMonth(), input.getDayOfMonth());

                SetBD(calendar.getTime());
            }
        });

        alert.setNegativeButton(getString(R.string.title_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    };

}
