package kultalaaki.vpkapuri;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kultalaaki.vpkapuri.services.SMSBackgroundService;

public class AlertActivity extends AppCompatActivity {

    private String header;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        textView = findViewById(R.id.header);

        Intent intent = getIntent();
        header = intent.getStringExtra("header");
    }

    @Override
    public void onStart() {
        super.onStart();
        textView.setText(header);
    }

    public void openAlarmPage(View view) {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void silenceAlarm(View view) {
        stopAlarm();
    }

    private void stopAlarm() {
        Intent stopAlarm = new Intent(this, SMSBackgroundService.class);
        stopService(stopAlarm);
    }

    public void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }
}