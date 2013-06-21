package com.example.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends Activity implements View.OnClickListener {

    NfcAdapter mAdapter;
    boolean mInWriteMode;
    TextView mTextView;
    Button mWriteTagButton;
    EditText number;
    String numberString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textText);
        number = (EditText) findViewById(R.id.numberEt);

        // grab our NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // button that starts the tag-write procedure
        mWriteTagButton = (Button)findViewById(R.id.write_tag_button);
        mWriteTagButton.setOnClickListener(this);

    }

    private void enableWriteMode() {
        mInWriteMode = true;

        //Intent i = new Intent(this,NFCTestActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //i.putExtra("nfcMessage", "My test message");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] intentFilters = new IntentFilter[]{tagDetected};

        mAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);

    }


    public void onNewIntent(Intent intent){
        if (mInWriteMode){
            mInWriteMode = false;

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            //String nfcMessage = intent.getStringExtra("nfcMessage");

            try {
                writeTag(tag, numberString);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean writeTag(Tag tag, String nfcMessage) throws IOException , FormatException{
        //Record to launch Play Store if not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(this.getPackageName());

        // Record with actual data we care about
        byte[] payload = nfcMessage.getBytes();
        byte[] mimeBytes = MimeType.NFC_DEMO.getBytes(Charset.forName("US-ASCII"));
        NdefRecord cardRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes,
                new byte[0], payload);
        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] { cardRecord, appRecord});

        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();


        /*try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if (ndef != null){
                ndef.connect();

                if (!ndef.isWritable()){
                    displayMessage("Read-Only tag.");
                    return false;
                }

                //Work out how much space is left for data
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size){
                    displayMessage("Tag don't have enough free space");
                    return false;
                } else {
                    //attempt to format tag
                    NdefFormatable format = NdefFormatable.get(tag);
                    if (format !=  null){

                        try {
                            format.connect();
                            format.format(message);
                            displayMessage("Tag written successfully");
                        } catch (FormatException e) {
                            displayMessage("Unable to format tag to NDEF.");
                            return false;
                            //e.printStackTrace();
                        }
                    } else {
                        displayMessage("Tag doesn't appear to support NDEF format.");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            displayMessage("Failed to write tag");
            e.printStackTrace();
        }*/
        return false;
    }

    private String getRandomConsole() {
        double rnd = Math.random();
        if(rnd<0.25) return "nes";
        else if(rnd<0.5) return "snes";
        else if(rnd<0.75) return "megadrive";
        else return "mastersystem";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.write_tag_button) {
            displayMessage("Touch and hold tag against phone to write.");
            if (number.getText().toString() != ""){
                numberString = number.getText().toString();
                enableWriteMode();
            } else {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void displayMessage(String s) {
        mTextView.setText(s);
    }

}
