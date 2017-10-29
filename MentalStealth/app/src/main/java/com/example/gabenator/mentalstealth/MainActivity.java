package com.example.gabenator.mentalstealth;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Messenger;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.provider.UserDictionary;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;

import com.aylien.textapi.TextAPIClient;
import com.aylien.textapi.parameters.*;
import com.aylien.textapi.responses.*;
import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    ImageView firstNumber;
    ImageView secondNumber;
    ImageView thirdNumber;
    ImageView fourthNumber;

    Button number0;
    Button number1;
    Button number2;
    Button number3;
    Button number4;
    Button number5;
    Button number6;
    Button number7;
    Button number8;
    Button number9;

    Button enter;
    Button back;

    private int pinCount;

    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver smsSender;
    private int MY_PERMISIONS_REQUEST_SMS_SENT = 10;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;
    TextAPIClient client;
    ArrayList<Message> messages;
    int [] pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pinCount = 0;
        pin = new int[]{1,2,3,4};

        firstNumber = (ImageView) findViewById(R.id.pinNum1);
        secondNumber = (ImageView) findViewById(R.id.pinNum2);
        thirdNumber = (ImageView) findViewById(R.id.pinNum3);
        fourthNumber = (ImageView) findViewById(R.id.pinNum4);

        number0 = (Button) findViewById(R.id.number0);
        number1 = (Button) findViewById(R.id.number1);
        number2 = (Button) findViewById(R.id.number2);
        number3 = (Button) findViewById(R.id.number3);
        number4 = (Button) findViewById(R.id.number4);
        number5 = (Button) findViewById(R.id.number5);
        number6 = (Button) findViewById(R.id.number6);
        number7 = (Button) findViewById(R.id.number7);
        number8 = (Button) findViewById(R.id.number8);
        number9 = (Button) findViewById(R.id.number9);

        enter = (Button) findViewById(R.id.enterPin);
        back = (Button) findViewById(R.id.back);

        messages = new ArrayList<Message>();

        client = new TextAPIClient("bc827ea5","17469033340d7b06b76b350e5e2477f1");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_SMS_RECEIVE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, MY_PERMISIONS_REQUEST_SMS_SENT);




        //showLastText();
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
        //unregisterReceiver(smsReceiver);
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
                    }
                }
            }
        };
    }

    public void enterButtonHit(View v) {
        //Toast.makeText(getApplicationContext(), "Bout to go into next activity!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, ViewTexts.class);
        startActivity(intent);
    }

    private void registerSmsReceiver() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

    private void showLastText() {
        ContentResolver resolver = getContentResolver();
        String [] columns = new String[] {"date_sent", "type", "body","address"};
        String [] clause = new String[] {Integer.toString(0)};
        Cursor cursor = resolver.query(Uri.parse("content://sms"), columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Message m;
                int date = cursor.getInt(0);
                int type = Integer.parseInt(cursor.getString(1));
                String body = cursor.getString(2);
                String creator = cursor.getString(3);
                m = new Message(type, body, date, creator);
                messages.add(m);
            } while (cursor.moveToNext());
        }
    }

    private String getContactName(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        String project[] = new String[] {ContactsContract.Data.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, project, null, null, null);
        if(cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return "unknown number";
        }
    }


}
