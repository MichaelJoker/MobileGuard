package com.paper.mobileguard.activity;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.paper.mobileguard.R;
import com.paper.mobileguard.adapter.MyBaseAdapter;
import com.paper.mobileguard.bean.BlackNumberInfo;
import com.paper.mobileguard.db.dao.BlackNumberDao;

import java.util.List;

public class CallSafeActivity extends AppCompatActivity {

    private ListView list_view;
    private List<BlackNumberInfo> blackNumberInfos;
    private LinearLayout ll_pb;
    private BlackNumberDao dao;
    private CallSafeAdapter adapter;
    private TextView tv_page_numbeer;
    private EditText et_page_number;

    /*当前页面*/

    private int mCurrentPageNumber = 1;

    /*每页数目*/

    private int mPageSize = 20;

    /*总页数*/
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ll_pb.setVisibility(View.INVISIBLE);

            et_page_number.setText("" + mCurrentPageNumber);

            tv_page_numbeer.setText("/"+totalPage);
            CallSafeAdapter adapter = new CallSafeAdapter(blackNumberInfos,CallSafeActivity.this);
            list_view.setAdapter(adapter);
        }
    };


    private void initData() {

        new Thread(){
            @Override
            public void run() {
                //通过总的记录数/每页的数据
                dao = new BlackNumberDao(CallSafeActivity.this);
                 totalPage = dao.getTotalNumber()/mPageSize;

                blackNumberInfos = dao.findPar(mCurrentPageNumber,mPageSize);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        ll_pb = (LinearLayout) findViewById(R.id.ll_pb);
        //展示加载的圆圈
        ll_pb.setVisibility(View.VISIBLE);
        list_view = (ListView)findViewById(R.id.list_view);
        tv_page_numbeer = (TextView) findViewById(R.id.tv_page_numbeer);
        et_page_number = (EditText) findViewById(R.id.et_page_number);

        /*使黑名单页面失去焦点，不自动弹出键盘*/
        et_page_number.clearFocus();
    }

    private class CallSafeAdapter extends MyBaseAdapter<BlackNumberInfo> {

        private CallSafeAdapter(List lists, Context mContext) {
            super(lists, mContext);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity.this, R.layout.item_call_safe, null);
                holder = new ViewHolder();
                holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_number.setText(lists.get(position).getNumber());
            String mode = lists.get(position).getMode();
            if (mode.equals("1")) {
                holder.tv_mode.setText("来电拦截+短信");
            } else if (mode.equals("2")) {
                holder.tv_mode.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tv_mode.setText("短信拦截");
            }


            final BlackNumberInfo info = blackNumberInfos.get(position);
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        Toast.makeText(CallSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        lists.remove(info);
                       /* //刷新界面
                        adapter.notifyDataSetChanged();*/
                    } else {
                        Toast.makeText(CallSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }


    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }


    /*上一页*/
    public void prePage(View view){
        if(mCurrentPageNumber<=1){
            Toast.makeText(CallSafeActivity.this,"已经是首页了",Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPageNumber--;
        initData();
    }

    /*下一页*/
    public void nextPage(View view){
        if(mCurrentPageNumber>=totalPage){
            Toast.makeText(CallSafeActivity.this, "已经是最后一页了", Toast.LENGTH_SHORT).show();
            return;
        }
        mCurrentPageNumber++;
        initData();
    }

    /*跳转*/
    public void jump(View view){
        String str_page_number = et_page_number.getText().toString().trim();
        if(TextUtils.isEmpty(str_page_number)){
            Toast.makeText(this,"输入不能为空",Toast.LENGTH_SHORT).show();
        }else {
            int number = Integer.parseInt(str_page_number);
            if(number >= 1 && number <= totalPage){
                mCurrentPageNumber = number;
                initData();

            }else {
                Toast.makeText(this,"请输入正确的页码",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
