package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Etusivu extends AppCompatActivity {

    Button halytysSivu;
    Button arkisto;
    Button ohjeita;
    Button asetukset;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etusivu);

        halytysSivu = (Button) findViewById(R.id.halytysSivu);
        arkisto = (Button) findViewById(R.id.arkisto);
        ohjeita = (Button) findViewById(R.id.ohjeita);
        asetukset = (Button) findViewById(R.id.asetukset);
        pyydaLuvatTiedostot();
        //Intent serviceIntent = new Intent(Etusivu.this, SmsBroadcastReceiver.class);
        //startService(serviceIntent);
        //broadcastreceiver();
        //powersavingsThing();

        halytysSivu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Etusivu.this, aktiivinenHaly.class);
                startActivity(intent2);
            }
        });

        arkisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentark = new Intent(Etusivu.this, ArkistoActivity.class);
                startActivity(intentark);
            }
        });

        ohjeita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentohj = new Intent(Etusivu.this, OhjeitaActivity.class);
                startActivity(intentohj);
            }
        });

        asetukset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentase = new Intent(Etusivu.this, SettingsActivity.class);
                startActivity(intentase);
            }
        });
    }

    public void pyydaLuvatTiedostot() {
        if (ContextCompat.checkSelfPermission(Etusivu.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Etusivu.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et voi asettaa soittoääntä jos et anna lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                return;

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Etusivu.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Etusivu.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    /*public void broadcastreceiver() {
        PackageManager pm = getPackageManager();
        ComponentName compName = new ComponentName(this,
                SmsBroadcastReceiver.class);
        pm.setComponentEnabledSetting(
                compName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }*/

    /*public void powersavingsThing(){
        Intent intent = new Intent();
        String packageName = getApplicationContext().getPackageName();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }*/
}
