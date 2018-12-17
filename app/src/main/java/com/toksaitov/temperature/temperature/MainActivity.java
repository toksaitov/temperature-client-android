package com.toksaitov.temperature.temperature;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String API_URL = "http://temperature.auca.space:8080/measurements?limit=100";
    private final static int UPDATE_PERIOD = 2000;

    private TextView temperatureTextView;
    private ChartView temperatureChartView;

    private RequestQueue networkingRequestQueue;

    private Handler handler;

    private ArrayList<Float> dataPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupNetworking();
        setupPeriodicUpdates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                // TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupUI() {
        temperatureTextView = findViewById(R.id.temperatureTextView);
        temperatureChartView = findViewById(R.id.temperatureChartView);
    }

    private void setupNetworking() {
        networkingRequestQueue = Volley.newRequestQueue(this);
    }

    private void setupPeriodicUpdates() {
        handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                getServerData();

                handler.postDelayed(this, UPDATE_PERIOD);
            }
        });
    }

    private void getServerData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    parseServerData(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    reportErrorToUser(getString(R.string.load_failure_toast_message));
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "text/plain");

                return params;
            }
        };
        networkingRequestQueue.add(stringRequest);
    }

    private void parseServerData(String response) {
        String[] lines = response.split("\n");
        if (lines.length == 0) {
            reportErrorToUser(getString(R.string.failed_to_parse_data_toast_message));
            return;
        }

        dataPoints.clear();
        int i = 0;
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 1) {
                continue;
            }

            float temperature;
            try {
                temperature = Float.parseFloat(parts[1]);
            } catch (Exception e) {
                continue;
            }

            if (i == 0) {
                temperatureTextView.setText(String.format(Locale.getDefault(), getString(R.string.temperatureFormat), temperature));
            }

            dataPoints.add(temperature);

            ++i;
        }

        temperatureChartView.setDataPoints(dataPoints);
    }

    private void reportErrorToUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
