package com.paper.mobileguard.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;


import com.paper.mobileguard.R;
import com.paper.mobileguard.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[]) intent.getExtras().get("pdus");

        //获取超级管理员
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        for (Object object : objects) {// 短信最多140字节,
            // 超出的话,会分为多条短信发送,所以是一个数组,因为我们的短信指令很短,所以for循环只执行一次
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = message.getOriginatingAddress();// 短信来源号码
            String messageBody = message.getMessageBody();// 短信内容

            System.out.println(originatingAddress + ":" + messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                // 播放报警音乐, 即使手机调为静音,也能播放音乐, 因为使用的是媒体声音的通道,和铃声无关
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
             /*   abortBroadcast();
             Android 4.4以后默认短信接收不被拦截，除非自己写一个短信应用并设为默认*/
            }else if ("#*location*#".equals(messageBody)) {
                // 获取经纬度坐标
                context.startService(new Intent(context, LocationService.class));// 开启定位服务

                SharedPreferences sp = context.getSharedPreferences("config",
                        Context.MODE_PRIVATE);
                String location = sp.getString("location", "getting location...");
                //如果坐标不发生变化，就不会显示坐标，而且显示的是上一次变化的坐标。
                Log.d("TAG", location);

            } else if ("#*wipedata*#".equals(messageBody)) {
                dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);

            } else if ("#*lockscreen*#".equals(messageBody)) {
                Log.i("TAG","远程锁屏.");
                dpm.resetPassword("123", 0);
                dpm.lockNow();
            }
        }
    }

}

