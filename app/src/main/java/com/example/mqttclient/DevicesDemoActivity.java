package com.example.mqttclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mqttclient.mqtt.MqttService;
import com.example.mqttclient.protocol.AirConditioningMessage;
import com.example.mqttclient.protocol.BoolMessage;
import com.example.mqttclient.protocol.FloatMessage;
import com.example.mqttclient.protocol.IntMessage;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DevicesDemoActivity extends AppCompatActivity implements MqttService.MqttEventCallBack, CompoundButton.OnCheckedChangeListener {

    private TextView connectState, temperatureValue, humidityValue, pmValue, gasValue, doorStatus;
    private EditText airCconditioningValue;
    private MqttService.MqttBinder mqttBinder;
    private String TAG = "MainActivity";
    private Switch parlourLightSwitch, curtain_switch, fan_socket_switch, air_conditioning_switch, parlourLightSwitch2, socketSwitch1, socketSwitch2 ;
    private Map<String, Integer> subscribeTopics = new HashMap<>();
    private ImageView image1,image2, image5, image6, image7;

    private boolean isDoorOpen = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttBinder = (MqttService.MqttBinder) iBinder;
            mqttBinder.setMqttEventCallback(DevicesDemoActivity.this);
            if (mqttBinder.isConnected()) {
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
        setContentView(R.layout.activity_devices_demo);

        connectState = findViewById(R.id.dev_connect_state);

        Intent mqttServiceIntent = new Intent(this, MqttService.class);
        bindService(mqttServiceIntent, connection, Context.BIND_AUTO_CREATE);

        temperatureValue = findViewById(R.id.temperature_value);

        humidityValue = findViewById(R.id.humidity_value);
        pmValue = findViewById(R.id.pm_value);
        gasValue = findViewById(R.id.gas_value);
        doorStatus = findViewById(R.id.door_status);

        airCconditioningValue = findViewById(R.id.air_conditioning_value);
        parlourLightSwitch = findViewById(R.id.parlour_light_switch);
        parlourLightSwitch2 = findViewById(R.id.parlour_light_switch2);
        parlourLightSwitch.setOnCheckedChangeListener(this);
        parlourLightSwitch2.setOnCheckedChangeListener(this);

        socketSwitch1 = findViewById(R.id.socket1);
        socketSwitch2 = findViewById(R.id.socket2);
        socketSwitch1.setOnCheckedChangeListener(this);
        socketSwitch2.setOnCheckedChangeListener(this);

        curtain_switch = findViewById(R.id.curtain_switch);
        curtain_switch.setOnCheckedChangeListener(this);
        fan_socket_switch = findViewById(R.id.fan_socket_switch);
        fan_socket_switch.setOnCheckedChangeListener(this);
        air_conditioning_switch = findViewById(R.id.air_conditioning_switch);
        air_conditioning_switch.setOnCheckedChangeListener(this);

        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image5=findViewById(R.id.image5);
        final Animation animation= AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setFillAfter(true);
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean open=fan_socket_switch.isChecked();
                if (open)
                    image1.clearAnimation();
                else
                    image1.startAnimation(animation);
                fan_socket_switch.setChecked(!open);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean open=parlourLightSwitch.isChecked();
                if (open)
                    image2.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
                else
                    image2.setImageResource(R.drawable.ic_wb_incandescent_black_24dp);
                parlourLightSwitch.setChecked(!open);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean open=parlourLightSwitch2.isChecked();
                if (open)
                    image5.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
                else
                    image5.setImageResource(R.drawable.ic_wb_incandescent_black_24dp);
                parlourLightSwitch2.setChecked(!open);
            }
        });



    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.parlour_light_switch:
                try {
                    if (compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/light1",
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        mqttBinder.publishMessage("/test/light1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.parlour_light_switch2:
                try {
                    if (compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/light2",
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        mqttBinder.publishMessage("/test/light2",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;


            case R.id.socket1:
                try {
                    if (compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/outlet1",
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        mqttBinder.publishMessage("/test/outlet1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;


            case R.id.curtain_switch:
                try {
                    if (compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/curtain1",
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        mqttBinder.publishMessage("/test/curtain1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.fan_socket_switch:
                try {
                    if (compoundButton.isChecked()) {
                        mqttBinder.publishMessage("/test/fan1",
                                new Gson().toJson(new BoolMessage(true)));
                    } else {
                        mqttBinder.publishMessage("/test/fan1",
                                new Gson().toJson(new BoolMessage(false)));
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.air_conditioning_switch:
                try {
                    if (compoundButton.isChecked()) {
                        String json = new Gson().toJson(new AirConditioningMessage(true,
                                Float.parseFloat(airCconditioningValue.getText().toString())));
                        Log.d("json",json);
                        mqttBinder.publishMessage("/test/airConditioning",json);
                    } else {
                        String json = new Gson().toJson(new AirConditioningMessage(false,
                                Float.parseFloat(airCconditioningValue.getText().toString())));
                        Log.d("json",json);
                        mqttBinder.publishMessage("/test/airConditioning",json);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    void subscribeTopics() {
        try {
            subscribeTopics.put("/test/temp",1);
            subscribeTopics.put("/test/hum", 2);
            subscribeTopics.put("/test/pm",3);
            subscribeTopics.put("/test/gas",4);
            subscribeTopics.put("/test/door",5);
            for(Map.Entry<String, Integer> entry : subscribeTopics.entrySet()){
                mqttBinder.subscribe(entry.getKey());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void unSubscribeTopics() {
        try {
            for(Map.Entry<String, Integer> entry : subscribeTopics.entrySet()){
                mqttBinder.unSubscribe(entry.getKey());
            }
            subscribeTopics.clear();
        } catch (MqttException e) {
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
        Log.d(TAG, "onConnectError: " + error);
        connectState.setText("未连接");
        subscribeTopics.clear();
    }

    @Override
    public void onDeliveryComplete() {
        Log.d(TAG, "publish ok");
    }

//    @Override
//    public void onMqttMessage(String topic, String message) {
//        Log.d("onMqttMessage", "topic:"+topic+ "message length:"+ message.length() + ", message:"+message);
//        Gson gson = new Gson();
//        switch (subscribeTopics.get(topic)){
//            case 1:
//                temperatureValue.setText(String.valueOf(gson.fromJson(message.trim(), FloatMessage.class).value));
//                break;
//
//            case 2:
//                humidityValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 3:
//                pmValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 4:
//                gasValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
//                break;
//
//            case 5:
////                String status = gson.fromJson(message.trim(), BoolMessage.class).value ?"开":"关";
////                doorStatus.setText(status);
//                isDoorOpen = !isDoorOpen;
//                doorStatus.setText(isDoorOpen ? "开" : "关");
//                break;
//        }
//
//    }
    @Override
    public void onMqttMessage(String topic,String message){
        Log.d("onMqttMessage","topic:"+topic+"message length:"+message.length()+
                ",message:"+message);
        Gson gson = new Gson();
        switch (subscribeTopics.get(topic)){
            case 1:
                temperatureValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        FloatMessage.class).value));
                break;

            case 2:
                humidityValue.setText(String.valueOf(gson.fromJson(message.trim(),
                        IntMessage.class).value));
                break;
            case 3:
                pmValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
                IntMessage int1Message = gson.fromJson(message.trim(), IntMessage.class);
                int pmValue = int1Message.value;
                if (pmValue > 50) {
                    // 发送通知
                    sendNotification("PM2.5浓度超标", "PM2.5浓度已达到: " + pmValue+"！！！请注意防护。");
                }
                break;
            case 4:
                gasValue.setText(String.valueOf(gson.fromJson(message.trim(), IntMessage.class).value));
                IntMessage intMessage = gson.fromJson(message.trim(), IntMessage.class);
                int gasValue = intMessage.value;
                if (gasValue > 50) {
                    // 发送通知
                    sendNotification("可燃气体浓度超标", "可燃气体浓度已达到: " + gasValue);
                }
                break;

            case 5:
    //                String status = gson.fromJson(message.trim(),BoolMessage.class).value ?"开":"关";
    //                doorStatues.setText(status);
                isDoorOpen = !isDoorOpen;
                doorStatus.setText(isDoorOpen ? "开" : "关");


                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mqttBinder.isConnected()) {
            connectState.setText("已连接");
            subscribeTopics();
        } else {
            connectState.setText("未连接");
        }
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


    private void sendNotification(String title, String content) {
        // 创建通知管理器
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            // 创建通知构建器
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                    .setSmallIcon(R.drawable.ic_launcher_background) // 设置通知小图标
                    .setContentTitle(title) // 设置通知标题
                    .setContentText(content) // 设置通知内容
                    .setPriority(NotificationCompat.PRIORITY_HIGH); // 设置通知优先级

            // 发送通知
            notificationManager.notify(new Random().nextInt(), builder.build());
        }
    }
    private void createNotificationChannel() {
        // 检查Android版本是否支持NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建NotificationChannel实例
            NotificationChannel channel = new NotificationChannel("your_channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);

            // 设置NotificationChannel属性
            channel.setDescription("Channel Description"); // 可选：设置渠道描述
            channel.enableLights(true); // 是否在通知时闪烁灯
            channel.setLightColor(Color.RED); // 灯闪烁的颜色
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000}); // 振动模式
            channel.enableVibration(true); // 是否振动

            // 获取系统服务中的NotificationManager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            // 注册NotificationChannel
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
