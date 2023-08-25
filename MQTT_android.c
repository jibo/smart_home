#include <ESP8266WiFi.h>
#include <PubSubClient.h>
WiFiClient espClient;
PubSubClient client(espClient);
const char* wifissid = ""; //wifi
const char* password = ""; //wifi密码
const char* mqtt_server = "broker.emqx.io";
const char* mqtt_id = "565402462_zb_ESP";   //id
const char* Mqtt_sub_topic = "565402462_zb_ESP";   //订阅主题
const char* Mqtt_pub_topic = "565402462_zb";  //  上报消息给  手机APP的TOPIC  //发送主题
long lastMsg = 0; //定时用的
void setup() {
  pinMode(2, OUTPUT);     
  Serial.begin(9600);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}
void setup_wifi() {
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(wifissid);
  WiFi.begin(wifissid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}
void callback(char* topic, byte* payload, unsigned int length) {
  String msg="";
  String LED_set = "";
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    msg+= (char)payload[i];
  }
  Serial.println(msg);
  if(msg.indexOf("led"))  //判断是否是要设置LED灯
  {
    //取出LED_set数据 并执行
    LED_set = msg.substring(msg.indexOf("led\":")+5,msg.indexOf("}")); 
    digitalWrite(2,!LED_set.toInt()); 
  }
}
void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect(mqtt_id)) {
      Serial.println("connected");
      //连接成功以后就开始订阅
      client.subscribe(Mqtt_sub_topic,1);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}
void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    String json = "{\"temperature\":"+String(analogRead(A0))+"}";
    client.publish(Mqtt_pub_topic,json.c_str());
  }
}
