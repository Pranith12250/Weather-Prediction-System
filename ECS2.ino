#include <dht.h>
#define dataPin 8
dht DHT;

const int rainPin = A0;

#include <Wire.h>
#include <Adafruit_BMP085.h>
#define seaLevelPressure_hPa 1013.25
Adafruit_BMP085 bmp;

unsigned long currentTime=0;
float temp[4];
float humid[4];
float pres[4];
bool rain=false;
int counter=0;

void setup() 
{
  pinMode(rainPin, INPUT_PULLUP);
  Serial.begin(9600);
  currentTime=millis();
}
void loop() 
{
  if (millis() - currentTime >= 6*1000)//6 * 60 * 60 * 1000) 
  {
    int readData = DHT.read22(dataPin);
    temp[counter]=DHT.temperature;
    humid[counter]=DHT.humidity;

    if (!bmp.begin()) 
      while (1) {Serial.print("error bro");}
    pres[counter]=bmp.readPressure();
    counter++;
    currentTime=millis();
  }
  int sensorValue = analogRead(rainPin);
  if(sensorValue < 500)
    rain=true;

  if(counter==4)
  {
    Serial.print(findMinTemp());
    Serial.print(",");

    Serial.print(findMaxTemp());
    Serial.print(",");

    Serial.print(humid[0]);
    Serial.print(",");
    Serial.print(humid[1]);
    Serial.print(",");

    Serial.print(pres[0]);
    Serial.print(",");
    Serial.print(pres[1]);
    Serial.print(",");

    Serial.print(temp[0]);
    Serial.print(",");
    Serial.print(temp[1]);
    Serial.print(",");

    Serial.print(rain==true?"Yes":"No");
    Serial.print(",");
    Serial.println();

    counter=0;
    rain=false;
  }
}
float findMaxTemp()
{
  float max=0;
  for(int x=0;x<4;x++)
    if(temp[x]>max)
      max=temp[x];
  return max;
}
float findMinTemp()
{
  float min=temp[0];
  for(int x=0;x<4;x++)
    if(temp[x]<min)
      min=temp[x];
  return min;
}