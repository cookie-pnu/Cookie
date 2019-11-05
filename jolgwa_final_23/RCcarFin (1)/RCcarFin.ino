#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출
#include <DFPlayer_Mini_Mp3.h>
#include <Servo.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>

LiquidCrystal_I2C lcd(0x27,16,2);
int distance;
void controlPet(char value);
void SmartCar_Go();
void SmartCar_Back();
void SmartCar_Stop();
void SmartCar_Left();
void SmartCar_Right();
void SmartCar_sLeft();
void SmartCar_sRight();
void SmartCar_GoBack();
void SmartCar_Sonic();
void RandomBark();
void feedpet(char value);
void hungrypet(unsigned long timer);
void movingpet(unsigned long timer, char value);
void boringpet(unsigned long timer, char value);
//void ultrasonic(int distance);

extern volatile unsigned long timer0_millis;
unsigned long timer=0;
unsigned long pretimer=0;
int hungry = 0;
int hungry_val = 0;
int boring_val = 0;


//motor PIN 세팅
int RightMotor_E_pin = 4;      // 오른쪽 모터의 Enable & PWM
int RightMotor_1_pin = 22;      // 오른쪽 모터 제어선 IN1
int RightMotor_2_pin = 23;     // 오른쪽 모터 제어선 IN2
int LeftMotor_3_pin = 24;      // 왼쪽 모터 제어선 IN3
int LeftMotor_4_pin = 25;      // 왼쪽 모터 제어선 IN4
int LeftMotor_E_pin = 5;      // 왼쪽 모터의 Enable & PWM

//초음파센서 PIN 세팅
int triggerPin = 2;
int echoPin = 3;


//블루투스 PIN 세팅

//#define BT_TXD 19 //블루투스 TX
//#define BT_RXD 18 //블루투스 RX

//SoftwareSerial bluetooth(BT_RXD, BT_TXD); //시리얼 통신 선언
char value; //블루투스에서 받는 값 이름 
char prev_command;
int E_carSpeed = 130; // 자동차 속도

Servo LEar;
int pos = 0;

Servo REar;
int pos2 = 0;

Servo Tail;
int pos3 = 0;

Servo Hand;
int pos4 = 0;

const int duration = 30000;

#define interval 10000

void setup() {
  Serial.begin(9600); //시리얼 모니터
  Serial1.begin(9600); //블루투스 시리얼 개방
  mp3_set_serial(Serial);
  delay(1);
  mp3_set_volume(50);
  lcd.init();
  lcd.backlight();
  
  pinMode(13, OUTPUT);
  pinMode(RightMotor_E_pin, OUTPUT);        // 출력모드로 설정
  pinMode(RightMotor_1_pin, OUTPUT);
  pinMode(RightMotor_2_pin, OUTPUT);
  pinMode(LeftMotor_3_pin, OUTPUT);
  pinMode(LeftMotor_4_pin, OUTPUT);
  pinMode(LeftMotor_E_pin, OUTPUT);
  pinMode(6,OUTPUT);

  pinMode(triggerPin, OUTPUT);
  pinMode(echoPin, INPUT);
  //attachInterrupt(digitalPinToInterrupt(echoPin), distance, RISING);

  LEar.attach(10);
  REar.attach(11);
  Tail.attach(12);
  Hand.attach(13);

  Serial.println("Arduino Pet is ready to start");
}

void loop() {
  Serial.print("Time : ");
  Serial.println(timer);
  timer = millis();
  delay(1000);
  
  byte value = 'a';
  if(Serial1.available()) {
    value = Serial1.read();
    Serial.write(value);
    boring_val = 0;
    Serial1.print("received");
  }
  Serial.print("value : ");
  Serial.println(value);
 /*
  if (Serial.available()) {          // 시리얼 모니터로부터 받은 데이터가 있으면
    bluetooth.write(Serial.read());  // 블루투스로 전송
  }*/
  /*movingpet(hungry);*/
  controlPet(value);
  /*hungrypet(timer);*/
  feedpet(value);
  movingpet(timer, value);
  boringpet(timer, value);
  hungrypet(timer);
  if ((timer % 81000 >= 0) && (timer % 81000 <= 1000)) {
    hungry = 1;
    hungry_val = 0;
  }
  if ((timer % 11000 >= 0) && (timer % 11000 <= 1000)) {
    boring_val += 1;
    Serial.println("boring_val++");
  }
}

