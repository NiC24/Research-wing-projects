
int led = 13;
int threshold = 30; 
int volume;
int inputPin=5;
int val=0;//State of Vibration sensor, low/high
int pinSpeaker = 10; 


void setup() 
{                
  Serial.begin(9600); // For debugging
  pinMode(led, OUTPUT);
   pinMode(pinSpeaker, OUTPUT);     
    pinMode(inputPin, INPUT); 
}




void loop() {
  
  volume = analogRead(A0); // Reads the value from the Analog PIN A0
  
    
    Serial.println(volume);
    delay(20);
 
  
  if(volume>=threshold)
{
    digitalWrite(led, HIGH); //Turn ON Led
  }  
  

else
{
    digitalWrite(led, LOW); // Turn OFF Led
  }

 {
 val = digitalRead(inputPin);
 if (val == HIGH) {            
    digitalWrite(led, HIGH); 
  delay(150);}}
}


void playTone(long duration, int freq) 
{
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

  


