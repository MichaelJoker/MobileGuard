package com.paper.mobileguard.activity;

import android.content.pm.IPackageStatsObserver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paper.mobileguard.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CleanCacheActivity extends AppCompatActivity {

    private static final int STATE_UPDATE_STATUS = 1;// 更新扫描状态
    private static final int STATE_SCAN_FINISH = 2;// 扫描结束
    private static final int STATE_FIND_CACHE = 3;// 发现缓存

    private ProgressBar pbProgress;
    private TextView tvStatus;
    private LinearLayout llContainer;
    private PackageManager mPM;
    private List<CacheInfo> cacheLists;
    private PackageManager packageManager;


    /*private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case STATE_UPDATE_STATUS:
                    String name = (String) msg.obj;
                    tvStatus.setText("正在扫描:" + name);
                    break;
                case STATE_FIND_CACHE:
                    final CacheInfo info = (CacheInfo) msg.obj;

                    View view = View.inflate(getApplicationContext(),
                            R.layout.item_clean_cache, null);
                    TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                    ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tvCacheSize = (TextView) view
                            .findViewById(R.id.tv_cache_size);
                    ImageView ivDelete = (ImageView) view
                            .findViewById(R.id.iv_delete);

                    tvName.setText(info.appName);
                    ivIcon.setImageDrawable(info.icon);
                    tvCacheSize.setText(Formatter.formatFileSize(
                            getApplicationContext(), info.cacheSize));
                    ivDelete.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // 清理单个缓存
                            // deleteApplicationCacheFiles, 只有系统应用才有此权限
                            // 需要跳到系统页面清理缓存
                            // Starting: Intent {
                            // act=android.settings.APPLICATION_DETAILS_SETTINGS
                            // dat=package:com.android.browser flg=0x10000000
                            // cmp=com.android.settings/.applications.InstalledAppDetails
                            // } from pid 200
                            Intent intent = new Intent(
                                    "android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:" + info.packageName));
                            startActivity(intent);
                        }
                    });

                    llContainer.addView(view, 0);
                    break;

                case STATE_SCAN_FINISH:
                    tvStatus.setText("扫描完毕");
                    break;

                default:
                    break;
            }
        };
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_clean_cache);
        list_view = (ListView) findViewById(R.id.list_view);


        //垃圾的集合
        cacheLists = new ArrayList<CacheInfo>();
        packageManager = getPackageManager();

        new  Thread(){
            public void run(){

                //安装到手机上面所有的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                for (PackageInfo packageInfo : installedPackages) {
                    getCacheSize(packageInfo);
                }
                handler.sendEmptyMessage(0);
            };
        }.start();

    }

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            CacheAdapter adapter = new CacheAdapter();
            list_view.setAdapter(adapter);
        };
    };

    private ListView list_view;

    private class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null ;
            ViewHolder holder;

            if(convertView == null){
                View.inflate(CleanCacheActivity.this,R.layout.item_clean_cache,null);
                holder = new ViewHolder();

                holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.cacheSize = (TextView) view.findViewById(R.id.tv_cache_size);
                holder.appName = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.icon.setImageDrawable(cacheLists.get(position).icon);
            holder.cacheSize.setText("缓存大小"+ Formatter.formatFileSize(CleanCacheActivity.this,cacheLists.get(position).cacheSize));
            holder.appName.setText(cacheLists.get(position).appName);
            return view;
        }
    }

    static class ViewHolder{

        ImageView icon;
        TextView cacheSize;
        TextView appName;
    }

    //获取到缓存的大小
    private void getCacheSize(PackageInfo packageInfo) {
        try {
            //通过反射获取到当前的方法
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            /*
            第一个参数表示当前这个方法由于调用的
            第二个参数表示包名
            * */
            method.invoke(packageManager,packageInfo.applicationInfo.packageName,new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {
        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;

        }

        // 此方法在子线程运行
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            //  获取当前应用的缓存大小
            long cacheSize = pStats.cacheSize;

            if (cacheSize > 0) {
                System.out.println("当前应用名字"+packageInfo.applicationInfo.loadLabel(packageManager)+cacheSize);
                CacheInfo cacheInfo= new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.icon = icon;
               String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.appName = appName;
                cacheInfo.cacheSize = cacheSize;
                cacheLists.add(cacheInfo);

            }
        }
    }

    class CacheInfo {
        public String appName;
        public String packageName;
        public Drawable icon;
        public long cacheSize;
    }
}
