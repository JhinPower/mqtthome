package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mqttclient.mqtt.MqttService;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MqttService.MqttEventCallBack {

    private TextView connectState;
    private MqttService.MqttBinder mqttBinder;
    private String TAG = "MainActivity";



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder)iBinder;
            mqttBinder.setMqttEventCallback(MainActivity.this);
            if(mqttBinder.isConnected()){
                connectState.setText("已连接");
                subscribeTopics();
            } else {
                connectState.setText("未连接");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectState = findViewById(R.id.connect_state);

        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);

        findViewById(R.id.settings_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.pubsub_test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PubSubTestActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.dev_demo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DevicesDemoActivity.class);
                startActivity(intent);
            }
        });
        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 可以跳转到注册页面
                Intent intent = new Intent(MainActivity.this, MainActivityDark.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "暗色模式", Toast.LENGTH_SHORT).show();
            }
        });


    }

    void subscribeTopics(){
        try {
            mqttBinder.subscribe("/test");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unSubscribeTopics(){
        try {
            mqttBinder.unSubscribe("/test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectSuccess() {
        subscribeTopics();
        connectState.setText("已连接");
    }

    @Override
    public void onConnectError(String error) {
        Log.d(TAG, "onConnectError: "+error);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectState.setText("未连接");
            }
        });
    }

    @Override
    public void onDeliveryComplete() {
        Log.d(TAG, "publish ok");
    }

    @Override
    public void onMqttMessage(String topic, String message) {
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mqttBinder.isConnected()){
                            connectState.setText("已连接");
                            subscribeTopics();
                        } else {
                            connectState.setText("未连接");
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unSubscribeTopics();
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    public void fetchWeather() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = service.getWeather("Shanghai", "your_api_key");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();
                    double temperature = weather.main.temp - 273.15; // 转换开尔文到摄氏度
                    showNotification("当前温度: " + temperature + "°C");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "weather_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("天气更新")
                .setContentText(message)
                .setContentInfo("Info");

        notificationManager.notify(1, notificationBuilder.build());
    }

    public void showWeatherNotification(View view) {
        fetchWeather();
    }


}
