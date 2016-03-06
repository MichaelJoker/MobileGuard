package com.paper.mobileguard.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.paper.mobileguard.R;
import com.paper.mobileguard.utils.MD5Utils;


public class MainActivity extends AppCompatActivity {

    private GridView gvHome;
    private String[] names = { "手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具" };
    private int[] icon = {R.drawable.permision_management, R.drawable.harass_intercept,R.drawable.phone_clean
            ,R.drawable.phone_accelarate,R.drawable.flow_management,R.drawable.virus_cleaning,R.drawable.rubbish_clean
            ,R.drawable.power};

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        // 设置监听
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        // 手机防盗
                        showPasswordDialog();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 显示密码弹窗
     */
    protected void showPasswordDialog() {

        // 判断是否设置密码
        String savedPassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            // 输入密码弹窗
            showPasswordInputDialog();
        } else {
            // 如果没有设置过, 弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }
    /**
     * 输入密码弹窗
     */

    private void showPasswordInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        //将自定义布局文件设置给dialog
        View view = View.inflate(this, R.layout.dailog_input_password,null);
        dialog.setView(view);
        //设置边距为0，保证在2.x版本上兼容
        //dialog.setView(view, 0, 0, 0, 0);

        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);

        Button bthOK = (Button) view.findViewById(R.id.bt_ok);
        Button bthCANCEL = (Button) view.findViewById(R.id.bt_cancel);
        bthOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPref.getString("password", null);

                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        dialog.dismiss();

                        //跳转到手机防盗页
                        startActivity(new Intent(MainActivity.this, LostFindActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "密码错误！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "输入框的内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bthCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();//隐藏dialog
            }
        });

        dialog.show();
    }

    /**
     * 设置密码弹窗
     */
    private void showPasswordSetDailog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_password,null);
        dialog.setView(view);// 将自定义的布局文件设置给dialog
        //dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view
                .findViewById(R.id.et_password_confirm);

        Button btnOK = (Button) view.findViewById(R.id.bt_ok1);
        Button btnCancel = (Button) view.findViewById(R.id.bt_cancel1);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();

                if(!TextUtils.isEmpty(password)&&!passwordConfirm.isEmpty()){
                    if(password.equals(passwordConfirm)){

                        //将密码保存下来
                        mPref.edit().putString("password",MD5Utils.encode(password)).commit();
                        dialog.dismiss();

                        //跳转到手机防盗页
                        startActivity(new Intent(MainActivity.this,LostFindActivity.class));
                    }else{
                        Toast.makeText(MainActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this,"输入框不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.home_list_item, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
            TextView tv = (TextView) view.findViewById(R.id.tv_homeitem_name);
            tv.setText(names[position]);
            iv.setImageResource(icon[position]);
            return view;
        }

    }
}

