package com.piyushlimited.weatherapplicationproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        getSupportActionBar().hide();
        EditText search = (EditText) findViewById(R.id.searchInput);
        Button searchButton = (Button) findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search_str = search.getText().toString().trim();
                if(search_str.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter a city",Toast.LENGTH_SHORT).show();
                }
                else{
                    final ProgressDialog progressDialog = new ProgressDialog(WeatherActivity.this);
                    progressDialog.setMessage("Please Wait.......");
                    progressDialog.show();
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+search_str+"&appid=dd4778c141c429e7deddd89722a90a63";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Temperature
                                JSONObject object = response.getJSONObject("main");
                                String temperature  = object.getString("temp");
                                Double temp_convert = Double.parseDouble(temperature)-273.15;
                                TextView temp = (TextView) findViewById(R.id.tempInput);
                                temp.setText(temp_convert.toString().substring(0,4)+ (char) 0x00B0);
                                //City
                                String place = response.getString("name");
                                TextView location = (TextView) findViewById(R.id.locationInput);
                                location.setText(place);
                                //Weather
                                String weather_str = response.getJSONArray("weather").getJSONObject(0).getString("main");
                                TextView weather = (TextView) findViewById(R.id.weatherInput);
                                weather.setText(weather_str);
                                //Weather Icon
                                String icon = response.getJSONArray("weather").getJSONObject(0).getString("icon");
                                setWeatherIcon(icon);
                                progressDialog.dismiss();
                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            String message=null;
                            if(error instanceof NetworkError)
                            {
                                message = "Cannot connect to Internet...Please check your connection!";
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else if(error instanceof ServerError)
                            {
                                message = "You entered wrong city, Please try again !";
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                    queue.add(request);
                }
            }
        });
    }

    private void setWeatherIcon(String iconId){
        ImageView imageView = (ImageView) findViewById(R.id.weatherIcon);
        if(iconId.equals("01d") || iconId.equals("01n")){
            imageView.setImageResource(R.drawable.ic_weather_clear_sky);
        }
        else if(iconId.equals("02d") || iconId.equals("02n")){
            imageView.setImageResource(R.drawable.ic_weather_few_cloud);
        }
        else if(iconId.equals("03d") || iconId.equals("03n")){
            imageView.setImageResource(R.drawable.ic_weather_scattered_clouds);
        }
        else if(iconId.equals("04d") || iconId.equals(("04n"))){
            imageView.setImageResource(R.drawable.ic_weather_broken_clouds);
        }
        else if(iconId.equals("09d") || iconId.equals("09n")){
            imageView.setImageResource(R.drawable.ic_weather_shower_rain);
        }
        else if(iconId.equals("10d") || iconId.equals("10n")){
            imageView.setImageResource(R.drawable.ic_weather_rain);
        }
        else if(iconId.equals("11d") || iconId.equals("11n")){
            imageView.setImageResource(R.drawable.ic_weather_thunderstorm);
        }
        else if(iconId.equals("13d") || iconId.equals("13n")){
            imageView.setImageResource(R.drawable.ic_weather_snow);
        }
        else if(iconId.equals("50d") || iconId.equals("50n")){
            imageView.setImageResource(R.drawable.ic_weather_mist);
        }
    }
}
