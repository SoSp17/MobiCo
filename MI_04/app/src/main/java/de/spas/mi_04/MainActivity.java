package de.spas.mi_04;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Paint paint;
    private int[][] labyrinth;
    private final int labyrinthSize = 20;
    private LabyrinthView labyrinthView;
    private float cellSize;
    private float diameter;
    private MediaPlayer mediaPlayer;
    private float[] lastAccelerometerValues;
    private float playerX=1.5f ; // Startposition des Spielers (x-Koordinate)solle im ersten fel oben links liegen
    private float playerY =1.5f; // Startposition des Spielers (y-Koordinate)

    public MainActivity() {
    }

    final String prefs = "pref";
    public SharedPreferences Options;
        String soundOn="ON";
        String soundOff="OFF";
        boolean sound;
    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        labyrinthView = new LabyrinthView(this);
        setContentView(labyrinthView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Berechne die cellSize basierend auf der Bildschirmgröße
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;
        cellSize = Math.min(screenWidth, screenHeight) / labyrinthSize;
diameter=cellSize/3;
        mediaPlayer = MediaPlayer.create(this, R.raw.diggy_hole); // Musikdatei aus dem res/raw-Ordner laden
        Options  = this.getSharedPreferences(prefs, Context.MODE_PRIVATE);
        sound=Options.getBoolean(soundOn, false);
    }




    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if(sound) startMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopMusic();
    }
    private void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    /**In der Methode onSensorChanged, werden die events verarbeitet, die durch ändern des Lagesensors entstehen und Kollisionen abgefragt
     *Um eine kollision zu detektieren, wird auf die aktuelle Position der durchmesser draufgerechnet,
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] currentAccelerometerValues = event.values;

            if (lastAccelerometerValues != null) {
                float xDiff = currentAccelerometerValues[0] + lastAccelerometerValues[0];
                float yDiff = currentAccelerometerValues[1] + lastAccelerometerValues[1];


                collisionDetection(xDiff,yDiff);
                // Aktualisiere die Darstellung
                labyrinthView.invalidate();
            }
            lastAccelerometerValues = currentAccelerometerValues.clone();
        }
    }



    /**
     *
     * @param xDiff beinhaltet die Werte der Änderung in x Richtung
     * @param yDiff beinhaltet die Werte der Änderung in y Richtung
     *Die differzenzen werden auf die vorherige position addiert und auf collision geprüft,falls keine vorliegt wird die Position aktualisiert
     *
     */



    @SuppressLint("SuspiciousIndentation")
    private void collisionDetection(float xDiff, float yDiff) {
        float lastPosX=playerX;
        float lastPosY=playerY;
        float newPosX = lastPosX-(xDiff * 0.1f);
        float newPosY = lastPosY+(yDiff * 0.1f);
        int roundedPlayerX=(int)Math.round(newPosX);
        int roundedPlayerY=(int)Math.round(newPosY);
        int roundedPlayerXm=(int)Math.round(newPosX-diameter);
        int roundedPlayerYm=(int)Math.nextDown(newPosY-diameter);
        int roundedPlayerXp=(int)Math.nextUp(newPosX+diameter);
        int roundedPlayerYp=(int)Math.nextUp(newPosY+diameter);
//Abfrage bewegung nach rechts, wenn ja round+1 ==1, keine bewegung ,wenn round-1 ==1 keine bewegung nach links

    if(roundedPlayerX<=0 || roundedPlayerX>=labyrinthSize)newPosX=lastPosX;
//else if(labyrinth[roundedPlayerX-1][roundedPlayerY]==1){newPosX=lastPosX; }

    if(roundedPlayerY<=0 || roundedPlayerY>=labyrinthSize)newPosY=lastPosY;
  //  else if(labyrinth[roundedPlayerX][roundedPlayerY]==1){newPosX=lastPosX; newPosY=lastPosY;}


        updatePosition(newPosX, newPosY);

        if (roundedPlayerX == labyrinthSize && roundedPlayerY == labyrinthSize) {
            // Der Spieler hat das Ziel erreicht
            Toast.makeText(this, "Ziel erreicht!", Toast.LENGTH_SHORT).show();
            startGame();

            // Hier kannst du weitere Aktionen nach dem Erreichen des Ziels durchführen
            //Hier wird die bestenliste aufgerufen um anschließend eine neue runde zu beginnen
            //Show fragment
        }

    }

    /**
     * Setzt die Position des Spielers auf die Startposition und lädt das Labyrinth neu
     */
    private void startGame() {
         playerX=1.5f ; // Startposition des Spielers (x-Koordinate)solle im ersten fel oben links liegen
         playerY =1.5f;
        labyrinthView = new LabyrinthView(this);
        setContentView(labyrinthView);
    }


    /**Übergibt die neuen Koordinaten an den Ball
     *
     * @param x
     *
     */
    private void updatePosition(float x,float y) {
        playerX=x;
        playerY=y;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignoriere diese Methode
    }

    /**
     *
     */
    private class LabyrinthView extends View {

        private final Paint paint;
        private final Paint paintB;
        private final Paint paintA;
        public LabyrinthView(MainActivity context) {
            super(context);
            paint = new Paint();
             paintA = new Paint();
            paintB=new Paint();
            paint.setColor(Color.BLACK);
            paintA.setColor(Color.GREEN);
            paintB.setColor(Color.RED);
            paintA.setStrokeWidth(5);
            paintB.setStrokeWidth(5);
            paint.setStrokeWidth(5);
            labyrinth = generateLabyrinth(labyrinthSize, labyrinthSize);
        }

        /**
         *
         * @param width
         * @param height
         * @return
         */
        private int[][] generateLabyrinth(int width, int height) {
            int[][] labyrinth = new int[height][width];
            Random random = new Random();

            // Initialisiere das Labyrinth mit Wänden
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    labyrinth[i][j] = 1;
                }
            }

            // Erzeuge das Labyrinth mit dem Backtracking-Algorithmus
            int startX = 1; // Start-X-Koordinate
            int startY = 0; // Start-Y-Koordinate
            generateLabyrinthWithBacktracking(startX, startY, labyrinth, random);

            return labyrinth;
        }

        /**
         *
         * @param x
         * @param y
         * @param labyrinth
         * @param random
         */
        private void generateLabyrinthWithBacktracking(int x, int y, int[][] labyrinth, Random random) {
            labyrinth[y][x] = 0; // Setze Gang-Zelle

            // Erzeuge eine zufällige Reihenfolge der Richtungen (oben, rechts, unten, links)
            int[] directions = {1, 2, 3, 4};
            shuffleArray(directions, random);

            // Durchlaufe die Richtungen
            for (int direction : directions) {
                int nextX = x;
                int nextY = y;

                // Bestimme die nächste Position basierend auf der Richtung
                switch (direction) {
                    case 1: // Oben
                        nextY -= 2;
                        break;
                    case 2: // Rechts
                        nextX += 2;
                        break;
                    case 3: // Unten
                        nextY += 2;
                        break;
                    case 4: // Links
                        nextX -= 2;
                        break;
                }

                // Überprüfe, ob die nächste Position innerhalb des Labyrinths liegt
                if (nextX > 0 && nextX < labyrinth[0].length  && nextY > 0 && nextY < labyrinth.length - 1 && labyrinth[nextY][nextX] == 1) {
                    labyrinth[y + (nextY - y) / 2][x + (nextX - x) / 2] = 0; // Entferne Wand

                    generateLabyrinthWithBacktracking(nextX, nextY, labyrinth, random);
                }
            }
        }

        /**
         *
         * @param array
         * @param random
         */
        private void shuffleArray(int[] array, Random random) {
            for (int i = array.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                int temp = array[index];
                array[index] = array[i];
                array[i] = temp;
            }
        }
        /**
         *
         * @param canvas
         */
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
  if(sound) startMusic();
            int width = getWidth();
            int height = getHeight();
          //  int cellSize = Math.min(width, height) / labyrinth.length;

            for (int i = 0; i < labyrinth.length; i++) {
                for (int j = 0; j < labyrinth[i].length; j++) {
                    int x = (int) (j * cellSize);
                    int y = (int) (i * cellSize);

                    if (labyrinth[i][j] == 1) {
   canvas.drawRect(x,y,x+cellSize,y+cellSize,paint);
                    }

                }
            }

            //(width-cellsize ist die fläche innerhalb, mit einem Rand aus einem feld ausenrum)
            canvas.drawRect(labyrinthSize*cellSize-cellSize,labyrinthSize*cellSize-cellSize,height,width,paintB);
            // Zeichne den Spieler (Gegenstand)
            paint.setColor(Color.BLACK);
            //koordinaten x und y geben den mittelpunkt, der anfangs gesetzte wert 1.5 wird mit der Zellengröße multipliziert, dadurch liegt
            //die erste position in der mitte des feldes 1/1
            int playerPosX = Math.round(playerX * cellSize );
            int playerPosY = Math.round(playerY * cellSize );
            canvas.drawCircle(playerPosX, playerPosY, diameter, paint);

        }
    }
}