/*void ultrasonic(int distance) {
  Serial.println("Interrupt");
  if(distance >= 30) {
    SmartCar_Stop();
  }
}*/

/*void movingpet(unsigned long hungry) //진행중
{
  hungry = millis();
  if(hungry % 30000 ==0)
  {
    for(pos3 = 0; pos3 <= 170; pos3 +=1) {
      Tail.write(pos3);
      delay(3);
    }
     for(pos3 = 170; pos3>=0; pos3 -=1) {
      Tail.write(pos3);
      delay(3);
     } 
  }
}*/

void hungrypet(unsigned long timer)
{
   /*Serial.println(hungry);
   prehungry=hungry;
   delay(1000);*/
   if((hungry == 1) && (hungry_val<=1)){
      Serial.println("Hungry");
      SmartCar_Turn();
      delay(2000);
      SmartCar_Stop();
      lcd.print("I'm hungry T.T");
      delay(200);
      lcd.init();
      mp3_play(4);
      delay(4000); 
      hungry_val += 1;
   }
}

void movingpet(unsigned long timer, char value) {
  /*Serial.println(moving);
  premoving = moving;
  delay(1000);*/
  if ((value == 'a') && (timer % 31000 >= 0) && (timer % 31000 <= 1000)) {
    for(pos = 0; pos <= 90; pos +=1) {
        pos2 += 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 0; pos2 <= 90; pos2 +=1) {
        REar.write(pos2);
        delay(5);
     }*/
     for(pos = 90; pos>=0; pos -=1) {
        pos2 -= 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 90; pos2>=0; pos2 -=1) {
        REar.write(pos2);
        delay(5);
     }*/
    Serial.println("Ear");
    lcd.print("Let's go outside!");
    delay(200);
    lcd.init();
  }
  if ((value == 'a') && (timer % 51000 >= 0) && (timer % 51000 <= 1000)) {
    for(pos3 = 0; pos3 <= 170; pos3 +=1) {
        Tail.write(pos3);
        delay(3);
    }
    for(pos3 = 170; pos3>=0; pos3 -=1) {
        Tail.write(pos3);
        delay(3);
    }
    Serial.println("Tail");
    lcd.print("I love you");
    delay(200);
    lcd.init();
  }
  if ((value == 'a') && (timer % 71000 >= 0) && (timer % 71000 <= 1000)) {
    SmartCar_GoBack();
    Serial.println("Moving");
    lcd.print("What are you do");
    lcd.setCursor(0, 1);
    lcd.print("ing?");
    delay(200);
    lcd.init();
  }
}

void boringpet(unsigned long timer, char value) {
  if (boring_val % 6 == 5) {
    for(pos4 = 30; pos4 <= 120; pos4 +=1) {
        Hand.write(pos4);
        delay(10);
     }
     for(pos4 = 120; pos4>=30; pos4 -=1) {
        Hand.write(pos4);
        delay(10);
     }
    Serial.println("Boring");
    lcd.print("I'm Boring T.T");
    delay(1000);
    lcd.init();
  }
}

void feedpet(char value)
{
  if(value == 'j')
  { //밥주기
      /*prehungry=hungry;
      timer0_millis=0;
      prehungry=0;*/
      Serial.println("full");
      lcd.print("Yum Yum");
      delay(200);
      mp3_play(6);
      lcd.init();
      delay(4000); 
      hungry = 0; 
  }
}

