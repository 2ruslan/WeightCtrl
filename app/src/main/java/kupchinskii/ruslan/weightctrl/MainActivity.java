package kupchinskii.ruslan.weightctrl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB.initDB(this);

        DBHelper.SaveSettings(1, new Date(10000000));
        DBHelper.SaveResults(100, 100, 100);
        DBHelper.GetReusultsAll();
        DBHelper.GetSettings();
    }
}
