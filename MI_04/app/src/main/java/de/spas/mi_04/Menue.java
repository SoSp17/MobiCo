package de.spas.mi_04;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * This class is the StartActivity of our Application. In it we enable the user to start the MainActivity or change the Options.
 * @see AppCompatActivity
 * @see View.OnClickListener
 */
public class Menue extends AppCompatActivity implements View.OnClickListener{

    Button startBtn;

    /**
     * This Method is called when the activity is used for the first time. It creates a toolbar which enables the user to go to the Options Side
     * or to end the app.
     * @param savedInstanceState this parameter is used to save information when the User ends the application
     * @see Toolbar
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Festlegen des verwendeten Layouts der Activity
        setContentView(R.layout.menue);

        //Start Button mit Button auf dem Layout verbinden und einen OnClickListener darauf ansetzen
        startBtn = (Button)findViewById(R.id.button_start);
        startBtn.setOnClickListener(this);

        //Verbinden der Toolbar und mit Funktionalität versehen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

    }

    /**
     * this method is used to define what happens if an element on the layout is clicked
     * @param v the layout on which the onCklickListener Listens
     * @see Intent
     */
    @Override
    public void onClick(View v) {
        //Erstellen eines neuen Intents, welche dieses und die MainActivity Klasse verbindet und einen Wechsel von dieser zur MainActivity ermöglicht
        Intent intent = new Intent(this, MainActivity.class);

        //starten der nächsten Activity
        startActivity(intent);

        //Beenden dieser Activity
        this.finish();
    }

    /**
     * This method is used to create the options Menu
     * @param menu the menu to use
     * @return true if the process was positive
     * @see Menu
     */
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    /**
     * This method is used to handle the users clicks in the menu and to get the right answer for this clicks
     * @param item the item the user clicked
     * @return if none of the defined cases is the right, the method returns an super.onOptionsItemSelected(item). This defined an own answer for the undefined case.
     * @see MenuItem
     * @see Intent
     */
    public boolean onOptionsItemSelected(MenuItem item){
        //Herausfinden welches Item geklickt wurde mittels der Item-ID
        switch(item.getItemId()){
            //handelt es sich um die ID der Optionen-Option
            case R.id.opts:
                //so wird ein neues Objekt vom Typ Intent erstellt welches von dieser zur Klasse Optionen wechseln soll
                Intent intent_opt = new Intent(this, Optionen.class);

                //Anschließend wird diese Activity gestartet
                startActivity(intent_opt);

            case R.id.end:
                //Für den Fall das der Angeklickte Menüpunkt die ID end hatte, wird die App beendet
                this.finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
