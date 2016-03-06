package com.paper.mobileguard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paper.mobileguard.R;

public class LostFindActivity extends AppCompatActivity {

    private SharedPreferences mPrefes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefes = getSharedPreferences("config",MODE_PRIVATE);
        TextView tvSafePhone;
        ImageView ivProtect;


        boolean configed = mPrefes.getBoolean("configed", false);// 判断是否进入过设置向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);
            // 根据sp更新安全号码
            tvSafePhone = (TextView) findViewById(R.id.tv_safe_phone);
            String phone = mPrefes.getString("safe_phone", "");
            tvSafePhone.setText(phone);

            // 根据sp更新保护锁
            ivProtect = (ImageView) findViewById(R.id.iv_protect);
            boolean protect = mPrefes.getBoolean("protect", false);
            if (protect) {
                ivProtect.setImageResource(R.drawable.lock);
            } else {
                ivProtect.setImageResource(R.drawable.unlock);
            }
        } else {
            // 跳转设置向导页
            startActivity(new Intent(this,SetupActivity1.class));
            finish();
        }
    }
    /**
     * 重新进入设置向导
     *
     * @param view
     */
    public void reEnter(View view) {
        startActivity(new Intent(this, SetupActivity1.class));
        finish();
    }
}
