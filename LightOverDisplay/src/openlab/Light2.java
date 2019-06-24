package openlab;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

public class Light2 {
    private Subscriber3 s3;


    private Color[] rgbArray = new Color[3];
    public void light2(Subscriber3 s3){
        this.s3=s3;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                capture();
              }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }

    public void capture(){

        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =
                ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) {
            GraphicsDevice gd = gs[j];
            GraphicsConfiguration[] gc =
                    gd.getConfigurations();
            for (int i=0; i < gc.length; i++) {
                virtualBounds =
                        virtualBounds.union(gc[i].getBounds());
            }
        }

        //Rectangle screenRect = new Rectangle(0,0,Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height);
        Rectangle screenRect = new Rectangle(0,0,virtualBounds.width,virtualBounds.height);

        try {
            BufferedImage capture = new Robot().createScreenCapture(screenRect);
            int W = capture.getWidth();
            int H = capture.getHeight();

            int[] color = new int[36];

            int w3 = W/3;
            int w36 = w3/6;
            int w34 = w3/4;         //stoji za to ?
            int w343 = w34*3;       //stoji za to ?
            int h3 = H/3;
            int h34 = h3/4;
            int h343 = h34*3;
            /* color of pixels */
            for(int i=0; i<3; i++){
                /* top  */
                color[i * 12] = capture.getRGB((w3 * i) + w36,  h34);
                color[1 + i * 12] = capture.getRGB((w3 * i) + (w36) * 3, h34);
                color[2 + i * 12] = capture.getRGB((w3 * i) + (w36) * 5, h34);
                color[3 + i * 12] = capture.getRGB((w3 * i) + (w36), h34);
                color[4 + i * 12] = capture.getRGB((w3 * i) + (w36) * 3, h343);
                color[5 + i * 12] = capture.getRGB((w3 * i) + (w36) * 5, h343);
                /* mid  */
                color[6 + i * 12] = capture.getRGB((w3 * i) + w34, h3 + h34);
                color[7 + i * 12] = capture.getRGB((w3 * i) + w343, h3 + h34);
                color[8 + i * 12] = capture.getRGB((w3 * i) + w34, h3 + h343);
                color[9 + i * 12] = capture.getRGB((w3 * i) + w343, h3 + h343);
                /* bot  */
                color[10 + i * 12] = capture.getRGB((w3 * i) + w34, h3 * 2 + h3 / 2);
                color[11 + i * 12] = capture.getRGB((w3 * i) + w343, h3 * 2 + h3 / 2);

            }

            int red;
            int green;
            int blue;
            for(int l=0;l<3;l++) {
                red = green = blue = 0;
                for (int k = 0; k < 12; k++) {
                    Color colorRGB = new Color(color[k+l*12]);
                    red += colorRGB.getRed();
                    green += colorRGB.getGreen();
                    blue += colorRGB.getBlue();
                }
                rgbArray[l] = new Color(red / 12, green / 12, blue / 12, 100);
                //System.out.println("Collum: "+l+" red "+rgbArray[l].getRed()+" green "+rgbArray[l].getGreen()+" blue "+rgbArray[l].getBlue());
            }
        setLight();
        } catch (AWTException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLight() throws JSONException {
        int[] lightLevel = s3.getLightLevel();
        System.out.println(lightLevel[1]);
        JSONArray jsonArray = new JSONArray();
        //System.out.print("r"+Integer.toHexString(rgbArray[0].getRed())+"g"+Integer.toHexString(rgbArray[0].getGreen())+"b"+Integer.toHexString(rgbArray[0].getBlue()));
        String color = Integer.toHexString(rgbArray[0].getRed())+Integer.toHexString(rgbArray[0].getGreen())+Integer.toHexString(rgbArray[0].getBlue())+"00";
        for (int i = 0; i < 27; i++) {
            jsonArray.put(color);
        }
        color = Integer.toHexString(rgbArray[1].getRed())+Integer.toHexString(rgbArray[1].getGreen())+Integer.toHexString(rgbArray[1].getBlue())+"00";
        for (int i = 0; i < 27; i++) {
            jsonArray.put(color);
        }
        color = Integer.toHexString(rgbArray[2].getRed())+Integer.toHexString(rgbArray[2].getGreen())+Integer.toHexString(rgbArray[2].getBlue())+"00";
        for (int i = 0; i < 27; i++) {
            jsonArray.put(color);
        }
        for (int i = 0; i < 16; i++) {
            jsonArray.put("0055aa33");
        }

        JSONObject json = new JSONObject();
        json.put("colors",jsonArray);
        json.put("duration", 500);

        String JsonDATA = json.toString();
        //JsonDATA = "{\"colors\":"+jsonArray.toString()+",\"duration\":500}";
        HttpURLConnection connection = null;
        System.out.println(JsonDATA);
        try {
            //URL url = new URL(params[0]);
            URL url = new URL("https://openlab.kpi.fei.tuke.sk/rest/light");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.connect();

            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            // json data
            writer.close();


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }

    }

}