void controlPet(char value)
{
  if(value == '1'){               // 전진 명령
      SmartCar_Go();
  }
  else if(value == 'k'){//쿠키~ 하고 부르면 멍멍하고 짖음 
      for(pos = 0; pos <= 90; pos +=1) {
        pos2 += 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 0; pos2 <= 90; pos2 +=1) {
        REar.write(pos2);
        delay(5);
     }*/
     for(pos = 90; pos>=0; pos -=1) {
        pos2 -= 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 90; pos2>=0; pos2 -=1) {
        REar.write(pos2);
        delay(5);
     }*/
      RandomBark();
      delay(4000); 
  }
  else if(value == 'b'){          // 후진 명령
      SmartCar_Back();  
  }
  else if(value == 's'){          // 정지 명령
      SmartCar_Stop(); 
  }
  else if(value == 'l'){          // 좌회전 명령
      SmartCar_Left();
  } 
  else if(value == 'r'){          // 우회전 명령
      SmartCar_Right(); 
  }
  else if(value == 'q'){          // 약좌회전 명령(실제사용x)
      SmartCar_sLeft(); 
  }
  else if(value == 'w'){          // 약우회전 명령(실제사용x)
      SmartCar_sRight();  
  }
  else if(value == '9'){         //귀 쫑긋 
    for(pos = 0; pos <= 90; pos +=1) {
        LEar.write(pos);  
    }
    for(pos2 = 0; pos2 <= 90; pos2 +=1) {
        REar.write(pos2);
        delay(5);
    }
    for(pos = 90; pos>=0; pos -=1) {
        LEar.write(pos);
    }
    for(pos2 = 90; pos2>=0; pos2 -=1) {
        REar.write(pos2);
        delay(5);
    }
    lcd.print("EAR");
    delay(200);
    lcd.init();
  }
  else if(value == '6'){         //손 
     for(pos4 = 30; pos4 <= 120; pos4 +=1) {
        Hand.write(pos4);
        delay(10);
     }
     for(pos4 = 120; pos4>=30; pos4 -=1) {
        Hand.write(pos4);
        delay(10);
     }
     lcd.print("Hand");
     delay(200);
     lcd.init();   
  }
  else if(value == '7'){         //산책 (귀,꼬리,음성)
     mp3_play(6);
     delay(50); 
     for(pos = 0; pos <= 90; pos +=1) {
        pos2 += 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 0; pos2 <= 90; pos2 +=1) {
        REar.write(pos2);
        delay(5);
     }*/
     for(pos = 90; pos>=0; pos -=1) {
        pos2 -= 1;
        LEar.write(pos);
        REar.write(pos2);
        delay(5);
     }
     /*for(pos2 = 90; pos2>=0; pos2 -=1) {
        REar.write(pos2);
        delay(5);
     }*/
    for(pos3 = 0; pos3 <= 170; pos3 +=1) {
        Tail.write(pos3);
        delay(3);
    }
    for(pos3 = 170; pos3>=0; pos3 -=1) {
        Tail.write(pos3);
        delay(3);
    }
    lcd.print("WOOOOOW!");
    delay(200);
    lcd.init();
  }
    
 /*else if(value == '5'){           //LED 제어 (핀 부족으로 주석처리)
      digitalWrite (13, HIGH); // set the LED on
      delay (2000); // wait for a second
      digitalWrite (13, LOW); // set the LED off
      delay (2000); // wait for a second
  }*/  
  
  else if(value == '4') {           //돌아
      SmartCar_Turn();
  }
}

//동작 명령 함수


//자동차 움직임 함수들 

void SmartCar_Go(){        // 전진
    Serial.println("Forward");
    
    digitalWrite(RightMotor_1_pin, HIGH);    
    digitalWrite(RightMotor_2_pin, LOW);
    digitalWrite(LeftMotor_3_pin, HIGH);    
    digitalWrite(LeftMotor_4_pin, LOW);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     // 오른쪽 모터 전진 PWM제어
      analogWrite(LeftMotor_E_pin, i);
      //delay(20); 
      digitalWrite(triggerPin, HIGH);
      delayMicroseconds(10);
      digitalWrite(triggerPin,LOW);

      distance = pulseIn(echoPin, HIGH) / 58;

      Serial.println("Distance(cm) = " + String(distance));

      delay(20);

      if (distance <= 30) {
        SmartCar_Stop();
        break;
      }
    }    
    lcd.print("Go Go");
    delay(200);
    lcd.init();
    prev_command = 'f';
}

