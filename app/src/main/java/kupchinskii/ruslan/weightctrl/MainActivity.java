package kupchinskii.ruslan.weightctrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.Random;

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
        if(!val.equals(""))
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

    TextView _crlResW;

    private GraphicalView chartViewWeight;
    private GraphicalView chartViewHips;

    LinearLayout layoutW;
    LinearLayout layoutH;

    CustomSeekBar sbWeight;

    CalendarAdapter calendarAdapter;

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _crlGrowth = (TextView) findViewById(R.id.et_gh);
        _crlWeight = (TextView) findViewById(R.id.et_wg);
        _crlHips = (TextView) findViewById(R.id.et_hp);

        _crlResW = (TextView) findViewById(R.id.et_resw);

        layoutW = (LinearLayout) findViewById(R.id.charWeight);
        layoutH = (LinearLayout) findViewById(R.id.charHips);

        sbWeight = (CustomSeekBar) findViewById(R.id.sbWeight);

        Helpers.Settings = getSharedPreferences(Helpers.SETTING_FILE, this.MODE_PRIVATE);
        PreferencesHelper.init(getSharedPreferences(PreferencesHelper.APP_PREFERENCES, Context.MODE_PRIVATE));

        DB.initDB(this);
        initCalendar();
        refresh();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initDataToSeekBar();
    }

    private void initDataToSeekBar() {
        ArrayList<ProgressItem> progressItemList = new ArrayList<ProgressItem>();

        // от 13 до 45

        ProgressItem mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float)9.375;
        mProgressItem.color = R.color.color16;
        progressItemList.add(mProgressItem);
        // blue span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float)7.8125;
                ;
        mProgressItem.color = R.color.color185;
        progressItemList.add(mProgressItem);
        // green span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float) 20;
        mProgressItem.color = R.color.color25;
        progressItemList.add(mProgressItem);

        //white span
        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float) 15.9375;
        mProgressItem.color =  R.color.color30;
        progressItemList.add(mProgressItem);

        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float) 15.625;
        mProgressItem.color =  R.color.color35;
        progressItemList.add(mProgressItem);

        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float) 15.625;
        mProgressItem.color =  R.color.color40;
        progressItemList.add(mProgressItem);

        mProgressItem = new ProgressItem();
        mProgressItem.progressItemPercentage = (float) 15.625;
        mProgressItem.color =  R.color.color45;
        progressItemList.add(mProgressItem);


        sbWeight.initData(progressItemList);
        sbWeight.invalidate();
        sbWeight.setEnabled(false);

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

    private void initMonthName() {
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

        String template = getString(R.string.res_html);
        try {
            sbWeight.setProgress((int)( (res.imt - 13.0) * 3.125 ));
            String resw = template.replace("{0}", String.valueOf(res.imt))
                                  .replace("{3}", String.valueOf(res.hips))
                                  .replace("{4}", String.valueOf(res.hipsNorm))
                                  ;
            _crlResW.setText(Html.fromHtml(resw));
            _crlResW.setMovementMethod(LinkMovementMethod.getInstance());
        }
        catch (Exception ex)
        {

        }
    }

    public void OnClickSave(View view){
        DBHelper.SaveResults(GetGrowth(), GetHips(), GetWeight());
        refresh();
        initCalendar();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void OnClickExit(View view) {

        if (!PreferencesHelper.GetNoShowPropO()) {
            Random rnd = new Random();
            if (rnd.nextInt(31) == 7) {
                Intent intent = new Intent(MainActivity.this, end_prg.class);
                startActivity(intent);
            }
        }
        finish();
    }

}
