package kupchinskii.ruslan.weightctrl;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.achartengine.GraphicalView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {

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


    SeekBar sbWeight;

    TextView _crlResW;
    TextView _crlResH;

    private GraphicalView chartViewWeight;
    private GraphicalView chartViewHips;

    private final String[] columns = new String[]{
            DB.C_RES_ONDATE,
            DB.C_RES_WEIGHT
    };

    private final int[] to = new int[]{
            R.id.lrv_ondate,
            R.id.lrv_weight
    };

    String idMes;
    String nameMes;
    ListView listView;
    Cursor cursor;
    private SimpleCursorAdapter dataAdapter;

    static final int DATE_DIALOG_ID = 0;
    static final int PICK_DATE_REQUEST = 1;

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

        sbWeight = (SeekBar) findViewById(R.id.sb_weight);

        listView = (ListView) findViewById(R.id.lv_results);

        /**/
/*
        seekbar.setThumb(new BitmapDrawable(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.seekbar_progress_thumb)));

        ShapeDrawable thumb = new ShapeDrawable(new RectShape());
        thumb.getPaint().setColor(Color.rgb(0, 0, 0));
        thumb.setIntrinsicHeight(-80);
        thumb.setIntrinsicWidth(30);
        sbWeight.setThumb(thumb);
        */

        /**/


        DB.initDB(this);
        initListView();
        RefreshCurrent();

        loadAdMob();


        /***/
        final GridView g = (GridView) findViewById(R.id.gvCalendar);
        CalendarAdapter mAdapter = new CalendarAdapter(getApplicationContext(),R.layout.item_calendar, 2015, 10);
        g.setAdapter(mAdapter);
        /**/

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (chartViewWeight == null) {

            LinearLayout layoutW = (LinearLayout) findViewById(R.id.charWeight);

            chartViewWeight = ChartHelper.buildChartWeight(getBaseContext());
            layoutW.addView(chartViewWeight, new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            LinearLayout layoutH = (LinearLayout) findViewById(R.id.charHips);

            chartViewHips = ChartHelper.buildChartWeight(getBaseContext());
            layoutH.addView(chartViewHips, new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        }
     else
        {
            chartViewWeight.repaint();
        }
    }

    private void RefreshCurrent() {
        DBHelper.result res = DBHelper.GetReusultLast();
        SetGrowth(res.growth);
        SetWeight(res.weight);
        SetHips(res.hips);
        SetBD(res.birthday);

        // ---------------------
        try {
            String resw = "";
            if (res.imt < res.imt0)
                resw = "недостаток веса";
            else if ((res.imt >= res.imt0 && res.imt < res.imt1))
                resw = "норма";
            else if ((res.imt >= res.imt1 && res.imt < res.imt2))
                resw = "чуть перебор";
            else if ((res.imt >= res.imt2))
                resw = "перебор";

            resw = resw + " твой:" + String.valueOf(res.imt) + "  max:" + String.valueOf(res.imt1);
            _crlResW.setText(resw);
        }
        catch (Exception ex)
        {

        }

        //_crlResH.setText("твой:" + );
    }


    public void OnClickSave(View view){
        DBHelper.SaveResults(GetGrowth(), GetHips(), GetWeight());
        DBHelper.SaveSettings(GetBD());
    }


    public void OnClickBirthday(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme);

        alert.setTitle("test");
        final DatePicker input = new DatePicker(this);
        input.setCalendarViewShown(false);

        alert.setView(input);

        //alert.setPositiveButton(R.string.title_save, new DialogInterface.OnClickListener() {
        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(input.getYear(), input.getMonth(), input.getDayOfMonth());

                SetBD(calendar.getTime());
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    };



    /**/
    private void refreshListView() {
        cursor = DBHelper.GetReusultsAll();
        dataAdapter.changeCursor(cursor);
    }

    private void initListView() {

        cursor = DBHelper.GetReusultsAll();

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.list_results_val,
                cursor,
                columns,
                to,
                0);

        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                /*
                OpenEditor(
                        cursor.getString(cursor.getColumnIndexOrThrow(DB.COLUMN_VALUES_EXID))
                        , cursor.getString(cursor.getColumnIndexOrThrow(DB.COLUMN_VALUES_ID))
                        , cursor.getString(cursor.getColumnIndexOrThrow(DB.COLUMN_VALUES_CNT_REAL))
                );*/
            }
        });
    }
    /**/


    private void loadAdMob()
    {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("42BC762C13DEBCCD86629DD16A42C588")
                    .addTestDevice("B346B741A6C330AE8DFEF31DFC8423FE")
                    .build();
        mAdView.loadAd(adRequest);
    }
}
