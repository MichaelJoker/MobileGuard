package com.paper.mobileguard.activity;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.Toolbar;


import com.paper.mobileguard.R;
import com.paper.mobileguard.utils.MD5Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity{

    private GridView gvHome;
    private String[] names = { "手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具" };
    private int[] icon = {R.drawable.permision_management, R.drawable.harass_intercept,R.drawable.phone_clean
            ,R.drawable.phone_accelarate,R.drawable.flow_management,R.drawable.virus_cleaning,R.drawable.rubbish_clean
            ,R.drawable.power};

    private SharedPreferences mPref;
    private Toolbar toolbar;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolBar();

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        //toolbar = (Toolbar) findViewById(R.id.toolbar);



        copyDB("address.db");// 拷贝归属地查询数据库
        copyDB("antivirus.db");// 拷贝病毒数据库

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
                    case 1:
                        // 通讯卫士
                        startActivity(new Intent(MainActivity.this,
                                CallSafeActivity.class));
                        break;
                    case 2:
                        // 软件管理
                        startActivity(new Intent(MainActivity.this,
                                AppManagerActivity.class));
                        break;
                    case 3:
                        // 进程管理
                        startActivity(new Intent(MainActivity.this,
                               TaskManagerActivity.class));
                        break;
                    case 4:
                        // 流量统计
                        startActivity(new Intent(MainActivity.this,
                                TracfficManagerActivity.class));
                        break;
                    case 5:
                        // 手机杀毒
                        startActivity(new Intent(MainActivity.this,
                                AntivirusActivity.class));
                        break;
                    case 6:
                        // 缓存清理
                        startActivity(new Intent(MainActivity.this,
                                CleanCache2Activity.class));
                        break;
                    case 7:
                        // 高级工具
                        startActivity(new Intent(MainActivity.this,
                                AToolsActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });


    }

    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // App Logo
//        toolbar.setLogo(R.mipmap.logo);
        // Title
//        toolbar.setTitle("WaKaKa");
        // Sub Title
//        toolbar.setSubtitle("Sub title");
        toolbar.setOverflowIcon(ContextCompat.getDrawable(MainActivity.this,R.mipmap.more));
        setSupportActionBar(toolbar);

        //导航按钮
        toolbar.setNavigationIcon(R.mipmap.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Click navigation", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String msg = "";
                switch (item.getItemId()) {
                   /* case R.id.action_delete:
                        msg += "Click delete";
                        break;*/
                    case R.id.action_about:
                        msg += "Click about";
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        break;
                }
                if (!msg.equals("")) {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    /**
     * 加载toobar中的菜单项
     *

     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
    public void showPasswordSetDailog(){
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

    //拷贝数据库


    private void copyDB(String dbName) {
         /*File filesDir = getFilesDir();
         System.out.println("路径:" + filesDir.getAbsolutePath());*/
        File destFile = new File(getFilesDir(), dbName);// 要拷贝的目标地址

        try{
            InputStream in = getAssets().open(dbName);
        }catch(IOException e){
            e.printStackTrace();
        }


        if (destFile.exists()) {
            System.out.println("数据库" + dbName + "已存在!");
            return;
        }

        FileOutputStream out = null;
        InputStream in = null;

        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(destFile);

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

