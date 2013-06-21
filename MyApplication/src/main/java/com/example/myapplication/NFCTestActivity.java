package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Atif on 29/05/13.
 */
public class NFCTestActivity extends Activity {
    private ImageView mCardView;
    private TextView tv;

    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs;

        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("Check", Context.MODE_PRIVATE);
        boolean checkin = prefs.getBoolean("checkIn",true);
        Intent intent = getIntent();
        if(intent.getType() != null && intent.getType().equals(MimeType.NFC_DEMO)) {
            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            Log.d("rawMsgs", "" + rawMsgs);
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            Log.d("msg" , " " + msg);
            NdefRecord numberRecord = msg.getRecords()[0];
            String number = new String(numberRecord.getPayload());
            Log.d("consoleName " , number);

            //Get current date and time
            Calendar calendar = Calendar.getInstance();
            int seconds = calendar.get(Calendar.SECOND);
            int minutes = calendar.get(Calendar.MINUTE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);

            //Date formatter
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String date = simpleDateFormat.format(calendar.getTime());

            //Compiling message
            String check;
            if (checkin){
                check = "Check in time \n" + hours + ":" + minutes +  ":" + seconds + "\n" + date;
            } else {
                check = "Check out time \n" + hours + ":" + minutes +  ":" + seconds + "\n" + date;
            }
            Intent i = new Intent(this,Services.class);
            Log.d("@NFC", ""+ check);
            i.putExtra("cha",check);
            i.putExtra("number", number);
            checkin = !checkin;//invert the value
            prefs.edit().putBoolean("checkIn",checkin).commit();//save the inversion
            this.startService(i);
            this.stopService(i);
            finish();
            displayCard(number);
        }
    }

    private void displayCard(String consoleName) {
        //tv.setText("asdfsdf ");
       // Toast.makeText(this,consoleName,Toast.LENGTH_SHORT).show();
        /*int cardResId = 0;
        if(consoleName.equals("nes")) cardResId = R.drawable.nes;
        else if(consoleName.equals("snes")) cardResId = R.drawable.snes;
        else if(consoleName.equals("megadrive")) cardResId = R.drawable.megadrive;
        else if(consoleName.equals("mastersystem")) cardResId = R.drawable.mastersystem;
        mCardView.setImageResource(cardResId);*/
    }
}
