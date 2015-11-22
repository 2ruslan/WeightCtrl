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

    private int GetGrowth() {
        return Integer.parseInt(_crlGrowth.getText().toString());
    }

    private void SetGrowth(int val) {
        _crlGrowth.setText(String.valueOf(val));
    }

    TextView _crlSex;

    private int GetSex() {
        return Integer.parseInt(_crlSex.getText().toString());
    }

    private void SetSex(int val) {
        _crlSex.setText(String.valueOf(val));
    }

    TextView _crlWeight;

    private int GetWeight() {
        return Integer.parseInt(_crlWeight.getText().toString());
    }

    private void SetWeight(int val) {
        _crlWeight.setText(String.valueOf(val));
    }

    TextView _crlBD;
    Date _dateBD;
    private Date GetBD() {
        return _dateBD;
    }
    private void SetBD(Date val) {
        _dateBD = val;
        _crlBD.setText(DateFormat.getDateInstance(DateFormat.SHORT).format(val));
    }

    TextView _crlHips;
    private int GetHips() {
        return Integer.parseInt(_crlHips.getText().toString());
    }
    private void SetHips(int val) {
        _crlHips.setText(String.valueOf(val));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _crlGrowth = (TextView) findViewById(R.id.et_gh);
        _crlSex = (TextView) findViewById(R.id.et_sx);
        _crlWeight = (TextView) findViewById(R.id.et_wg);
        _crlHips = (TextView) findViewById(R.id.et_hp);
        _crlBD = (TextView) findViewById(R.id.et_bd);



        DB.initDB(this);

        RefreshCurrent();
    }

    private void RefreshCurrent() {
        DBHelper.result res = DBHelper.GetReusultLast();
        SetGrowth(res.growth);
        SetWeight(res.weight);
        SetHips(res.hips);
        SetSex(res.sex);
        SetBD(res.birthday);
    }

    public void OnClickSave(View view){
        DBHelper.SaveResults(GetGrowth(), GetHips(), GetWeight());
        DBHelper.SaveSettings(GetSex(), GetBD());
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
