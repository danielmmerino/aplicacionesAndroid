
package com.example.daniel.tesispaul;
import android.app.PendingIntent;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    NfcAdapter nfcAdapter;

    ToggleButton tglReadWrite;
    EditText txtTagContent;

    Button btnDescargar;

    public String numeroH="";
    public String nombre="";
    public String direccion = "";
    public String telefonoT = "";
    public String representante = "";
    public String institucion  = "";
    public String cedula  = "";
    public String  nacimiento = "";
    public String  estadoCivil = "";
    public String  instruccion = "";
    public String  procedencia = "";


    public String tx1="Número Historia:        ";
    public String tx2="Nombre del paciente: ";
    public String tx3="Dirección del paciente: ";
    public String tx4="Teléfono del trabajo:  ";
    public String tx5="Nombre del representante: ";
    public String tx6="Institución:";
    public String tx7="Cédula de ciudadanía: ";
    public String tx8="Fecha de nacimiento: ";
    public String tx9="Estado civil: ";
    public String tx10="Instrucción:       ";
    public String tx11="Procedencia:  ";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tglReadWrite = (ToggleButton)findViewById(R.id.tglReadWrite);
        txtTagContent = (EditText)findViewById(R.id.txtTagContent);


    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "Recibiendo", Toast.LENGTH_SHORT).show();


            if(tglReadWrite.isChecked())
            {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                }else{
                    Toast.makeText(this, "No se encuentra tecnologia NFC!", Toast.LENGTH_SHORT).show();
                }

            }else{
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


            }

        }
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        String [] parte;


        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            parte = tagContent.split("@");
            String parte1 =parte [0];
            String parte2 =parte [1];

            if (nombre == "" || numeroH == "" || direccion == "" || telefonoT =="" || representante ==""  || nacimiento =="" || cedula == ""  ) {

                if (parte1.equals("1")) {
                    numeroH = parte2;
                } else if (parte1.equals("2")) {
                    nombre = parte2;
                } else if (parte1.equals("3")){
                    direccion = parte2;
                } else if (parte1.equals("4")){
                    telefonoT = parte2;
                } else if (parte1.equals("5")){
                    representante = parte2;
                } else if (parte1.equals("6")){
                    nacimiento = parte2;
                } else if (parte1.equals("7")){
                    cedula = parte2;
                }

                txtTagContent.setTextSize(1,22);
                txtTagContent.setTextColor(Color.rgb(240,75,24));
                txtTagContent.setText("*** RECIBIENDO INFORMACION ***  \n  \n \n ..........POR FAVOR ESPERE......");
            }

            else {
                txtTagContent.setText(txtTagContent.getText()+parte1);
                txtTagContent.setTextSize(1,17);
                txtTagContent.setTextColor(Color.rgb(94,79,137));
                txtTagContent.setText(tx1+numeroH + "\n" +tx2+ nombre + "\n" +tx3 +direccion+ "\n" +tx4 +telefonoT+ "\n" +tx5 +representante+ "\n"+tx8 +nacimiento+ "\n"+tx7 +cedula+ "\n" + "\n" + "\n" + "\n" + "\n" + "\n" +"Descargar ficha completa");



                Toast.makeText(this, "COMPLETADO", Toast.LENGTH_LONG).show();
            }



            // txtTagContent.setText(txtTagContent.getText()+tagContent);
        }else
        {
            Toast.makeText(this, "Vuelva a acercar los dispositivos!", Toast.LENGTH_SHORT).show();
        }

    }




    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag writen!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }







    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

}
