package com.example.administrator.nlservice;

import android.content.Intent;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 通知监听服务
 *
 * @author SJL
 * @date 2017/5/22 22:21
 */
public class NLService extends NotificationListenerService {

    @Override
    public void onCreate() {
        Toast.makeText(NLService.this,"NLS启动",Toast.LENGTH_SHORT).show();
        super.onCreate();
        //android.os.Debug.waitForDebugger();
    }

    //监听发送的notification
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        super.onNotificationPosted(sbn);
        //这里只是获取了包名和通知提示信息，其他数据可根据需求取，注意空指针就行
        String pkg = sbn.getPackageName();
        //String title=sbn.getNotification().toString();
        //String title=sbn.getTag();
        //String title=sbn.getId();
        //System.out.println(title);
        String title=sbn.getNotification().extras.get("android.title").toString();
        String text=sbn.getNotification().extras.get("android.text").toString();
        System.out.println("               +++++"+pkg+"+++++               ");
        String notice=title+":"+text;
        System.out.println("++++++++++++++++++++");
        System.out.println(notice);
        CharSequence tickerText = sbn.getNotification().tickerText;
        sendBroadcast(String.format("显示通知\npkg:%s\ncontent:%s\ntickerText:%s",pkg, notice, TextUtils.isEmpty(tickerText) ? "null" : tickerText));
        //sendBroadcast(String.format("显示通知\npkg:%s\ntickerText:%s", pkg, TextUtils.isEmpty(tickerText) ? "null" : tickerText));
    }

    //监听被清楚的notification
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        super.onNotificationRemoved(sbn);
        String pkg = sbn.getPackageName();
        System.out.println("来自"+pkg+"的通知被移除");
        CharSequence tickerText = sbn.getNotification().tickerText;
        sendBroadcast(String.format("移除通知\npkg:%s\ntickerText:%s", pkg, TextUtils.isEmpty(tickerText) ? "null" : tickerText));
    }

    private void sendBroadcast(String msg) {
        Intent intent = new Intent(getPackageName());
        intent.putExtra("text", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("关闭service的命令正在调用");
        super.onDestroy();
    }



    //////////

    /////////


}
