package com.example.administrator.nlservice;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION = 100;
    private Button btnAuth;
    private Button btnSend;
    private Button btnRemove;
    private Button close;
    private TextView tv;
    private TextView chat;
    private NotificationManager manager;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("text");
            tv.setText(str);//改变主UI界面的通知内容显示
            String chatcontent="";
            //可以在此处解析通知的内容**
            if(str.startsWith("显示通知\npkg:com.tencent.mobileqq")){

                //tv.setText(str);//改变主UI界面的通知内容显示
                int intIndex = str.indexOf("tickerText:");
                if(intIndex!=-1){
                        chatcontent=str.substring(intIndex+11,str.length());
                        chat.setText(chatcontent+"    ---有效聊天指令");

                }



            }





        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this,"启动主UI",Toast.LENGTH_SHORT).show();
        startService(new Intent(this, NotificationCollectorMonitorService.class));
        initView();
    }

    private void initView() {
        btnAuth = (Button) findViewById(R.id.btnAuth);
        btnAuth.setOnClickListener(this);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnRemove = (Button) findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(this);
        close = (Button) findViewById(R.id.close);
        close.setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);
        chat = (TextView) findViewById(R.id.chatcontent);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //注册本地广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getPackageName());
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnAuth.setText(String.format("授权——%s", isNotificationServiceEnable() ? "已授权" : "未授权"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    /**
     * 是否已授权
     *
     * @return
     */
    private boolean isNotificationServiceEnable() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAuth:
                //直接跳转通知授权界面
                //android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS是API 22才加入到Settings里，这里直接写死
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                break;
            case R.id.btnSend:
                sendNotification();
                break;
            case R.id.btnRemove:
                removeNotification();
                break;
            case R.id.close:
                closeapp();
                break;
        }
    }

    /**
     * 发送通知
     */
    private void sendNotification() {
        Notification notification = new Notification.Builder(this)
                // 设置显示在状态栏的通知提示信息
                .setTicker("有新消息")
                // 设置通知的图标
                .setSmallIcon(R.mipmap.ic_launcher)
                // 设置通知内容的标题
                .setContentTitle("一条新通知")
                // 设置通知内容
                .setContentText("通知内容")
                .setWhen(System.currentTimeMillis())
                .build();
        notification.tickerText = "您有一条新的通知";
        notification.defaults = notification.DEFAULT_SOUND;
        manager.notify(NOTIFICATION, notification);
    }

    /**
     * 移除通知
     */
    private void removeNotification() {
        manager.cancel(NOTIFICATION);
    }



    private void closeapp(){
        System.out.println("您按了关闭");
        Intent stopIntent = new Intent(this, NLService.class);
        stopService(stopIntent);
        Intent stopcollect = new Intent(this, NotificationCollectorMonitorService.class);
        stopService(stopcollect);
        //System.exit(0);//正常退出App
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(getPackageName());
        System.exit(0);//正常退出App

    }




}