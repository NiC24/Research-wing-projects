#include <GSM.h>
#include <GSM3CircularBuffer.h>
#include <GSM3IO.h>
#include <GSM3MobileAccessProvider.h>
#include <GSM3MobileCellManagement.h>
#include <GSM3MobileClientProvider.h>
#include <GSM3MobileClientService.h>
#include <GSM3MobileDataNetworkProvider.h>
#include <GSM3MobileMockupProvider.h>
#include <GSM3MobileNetworkProvider.h>
#include <GSM3MobileNetworkRegistry.h>
#include <GSM3MobileServerProvider.h>
#include <GSM3MobileServerService.h>
#include <GSM3MobileSMSProvider.h>
#include <GSM3MobileVoiceProvider.h>
#include <GSM3ShieldV1.h>
#include <GSM3ShieldV1AccessProvider.h>
#include <GSM3ShieldV1BandManagement.h>
#include <GSM3ShieldV1BaseProvider.h>
#include <GSM3ShieldV1CellManagement.h>
#include <GSM3ShieldV1ClientProvider.h>
#include <GSM3ShieldV1DataNetworkProvider.h>
#include <GSM3ShieldV1DirectModemProvider.h>
#include <GSM3ShieldV1ModemCore.h>
#include <GSM3ShieldV1ModemVerification.h>
#include <GSM3ShieldV1MultiClientProvider.h>
#include <GSM3ShieldV1MultiServerProvider.h>
#include <GSM3ShieldV1PinManagement.h>
#include <GSM3ShieldV1ScanNetworks.h>
#include <GSM3ShieldV1ServerProvider.h>
#include <GSM3ShieldV1SMSProvider.h>
#include <GSM3ShieldV1VoiceProvider.h>
#include <GSM3SMSService.h>
#include <GSM3SoftSerial.h>
#include <GSM3VoiceCallService.h>

#include <dht.h>

dht DHT;

#define DHT11_PIN 7

int airquality = 0;
int pinSpeaker = 10;
int ledPin = 13;
const int gasPin = A1;   



void setup()
{
 pinMode(ledPin, OUTPUT);   
 pinMode(pinSpeaker, OUTPUT);
  Serial.begin(9600);

}

void loop()
{
  int sensorValue = analogRead(A0);
  int MQ2=analogRead(gasPin);
  if(sensorValue > 199 || MQ2>500)
  {
   digitalWrite(ledPin, HIGH);  // turn LED ON
    playTone(300, 160);
    Serial.println("Hazardous gases detected");
    SendTextMessage();
    delay(150);
    }
  else
  {
   digitalWrite(ledPin, LOW); // turn LED OFF
   playTone(0, 0);   
   delay(300);
    } 
    
// Serial.println("  ");
// Serial.println("Air Quality Index  ");
  Serial.println(sensorValue);
  

//  Serial.print("*PPM");
//  Serial.println();
  delay(1000);

 int chk = DHT.read11(DHT11_PIN);
  delay(1000);
//
//  Serial.println("Temp in Deg C");
  Serial.println(DHT.temperature);
//  Serial.println(" ");
//  Serial.println("Humidity % " );
  Serial.println(DHT.humidity);

if(DHT.temperature > 39)
   {
   digitalWrite(ledPin, HIGH);  // turn LED ON
    playTone(300, 160);
    SendText();
    delay(150);
    }
  
  else
   {
   digitalWrite(ledPin, LOW); // turn LED OFF
    playTone(0, 0);  
    delay(300);
    } 
 
 }


 void SendTextMessage()
{
  int sms_count=0;
  for(sms_count=0;sms_count<=2;sms_count++)
  
  Serial.println("AT+CMGF=1");    //To send SMS in Text Mode
  delay(2000);
  Serial.println("AT+CMGS=\"+919223557250\"\r"); // assign a  phone number  
  delay(3000);
  Serial.println("The Air Quality Index is Not safe for work, please take mandatory action!");//the content of the message
  delay(200);
  Serial.println((char)26);//the stopping character
  delay(5000);
  sms_count++;
}


  void SendText()
{
  int sms_count=0;
  for(sms_count=0;sms_count<=2;sms_count++)
  
  Serial.println("AT+CMGF=1");    //To send SMS in Text Mode
  delay(2000);
  Serial.println("AT+CMGS=\"+919223557250\"\r"); // change to the phone number you using 
  delay(3000);
  Serial.println("The Indoor temp is Not safe for work, please take mandatory action!");//the content of the message
  delay(200);
  Serial.println((char)26);//the stopping character
  delay(5000);
  sms_count++;
}
 


// duration in mSecs, frequency in hertz
void playTone(long duration, int freq) {
    duration *= 1000;
    int period = (1.0 / freq) * 1000000;
    long elapsed_time = 0;
    while (elapsed_time < duration) {
        digitalWrite(pinSpeaker,HIGH);
        delayMicroseconds(period / 2);
        digitalWrite(pinSpeaker, LOW);
        delayMicroseconds(period / 2);
        elapsed_time += (period);
    }
}
