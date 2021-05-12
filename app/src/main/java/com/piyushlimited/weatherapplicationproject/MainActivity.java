package com.piyushlimited.weatherapplicationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.slider.Slider;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.locks.ReadWriteLock;

public class MainActivity extends AppCompatActivity {

    String Location_Provider = LocationManager.GPS_PROVIDER;
    LocationManager mLocationManager;
    LocationListener mLocationListner;
    final int REQUEST_CODE = 101;
    String Latitude ;
    String Longitude ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        getWhetherForCurrentLocation();
        TextView searchButton = (TextView) findViewById(R.id.userAnotherCitySearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getWhetherForCurrentLocation(){
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Latitude = String.valueOf(location.getLatitude());
                Longitude = String.valueOf(location.getLongitude());
                getWhether();
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Toast.makeText(getApplicationContext(),"GPS Enabled",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(getApplicationContext(),"GPS Disabled, Please turn it on",Toast.LENGTH_LONG).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, 5000, 1000, mLocationListner);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWhether();
            }
            else{
                Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void getWhether(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait.......");
        progressDialog.show();
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+Latitude+"&lon="+Longitude+"&appid=dd4778c141c429e7deddd89722a90a63";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Temperature
                    JSONObject object = response.getJSONObject("main");
                    String temperature  = object.getString("temp");
                    Double temp_convert = Double.parseDouble(temperature)-273.15;
                    TextView temp = (TextView) findViewById(R.id.userTempInput);
                    temp.setText(temp_convert.toString().substring(0,4)+ (char) 0x00B0);
                    //City
                    String place = response.getString("name");
                    TextView location = (TextView) findViewById(R.id.userLocationInput);
                    location.setText(place);
                    //Weather
                    String weather_str = response.getJSONArray("weather").getJSONObject(0).getString("main");
                    TextView weather = (TextView) findViewById(R.id.userWhetherInput);
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
                    message = "Server Error, Please try again !";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        queue.add(request);
    }

    private void setWeatherIcon(String iconId){
        ImageView imageView = (ImageView) findViewById(R.id.userWhetherIcon);
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