void SmartCar_Turn() { // 돌아
    Serial.println("turn");
    digitalWrite(RightMotor_1_pin, LOW);    
    digitalWrite(RightMotor_2_pin, HIGH);
    digitalWrite(LeftMotor_3_pin, HIGH);    
    digitalWrite(LeftMotor_4_pin, HIGH);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }
    lcd.print("Turning...");
    delay(200);
    lcd.init();
}

void SmartCar_Back() {        // 후진
    Serial.println("Backward");
    digitalWrite(RightMotor_1_pin, LOW);    
    digitalWrite(RightMotor_2_pin, HIGH);
    digitalWrite(LeftMotor_3_pin, LOW);    
    digitalWrite(LeftMotor_4_pin, HIGH);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }    
    lcd.print("Back");
    delay(200);
    lcd.init();
    prev_command = 'b';
}


void SmartCar_Left(){        // 좌회전
    Serial.println("Left");
    digitalWrite(RightMotor_1_pin, LOW);    
    digitalWrite(RightMotor_2_pin, HIGH);
    digitalWrite(LeftMotor_3_pin, HIGH);    
    digitalWrite(LeftMotor_4_pin, LOW);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }    
    lcd.print("Left!");
    delay(200);
    lcd.init();
    prev_command = 'l';
}

void SmartCar_Right(){        // 우회전
    Serial.println("Right");
    digitalWrite(RightMotor_1_pin, HIGH);    
    digitalWrite(RightMotor_2_pin, LOW);
    digitalWrite(LeftMotor_3_pin, LOW);    
    digitalWrite(LeftMotor_4_pin, HIGH);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }    
    lcd.print("Right!");
    delay(200);
    lcd.init();
    prev_command = 'r';
}

void SmartCar_sLeft(){        // 약좌회전 (넣을 필요가 없어서 주석처리)
    Serial.println("sLeft");
    digitalWrite(RightMotor_1_pin, LOW);    
    digitalWrite(RightMotor_2_pin, LOW);
    digitalWrite(LeftMotor_3_pin, HIGH);    
    digitalWrite(LeftMotor_4_pin, LOW);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);    
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }    
    prev_command = 'q';
}

void SmartCar_sRight(){        // 약우회전
    Serial.println("sRight");
    digitalWrite(RightMotor_1_pin, HIGH);    
    digitalWrite(RightMotor_2_pin, LOW);
    digitalWrite(LeftMotor_3_pin, LOW);    
    digitalWrite(LeftMotor_4_pin, LOW);

    for(int i=0; i<=E_carSpeed; i=i+5){
      analogWrite(RightMotor_E_pin, i);     
      analogWrite(LeftMotor_E_pin, i);
      delay(20); 
    }    
    prev_command = 'w';
}

void SmartCar_Stop(){       // 정지
    for(int i=E_carSpeed; i>=0; i=i-5){
      if(prev_command == 'f'){
        analogWrite(RightMotor_E_pin, i);  
        analogWrite(LeftMotor_E_pin, i);
        delay(20); 
      }
      else if(prev_command == 'b'){
        analogWrite(RightMotor_E_pin, i);  
        analogWrite(LeftMotor_E_pin, i);
        delay(20); 
      }
    } 
      
    digitalWrite(RightMotor_E_pin, LOW);  // 오른쪽 모터 정지
    digitalWrite(LeftMotor_E_pin, LOW);   // 왼쪽 모터 정지
    lcd.print("STOP!");
    delay(200);
    lcd.init();
}

void SmartCar_GoBack() {
  SmartCar_Go();
  delay(500);
  SmartCar_Stop();
  delay(500);
  SmartCar_Back();
  delay(500);
  SmartCar_Stop();
}
 
void RandomBark(){ //랜덤으로 짖는 함수
  int a;
 
  lcd.print("YES!!");
  delay(200);
  lcd.init();
  
  a = random(2);

  if(a==0) {
    mp3_play(6);
  }
  else if(a==1) {
    mp3_play(1);
  }
  else if(a==2) {
    mp3_play(2);
  }
}
    
  

 
