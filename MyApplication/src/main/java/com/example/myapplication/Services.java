package com.example.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Atif on 30/05/13.
 */
public class Services extends Service {

    String number,message;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        message = intent.getExtras().getString("cha");
        number = intent.getExtras().getString("number");
        Log.d("Value@onStartCommand", "" + message + number);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        sendSMS(number);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void sendSMS(String phoneNumber) {
        // TODO Auto-generated method stub
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        Log.d("SMS Service", "Seding SMS...");
		 /*PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
	        new Intent(SENT), 0);*/

	   /* PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
	        new Intent(DELIVERED), 0);*/



        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                //arg0.unregisterReceiver(arg0);
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                //arg0.unregisterReceiver(this);
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        /*ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sentPI = new ArrayList<PendingIntent>();
        for (int i=0; i < parts.size(); i++){

            PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
            sentPI.add(pi);
        }

        ArrayList<PendingIntent> deliveredPI = new ArrayList<PendingIntent>();
        for (int i=0; i < parts.size(); i++){

            PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_CANCEL_CURRENT);
            deliveredPI.add(pi);
        }*/
        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"Service Destroyed",Toast.LENGTH_SHORT).show();
    }
}
