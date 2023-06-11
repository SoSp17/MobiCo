package de.spas.mi_04;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This Class is for the Options-Menue at the top of the app. With her we can set our IP-address, and change between the mqtt and accelerometer functionality.
 * @see AppCompatActivity
 * @see View.OnClickListener
 * @see Button
 * @see EditText
 * @see RadioButton
 * @see RadioGroup
 * @see SharedPreferences
 * @see String
 */
public class Optionen extends AppCompatActivity implements View.OnClickListener {
    Button bestButton;
    EditText ip_addr;
    RadioButton rb_acc;
    RadioButton rb_mqtt;
    RadioGroup auswahl;
    Switch sw_sound;

    SharedPreferences preferences;
    final String pref = "pref";
    final String mqtt = "mqtt";
    final String acce = "accelerometer";
  String sound="ON";
    String ip_add = "192.168.0.79";
    String ip_new;
    String ip_addresse = "ip";
    String ip_a = "tcp://";
    String ip_e = ":1883";

    /**
     * This method is called if the app is opened for the first time. In her we set the content of the View, and connect some variables with the elements of the layout
     * because else we cant have an interaction.
     * @param savedInstanceState this parameter is used to save information when the User ends the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.optionen);
        bestButton = (Button)findViewById(R.id.button_best);

        //setzen eines onClickListeners auf den Button Auswahl bestätigen
        bestButton.setOnClickListener(this);
        ip_addr = (EditText)findViewById(R.id.eT_ip);
        rb_acc = (RadioButton)findViewById(R.id.rb_Acc);
        rb_mqtt = (RadioButton)findViewById(R.id.rB_mqtt);
        auswahl = (RadioGroup)findViewById(R.id.rg_auswahl);
        sw_sound= (Switch) findViewById(R.id.switch1);

    }
//zwei auswahlknöpfe durch einen switch ersetzen,intern default
    /**
     * This method is called from onClick. In it we write the values passed by the user in the layout into the SharedPreferences, so we can use them in the MainActivity
     * @see SharedPreferences
     */
    public void load_options(){
        //Abrufen der shared Preferences
        preferences = getSharedPreferences(pref,MODE_PRIVATE);

        //Erstellen eines Editors der das updaten bzw. löschen eingetragener Werte ermöglicht
        SharedPreferences.Editor edit = preferences.edit();

        //leeren der SharedPreferences bevor sie neu befüllt werden
        edit.clear();

        //wenn der Radio Button mqtt ausgewählt wurde
        if(rb_mqtt.isChecked()) {

            //wird für den eintrag mqtt der wert true hinterlegt
            edit.putBoolean(mqtt, true);

            //und für den Eintrag accelerometer der wert false
            edit.putBoolean(acce,false);

            //anschließend werden die änderungen in die Shared Preferences übertragen
            edit.apply();
        }
        //Ähnliches Vorgehen für den Fall das accelerometer ausgewählt ist
        else if(rb_acc.isChecked()){
            edit.putBoolean(acce,true);
            edit.putBoolean(mqtt,false);
            edit.apply();
        }
        else{ //Wurde keine der beiden Optionen ausgewählt so wird es so behandelt als wäre mqtt ausgewählt worden
            edit.putBoolean(mqtt, true);
            edit.putBoolean(acce,false);
            edit.apply();
        }
        //Abrufen der Eingabe des Users an der Stelle der IP-Adresse
        String ip_new = ip_addr.getText().toString();

        //Überprüfen ob ip_new leer ist, wenn ja bekommt es den Wert der Variablen ip_add
        if(ip_new == null) ip_new = ip_add;

        //Überschreiben der Variablen ip_add mit dem zusammengesetzten string aus den variablen ip_a, ip_new und ip_e
        ip_add = ip_a+ip_new+ip_e;

        //setzen des Strings für den Shared Preferences Eintrag ip_adresse
        edit.putString(ip_addresse,ip_add);

      //  if (sound_tg.isActivated()) {
       // }
        //Überschreiben des bisherigen Eintrags
        edit.apply();
        if(sw_sound.isChecked()){
            edit.putString(sound,"ON");
            edit.apply();
        }
        else{

            edit.putString(sound,"OFF");
            edit.apply();
        }

    }

    /**
     * In this function we define what should happen if the button is clicked.
     * @param v the view on which the button lays
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        //Abfragen welche id das Element auf das gecklickt wurde hat
        //Handelt es sich um den Button so
        if (v.getId() == R.id.button_best) {//wird die Funktion load_options aufgerufen
            load_options();

            //eines neues Objekt vom Typ intent erstellt welches von der aktuellen Klasse zur Klasse Menue zurück wechseln soll
            Intent intent = new Intent(this, Menue.class);

            //über startActivity wird das starten der Menue Activity dann veranlasst
            startActivity(intent);

            //Anschließend wird diese Activity beendet
            this.finish();
        }

    }
}