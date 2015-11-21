package kupchinskii.ruslan.weightctrl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView _crlGrowth;
    private int GetGrowth()
    {
        return Integer.parseInt(_crlGrowth.getText().toString());
    }
    private void SetGrowth(int val)
    {
        _crlGrowth.setText(String.valueOf(val));
    }

    TextView _crlSex;
    private int GetSex()
    {
        return Integer.parseInt(_crlSex.getText().toString());
    }
    private void SetSex(int val)
    {
        _crlSex.setText(String.valueOf(val));
    }

    TextView _crlWeight;
    private int GetWeight()
    {
        return Integer.parseInt(_crlWeight.getText().toString());
    }
    private void SetWeight(int val)
    {
        _crlWeight.setText(String.valueOf(val));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _crlGrowth = (TextView) findViewById(R.id.et_gh);
        _crlSex = (TextView) findViewById(R.id.et_sx);
        _crlWeight = (TextView) findViewById(R.id.et_wg);


        DB.initDB(this);

        DBHelper.SaveSettings(1, new Date(10000000));
        DBHelper.SaveResults(100, 100, 100);
        DBHelper.GetReusultsAll();
        DBHelper.GetSettings();

        DBHelper.result res =  DBHelper.GetReusultLast();
        SetGrowth(res.growth);
        SetWeight(res.weight);

    }
}
