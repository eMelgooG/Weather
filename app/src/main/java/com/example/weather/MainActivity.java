package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private static final String APPID = "2d8b7d6ba660a1e933cba71736cba88e";
    private static final String LINK = "http://api.openweathermap.org/data/2.5/weather?q=";
    Button displayWeatherButton;
    EditText cityNameEditText;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    TextView weatherResultTextView;
    DownloadJasonData retrieveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayWeatherButton = findViewById(R.id.buttonDisplayWeather);
        cityNameEditText = findViewById(R.id.cityET);
        weatherResultTextView = findViewById(R.id.weatherTV);


    }

    public void getLocation(View view) {
        retrieveData = new DownloadJasonData();
        try {
            String location = cityNameEditText.getText().toString();
            String link = LINK + location + "&appid=" + APPID;
            retrieveData.execute(link);


        } catch (Exception e) {
            e.printStackTrace();
        }

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityNameEditText.getWindowToken(), 0);
    }

    class DownloadJasonData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getWeatherInfo(String param) {
            String result = "";
            try {
                JSONArray arr = new JSONArray(param);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals(""))
                        result += main + ": " + description + "\r\n";
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }

        private String getMainInfo(String param) {
            String result = "";
            try {
                JSONObject jsonPart = new JSONObject(param);

                double temp = jsonPart.getDouble("temp");
                double tempMin = jsonPart.getDouble("temp_min");
                double tempMax = jsonPart.getDouble("temp_max");
                String humidity = jsonPart.getString("humidity");

                temp += -273.15; //Convert form Kelvin to Celsius
                tempMax += -273.15;
                tempMin += -273.15;

                result += "Temp: " + df2.format(temp) + "°C  MAX: " + df2.format(tempMax) + "°C  MIN: " + df2.format(tempMin) + "°C " + "\r\n" + "Humidity: " + humidity + "%" + "\r\n";

                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                try {
                    String message = "";
                    JSONObject jasonObject = new JSONObject(s);
                    String weatherInfo = jasonObject.getString("weather");
                    String main = jasonObject.getString("main");
                    message += getMainInfo(main);
                    message += getWeatherInfo(weatherInfo);


                    if (!message.equals("")) {
                        weatherResultTextView.setText(message);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
