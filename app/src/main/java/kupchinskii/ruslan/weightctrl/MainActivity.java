package kupchinskii.ruslan.weightctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    static public Integer str2int(String val){
        Integer res = null;
        try {
            if (val != null && !val.equals(""))
                res = Integer.parseInt(val);
        }
        catch (Exception ex) {};
        return  res;
    }

    static public Integer getWeight(String val) {
        Double res = 0.0;
        try {
            if (!val.equals(""))
                res = Double.parseDouble(val);

            if (res != null) {
                res *= 10;
            }
        }catch (Exception ex) {}

        return (int)Math.round(res);
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
        return getWeight(_crlWeight.getText().toString());
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
    TextView _crlResH;

    private GraphicalView chartViewWeight;
    private GraphicalView chartViewHips;

    RelativeLayout layoutW;
    RelativeLayout layoutH;

    View layoutHolderW;
    View layoutHolderH;
 //   View holderResCtrl;

    CustomSeekBar sbWeight;

    CalendarAdapter calendarAdapter;
    GridView g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _crlGrowth = (TextView) findViewById(R.id.et_gh);
        _crlWeight = (TextView) findViewById(R.id.et_wg);
        _crlHips = (TextView) findViewById(R.id.et_hp);

        _crlResW = (TextView) findViewById(R.id.et_resw);
        _crlResH = (TextView) findViewById(R.id.et_resh);

        layoutW = (RelativeLayout) findViewById(R.id.charWeight);
        layoutH = (RelativeLayout) findViewById(R.id.charHips);

        layoutHolderW = findViewById(R.id.holderCharWeight);
        layoutHolderH = findViewById(R.id.holderCharHips);
 //       holderResCtrl = findViewById(R.id.holderResCtrl);


        sbWeight = (CustomSeekBar) findViewById(R.id.sbWeight);

        g = (GridView) findViewById(R.id.gvCalendar);

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

        initCalendar();
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception ex)
        {}
    }


    //region calendar

    private void initCalendar()
    {

        calendarAdapter = new CalendarAdapter(getApplicationContext(),R.layout.item_calendar);
        g.setAdapter(calendarAdapter);
        initMonthName();

        g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CalendarItem itm = calendarAdapter.getItem(position);
                if (itm.isDay) {
                    /**/

                    View frg = View.inflate(MainActivity.this, R.layout.day_edit, null);
                    final EditText ed_hips = (EditText) frg.findViewById(R.id.ed_hips);
                    final EditText ed_weight = (EditText) frg.findViewById(R.id.ed_weight);

                    ed_weight.setText(itm.weight);
                    ed_hips.setText(itm.Hips);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialogTheme);


                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

                    builder.setTitle(dateFormat.format(itm.date));

                    builder
                            .setView(frg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.title_save, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String th = ed_hips.getText().toString();
                                    String tw = ed_weight.getText().toString();

                                    DBHelper.SaveResults(null, MainActivity.str2int(th), MainActivity.getWeight(tw), itm.date);
                                    MainActivity.this.refresh();
                                }
                            })
                            .setNegativeButton(R.string.title_exit, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).show();

                    /**/
                }
            }
        });
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


        if (chartViewWeight != null) {
            layoutHolderW.setVisibility(View.VISIBLE);
//            holderResCtrl.setVisibility(View.VISIBLE);
            layoutW.addView(chartViewWeight, new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        else {
            layoutHolderW.setVisibility(View.GONE);
//            holderResCtrl.setVisibility(View.VISIBLE);
        }

        chartViewHips = ChartHelper.buildChartHips(getBaseContext());
        layoutH.removeAllViews();

        if(chartViewHips != null) {
            layoutHolderH.setVisibility(View.VISIBLE);
            layoutH.addView(chartViewHips, new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        else{
            layoutHolderH.setVisibility(View.GONE);
        }
    }

    private void RefreshCurrent() {
        DBHelper.result res = DBHelper.GetReusultLast();
        SetGrowth(res.growth);
        SetWeight(res.weight);
        SetHips(res.hips);

        String template = getString(R.string.res_html);
        String templateH = getString(R.string.res_html_h);
        try {
            if (res.imt >0 ) {
                sbWeight.setProgress((int) ((res.imt - 13.0) * 3.125));

                double wMax = (res.growth / 100.0) * (res.growth / 100.0) * 25.0;
                double wMin = (res.growth / 100.0) * (res.growth / 100.0) * 18.5;
                String resw = template.replace("{0}", String.valueOf(res.imt))
                        .replace("{3}", String.valueOf(res.hips))
                        .replace("{4}", String.valueOf(res.hipsNorm))
                        .replace("{6}", String.valueOf( Math.round(wMax)))
                        .replace("{5}", String.valueOf(Math.round(wMin)));
                _crlResW.setText(Html.fromHtml(resw));
                _crlResW.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if(res.hips >0) {
                String resh = templateH.replace("{0}", String.valueOf(res.imt))
                        .replace("{3}", String.valueOf(res.hips))
                        .replace("{4}", String.valueOf(res.hipsNorm));
                _crlResH.setText(Html.fromHtml(resh));
                _crlResH.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        catch (Exception ex)
        {

        }
    }

    public void OnClickSave(View view){
        DBHelper.SaveResults(GetGrowth(), GetHips(), GetWeight());
        refresh();

    }

    public void OnClickExit(View view) {

        if (!PreferencesHelper.GetNoShowPropO()) {
            Random rnd = new Random();
            if (rnd.nextInt(21) == 7) {
                Intent intent = new Intent(MainActivity.this, end_prg.class);
                startActivity(intent);
            }
        }
        finish();

    }

}
