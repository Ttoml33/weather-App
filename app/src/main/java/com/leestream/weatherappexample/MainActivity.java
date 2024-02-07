package com.leestream.weatherappexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView txtResults;
    private EditText etCity,etCountry;
    private String url="http://api.openweathermap.org/data/2.5/weather";
    private final String appID="#get yor api key from open weather Api";
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
     private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResults=findViewById(R.id.txtResults);
        etCity=findViewById(R.id.etCity);
        etCountry=findViewById(R.id.etCountry);
        
        findViewById(R.id.btnFind).setOnClickListener(v -> getWeather());
    }

    private void getWeather() {
        String tempUrl="";
        String city=etCity.getText().toString().trim();
        String country=etCountry.getText().toString().trim();

        if (city.equals("")){
            txtResults.setText("City field Cannot be empty");
        }else {
            if (country.equals("")){
                tempUrl = url + "?q=" + city + "," + country + " &appid=" + appID;
            }else {
                tempUrl = url + "?q=" + city + " &appid=" + appID;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG + " onResponse: ", response);

                    String outPut= " ";
                    try {
                        JSONObject jsonObjectResponse = new JSONObject(response);

                        JSONArray jsonArray = jsonObjectResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");

                        JSONObject jsonObjectMain = jsonObjectResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");

                        JSONObject jsonObjectWind = jsonObjectResponse.getJSONObject("wind");
                        double WindSpeed = jsonObjectWind.getDouble("speed");

                        JSONObject jsonObjectCloud = jsonObjectResponse.getJSONObject("clouds");
                        String cloud = jsonObjectCloud.getString("all");

                        JSONObject jsonObjectSys = jsonObjectResponse.getJSONObject("sys");
                        String country = jsonObjectSys.getString("country");

                        String city = jsonObjectResponse.getString("name");

                        txtResults.setTextColor(getColor(R.color.white));

                        outPut = "The current weather of " + city + " (" + country + ") "
                                + "\n Temp: " + decimalFormat.format(temp) + " ºC"
                                + "\n Feels Like: " + decimalFormat.format(feelsLike) + " ºC"
                                + "\n Humidity: " + humidity + "%"
                                + "\n Description: " + description
                                + "\n Wind Speed: " + WindSpeed + "m/s (meters per second"
                                + "\n Cloudiness: " + cloud + "%"
                                + "\n Pressure: " + pressure + "hPa";

                        txtResults.setText(outPut);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}
