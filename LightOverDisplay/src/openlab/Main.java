package openlab;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;

import javax.swing.*;
import java.net.URISyntaxException;

public class Main{

    public static void main(String [ ] args) throws MqttException, URISyntaxException, JSONException, InterruptedException {
        Light light = new Light();
        //light.light();
        Subscriber3 s3 = new Subscriber3();
        Light2 light2 = new Light2();
        light2.light2(s3);
        //Subscriber s = new Subscriber();
        //Subscriber2 s2 = new Subscriber2();

    }

}
