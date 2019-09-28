
// Disclaimer: Launcher icon provided by -> https://www.flaticon.com/authors/smashicons

package com.example.user.myapplicationrecopoi;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /* Client variables */
    public static int recommendations[];
    public static Data received;

    protected static int user, k;
    protected static String category;
    protected static double distance, latitude, longitude;

    /* Interface */
    EditText user_field, k_field, category_field, distance_field, latitude_field, longitude_field;
    private Button confirm, reset;
    private TextView prediction_view;

    String to_text_view;
    Context context; // used to grab application context and start mapsActivity
    private Map<Integer, Poi> poiList;
    protected static ArrayList<Poi> pois = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_field = (EditText) findViewById(R.id.user_value);
        k_field = (EditText) findViewById(R.id.k_value);
        category_field = (EditText) findViewById(R.id.category_value);
        distance_field = (EditText) findViewById(R.id.distance_value);
        latitude_field = (EditText) findViewById(R.id.latitude_value);
        longitude_field = (EditText) findViewById(R.id.longitude_value);

        confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkfields();

                System.out.println("Epeleksa ton xrhsth me id: " + user);
                System.out.println("Epeleksa " + k + " POIs");
                System.out.println("Epeleksa latitude: " + latitude);
                System.out.println("Epeleksa longtitude: " + longitude);
                System.out.println("Epeleksa catergory: " + category);
                System.out.println("Epeleksa distance: " + distance);
                System.out.println("-------------------");

                AsyncTaskThread att = new AsyncTaskThread();
                att.execute();

                prediction_view = (TextView) findViewById(R.id.prediction_view);
                prediction_view.setText("Waiting for results...");

            }
        });

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartMainActivity();
            }
        });
    }

    private class AsyncTaskThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            System.out.println("Tha stalei: ");
            System.out.println("user = "+user+", k = "+k+", latitude = "+latitude+", longitude = "+longitude+", category = "+category+ ", distance = "+distance);

            recommendations= new int[k];

            Socket clientSocket = null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;

            try {
                clientSocket = new Socket("192.168.1.4", 60000);

                System.out.println("Dhmiourgh8hke");

                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                System.out.println("Paei na steilei ta data - out.write(....)");
                /* Send the query */
                out.writeObject(new Data(user, k, latitude, longitude, category, distance));
                out.flush();

                received = (Data) in.readObject();
                recommendations = received.pred;

                to_text_view = "";
                for (int i = 0; i < recommendations.length; i++) {
                    if (i == recommendations.length - 1) {
                        to_text_view += recommendations[i] + "\n";
                    } else {
                        to_text_view += recommendations[i] + ", ";
                    }
                }

                System.out.println("PREDICTIONS: " + to_text_view);

            /* Inform HandleClient of termination */
                out.writeObject(new Data("exit"));
                out.flush();

            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");

            } catch (IOException ioException) {
                ioException.printStackTrace();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();

            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();

                } catch (IOException ioException) {
                    ioException.printStackTrace();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(".............");

            if(pois.isEmpty()) {
                for (int i = 0; i < recommendations.length; i++) {
                    pois.add(JSONParser().get(recommendations[i]));
                }
            }
            System.out.println("Emfanish POIs!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(pois);

            return to_text_view;
        }

        @Override
        protected void onPostExecute(String s) {
            prediction_view.setText("PREDICTIONS: " + to_text_view);

            // start the map activity
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    private void checkfields() {

        try {
            user = Integer.parseInt(user_field.getText().toString());
            k = Integer.parseInt(k_field.getText().toString());
            category = String.valueOf(category_field.getText().toString());
            latitude = Double.parseDouble(latitude_field.getText().toString());
            longitude = Double.parseDouble(longitude_field.getText().toString());
            distance = Double.parseDouble(distance_field.getText().toString());

        } catch (NumberFormatException e) {
            restartMainActivity();
        }
    }

    private void restartMainActivity() {
        pois.clear();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public Map<Integer, Poi> JSONParser() {

        String json;

        try {
            InputStream is = getAssets().open("POIs.json");

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            Gson gson = new Gson();
            json = new String(buffer, "UTF-8");
            Type type = new TypeToken<Map<Integer, Poi>>() {}.getType();
            poiList = gson.fromJson(json, type);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return poiList;
    }

}