package com.example.gabenator.mentalstealth;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.*;
import com.aylien.textapi.responses.*;


public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver smsSender;
    private int MY_PERMISIONS_REQUEST_SMS_SENT = 10;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    TextAPIClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new TextAPIClient("bc827ea5","17469033340d7b06b76b350e5e2477f1");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, MY_PERMISIONS_REQUEST_SMS_SENT);

        initializeSMSReceiver();
        registerSmsReceiver();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //ContentResolver contentResolver = getContentResolver();
        //contentResolver.registerContentObserver(Uri.parse("content://sms"), true, new SentObserver(new Handler(), contentResolver));
    }

    public void sentHandler() {
        System.out.println("New text message sent");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    private void initializeSMSReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if(bundle != null) {
                    Object [] pdus = (Object []) bundle.get("pdus");
                    for(int i = 0; i < pdus.length; i++) {
                        byte[] pdu = (byte []) pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu, "3gpp");
                        String text = message.getDisplayMessageBody();
                        String sender = message.getOriginatingAddress();

                        SentimentParams.Builder builder = SentimentParams.newBuilder();
                        builder.setText(text);
                        builder.setMode("tweet");
                        Sentiment sentiment = null;
                        try {
                            sentiment = client.sentiment(builder.build());
                        } catch(Exception e) {
                            Log.d("TAG", e.toString());
                        }
                        Log.d("TAG", sentiment.getText());
                        Toast toast = Toast.makeText(context, "Sentiment: " + sentiment.getText() + "\nSender: " + sender, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        };
    }

    private void registerSmsReceiver() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

}
