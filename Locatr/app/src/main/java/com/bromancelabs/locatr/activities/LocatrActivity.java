package com.bromancelabs.locatr.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import com.bromancelabs.locatr.fragments.LocatrFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LocatrActivity extends SingleFragmentActivity {
    private static final int REQUEST_ERROR = 0;
    private GoogleApiAvailability mGoogleInstance = GoogleApiAvailability.getInstance();

    @Override
    protected Fragment createFragment() {
        return LocatrFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServices();
    }

    private void checkGooglePlayServices() {
        int resultCode = mGoogleInstance.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS != resultCode) {
            if (mGoogleInstance.isUserResolvableError(resultCode)) {
                Dialog errorDialog = mGoogleInstance.getErrorDialog(this, resultCode, REQUEST_ERROR);

                if (null != errorDialog) {
                    errorDialog.show();
                    errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                }
            }
        }
    }
}
