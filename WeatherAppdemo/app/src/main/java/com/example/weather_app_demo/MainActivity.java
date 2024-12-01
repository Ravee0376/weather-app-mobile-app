package com.example.weather_app_demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText etCity;
    private Button btnGetWeather;
    private TextView tvWeatherResult;

    private final String API_KEY = "eabe4c6154384a68af7c2cb2518e88c3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge mode
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        etCity = findViewById(R.id.etCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        tvWeatherResult = findViewById(R.id.tvWeatherResult);

        // Set up button click listener
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                } else {
                    tvWeatherResult.setText("Please enter a city name!");
                }
            }
        });
    }

    private void getWeather(String city) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvWeatherResult.setText("Failed to fetch weather data!"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    runOnUiThread(() -> parseJSON(jsonData));
                } else {
                    runOnUiThread(() -> tvWeatherResult.setText("City not found!"));
                }
            }
        });
    }

    private void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String cityName = jsonObject.getString("name");
            JSONObject main = jsonObject.getJSONObject("main");
            double temp = main.getDouble("temp");
            tvWeatherResult.setText("City: " + cityName + "\nTemp: " + temp + "°C");
        } catch (Exception e) {
            e.printStackTrace();
            tvWeatherResult.setText("Error parsing weather data!");
        }
    }
}
