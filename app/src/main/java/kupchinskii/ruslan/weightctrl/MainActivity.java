package kupchinskii.ruslan.weightctrl;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView _crlGrowth;

    private Integer str2int(String val){
        Integer res = null;
        if(val != null && !val.equals(""))
            res = Integer.parseInt(val);
        return  res;
    }

    private Integer GetGrowth() {
        return  str2int(_crlGrowth.getText().toString());
    }

    private void SetGrowth(Integer val) {
        if(val != null)
            _crlGrowth.setText(String.valueOf(val));
        else
            _crlGrowth.setText(null);
    }

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
            _crlWeight.setText(String.valueOf(val / 10));
        else
            _crlWeight.setText(null);
    }

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

    TextView _crlResW;
    TextView _crlResH;

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

        DB.initDB(this);

        RefreshCurrent();
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

}
