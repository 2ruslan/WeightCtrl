package kupchinskii.ruslan.weightctrl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.drive.Drive;

public class backup extends AppCompatActivity {

//    Builder mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

 /*       mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
                */

        setContentView(R.layout.activity_backup);
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    */
/*
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
    */
}
