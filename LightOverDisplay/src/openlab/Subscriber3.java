package openlab;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;

public class Subscriber3 implements MqttCallback {
    private final int qos = 1;
    private String topic = "openlab/sensorkits/+/light";
    private MqttClient client;
    private int[] lightLevel = new int[6];
        public Subscriber3() throws MqttException, JSONException, InterruptedException {
        String host = String.format("tcp://%s:%d", "openlab.kpi.fei.tuke.sk", 1883);
        String clientId = "Trembulak591";
        //String[] MAC = {"+","B8:27:EB:B2:23:2A","B8:27:EB:F8:82:2C","B8:27:EB:DA:2E:65","B8:27:EB:DC:E8:38","B8:27:EB:78:8F:4D","B8:27:EB:2F:7B:7D"};
        //this.topic="openlab/sensorkits/+/light";

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);

        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(conOpt);
        this.client.subscribe(this.topic, qos);
    }

    public int[] getLightLevel(){
        return lightLevel;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost because: " + throwable);
        System.exit(1);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String data =  new String(mqttMessage.getPayload());
        String MAC = topic.split("/")[2];
        switch (MAC){
            case "B8:27:EB:B2:23:2A":
                lightLevel[0] = Integer.parseInt(data);
                //System.out.println(String.format("1.sensor(2x2 panel, behind wood wall): %s", data));
                break;
            case "B8:27:EB:F8:82:2C":
                lightLevel[1] = Integer.parseInt(data);
                //System.out.println(String.format("2.sensor(Aurora): %s", data));
                break;
            case "B8:27:EB:DA:2E:65":
                lightLevel[2] = Integer.parseInt(data);
                //System.out.println(String.format("3.sensor(windows,3x3 panel): %s", data));
                break;
            case "B8:27:EB:DC:E8:38":
                lightLevel[3] = Integer.parseInt(data);
                //System.out.println(String.format("4.sensor(stairs): %s", data));
                break;
            case "B8:27:EB:78:8F:4D":
                lightLevel[4] = Integer.parseInt(data);
                //System.out.println(String.format("5.sensor(4.& 5. TV panel): %s", data));
                break;
            case "B8:27:EB:2F:7B:7D":
                lightLevel[5] = Integer.parseInt(data);
                //System.out.println(String.format("6.sensor(B block door): %s", data));
                break;
            /*default:
                System.out.println("No sensor data");*/
        }
        //System.out.println(String.format("[%s] %s %s", this.topic, topic, data));
        //System.out.println(String.format("[%s] %s", topic.split("/")[2], data));
        //System.out.println(lightLevel[5]);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
