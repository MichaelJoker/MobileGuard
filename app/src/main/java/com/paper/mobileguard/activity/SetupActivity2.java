package com.paper.mobileguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.paper.mobileguard.R;



/**
 * 第2个设置向导页
 *
 */
public class SetupActivity2 extends BaseSetupActivity {

    private CheckBox sivSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSim = (CheckBox)findViewById(R.id.siv_sim);

        String sim = mPref.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            sivSim.setChecked(true);
        } else {
            sivSim.setChecked(false);
        }

        sivSim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivSim.isChecked()) {
                    sivSim.setChecked(true);
                    // 保存sim卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();// 获取sim卡序列号
                    System.out.println("sim卡序列号:" + simSerialNumber);

                    mPref.edit().putString("sim", simSerialNumber).commit();// 将sim卡序列号保存在sp中


                } else {
                    sivSim.setChecked(false);
                    mPref.edit().remove("sim").commit();// 删除已绑定的sim卡
                }
            }
        });
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(this, SetupActivity3.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);// 进入动画和退出动画
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, SetupActivity1.class));
        finish();

        // 两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in,
                R.anim.tran_previous_out);// 进入动画和退出动画
    }
}

