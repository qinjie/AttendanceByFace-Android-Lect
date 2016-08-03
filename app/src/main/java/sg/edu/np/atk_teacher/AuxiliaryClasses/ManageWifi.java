package sg.edu.np.atk_teacher.AuxiliaryClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;

/**
 * Created by Champ on 02/08/2016.
 */
public class ManageWifi {

    static Activity activity;

    private static boolean isWifiOn() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(activity.WIFI_SERVICE);
        return(wifiManager.isWifiEnabled());
    }

    private static void turnWifiOn() {
        WifiManager wifiManager = (WifiManager) activity.getSystemService(activity.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public static void askToTurnOnWifi(final Activity _activity) {
        activity = _activity;

        if(isWifiOn())
            return;
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Internet connection");
        builder.setMessage("This action requires internet! Do you want to turn your wifi on?");
        builder.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                turnWifiOn();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}
