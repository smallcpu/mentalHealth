package com.example.gabenator.mentalstealth;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import static android.R.attr.phoneNumber;

/**
 * Created by Gabenator on 10/28/2017.
 */

public class SentObserver extends ContentObserver {

    private String lastSmsId;
    ContentResolver contentResolver;

    public SentObserver(Handler handler, ContentResolver cr) {
        super(handler);
        this.contentResolver = cr;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = contentResolver.query(uriSMSURI, null, null, null, null);
        cur.moveToNext();
        String id = cur.getString(cur.getColumnIndex("_id"));
        if (smsChecker(id)) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String message = cur.getString(cur.getColumnIndex("body"));
            System.out.println("message");
        }
    }

    // Prevent duplicate results without overlooking legitimate duplicates
    public boolean smsChecker(String smsId) {
        boolean flagSMS = true;

        if (smsId.equals(lastSmsId)) {
            flagSMS = false;
        }
        else {
            lastSmsId = smsId;
        }

        return flagSMS;
    }
}
