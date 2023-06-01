
#include <Adafruit_Sensor.h>
#include <Adafruit_MPU6050.h>
#include <Wire.h>
//#include "defines.h"
#include <WiFi.h>


extern "C"
{
#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"
}
#include <AsyncMQTT_ESP32.h>

//#define MQTT_HOST         IPAddress(192, 168, 2, 110)
#define MQTT_HOST         "broker.hivemq.com"        // Broker address
#define MQTT_PORT         1883
#define WIFI_SSID "OTH-AW"


//const char *PubTopic  = "async-mqtt/ESP32_Pub";               // Topic to publish
Adafruit_MPU6050 mpu;
AsyncMqttClient mqttClient;
TimerHandle_t mqttReconnectTimer;
TimerHandle_t wifiReconnectTimer;
//topics unter denen werte veröffentlicht werden
const char topic2[]="YAchse";
const char  topic[]="XAchse";
void connectToWifi()
{
  Serial.println("Connecting to Wi-Fi...");
  WiFi.begin(WIFI_SSID);
}




void onMqttConnect(bool sessionPresent)
{
  Serial.print("Connected to MQTT broker: ");
  Serial.print(MQTT_HOST);

}

void onMqttMessage(char* topic, char* payload, const AsyncMqttClientMessageProperties& properties,
                   const size_t& len, const size_t& index, const size_t& total)
{
  (void) payload;

}

void onMqttPublish(const uint16_t& packetId)
{
  Serial.println("Publish acknowledged.");
}
//integer um die werte des accelerometer aufzunehmen
int xAchse=0;
int yAchse=0;


void setup() {
 Serial.begin(115200);

 // while (!Serial && millis() < 5000);

   delay(10);

  Serial.print("\nStarting FullyFeature_ESP32 on ");
  //Verbindung mit wifi
    WiFi.begin(WIFI_SSID);
 // Serial.println(ARDUINO_BOARD);
//  Serial.println(ASYNC_MQTT_ESP32_VERSION);

 /* mqttReconnectTimer = xTimerCreate("mqttTimer", pdMS_TO_TICKS(2000), pdFALSE, (void*)0,
                                    reinterpret_cast<TimerCallbackFunction_t>(connectToMqtt));
  wifiReconnectTimer = xTimerCreate("wifiTimer", pdMS_TO_TICKS(2000), pdFALSE, (void*)0,
                                    reinterpret_cast<TimerCallbackFunction_t>(connectToWifi));
*/
  //WiFi.onEvent(WiFiEvent);
  //Server einstellungen werden gesetzt und dann mqtt verbunden
   mqttClient.setServer(MQTT_HOST, MQTT_PORT);
 mqttClient.connect();
  mqttClient.onConnect(onMqttConnect);
  mqttClient.onMessage(onMqttMessage);
  mqttClient.onPublish(onMqttPublish);
//Accellerometer wird verbunden und erfolgreiche verbindung ausgegeben
    if (!mpu.begin()) {
    Serial.println("Failed to find MPU6050 chip");
    while (1) {
      delay(10);
    }
  }
  Serial.println("MPU6050 Found!");
   mpu.setAccelerometerRange(MPU6050_RANGE_8_G);
     mpu.setFilterBandwidth(MPU6050_BAND_21_HZ);
}

void loop() {
//Variablen, die die Sensorbaten aufnehmen
  sensors_event_t a, g, temp;
  mpu.getEvent(&a, &g, &temp);
//Einlesen der Daten und paren in string
xAchse=a.acceleration.x;
yAchse=a.acceleration.y;
char XString[8];
char YString[8];
dtostrf(xAchse,1,2,XString);
dtostrf(yAchse,1,2,YString);
//Werte ausgeben
  Serial.println("Acceleration X: ");
  Serial.print(a.acceleration.x);
  Serial.print(", Y: ");
  Serial.print(a.acceleration.y);

  //Publishen der werte über mqtt
    mqttClient.publish(topic,0,true,XString);
    mqttClient.publish(topic2,0,true,YString);
  mqttClient.onPublish(onMqttPublish);
  delay(10); // this speeds up the simulation
}
