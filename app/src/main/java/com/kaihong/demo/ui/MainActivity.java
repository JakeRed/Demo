package com.kaihong.demo.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kaihong.demo.adapter.DataAdapter;
import com.kaihong.demo.entity.AnimatorEntity;
import com.kaihong.demo.help.VoiceMailScrollHelp;
import com.kaihong.demo.intface.ViewUpdateInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaihong on 2017/4/4.
 * email:redzkh@gmail.com
 *
 * 这个demo想写一个类似于ios 9.2 之后可视化语音信箱界面
 * 使用RecycleView 组件，点击item ，展开相关item(一个简单的展开动画)，
 * 并有一个高亮显示的效果，除了当前item，做一个背景灰色的效果。
 *
 */
public class MainActivity extends AppCompatActivity implements ViewUpdateInterface {
    // demo用到的数据实体
    private List<AnimatorEntity> mDatas  = new ArrayList<>();
    // 数据展示组件RecycleView
    private RecyclerView mRecyclerView = null;
    // RecycleView 对应的布局管理类
    private LinearLayoutManager layoutManager = null;
    // Data Adapter
    private DataAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    /**
     * 数据初始化方法
     */
    private void initData() {
        for(int i = 0 ; i < 20 ; i++ ){
            mDatas.add(new AnimatorEntity(
                    getAddPositionString(getString(R.string.title),i),
                    getAddPositionString(getString(R.string.desc),i)));
        }
    }

    /**
     * 界面初始化方法
     */
    private void initView() {
        // 实例化控件
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);

        // 设置动画时长
        mRecyclerView.getItemAnimator().setChangeDuration(300);
        mRecyclerView.getItemAnimator().setMoveDuration(300);

        // 初始化界面管理类
        layoutManager  = new LinearLayoutManager(this);

        // 给RecycleView 绑定Manager
        mRecyclerView.setLayoutManager(layoutManager);

        // 实例化数据适配器并绑定在控件上
        mAdapter = new DataAdapter (this,mDatas,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 临时的处理字符串方法，方便区分RecycleView里面数据
     * @param context
     * @param position
     * @return
     */
    private String getAddPositionString(String context,int position){
        return context + "--" + position;
    }

    /**
     * 设置RecycleView的背景，动画时间和voiceMail展开时间一样
     *
     * @param start
     * @param end
     */
    public void setBg(int start , int end){
        ValueAnimator colorAnim = ObjectAnimator.ofInt(mRecyclerView,"backgroundColor", start, end);
        colorAnim.setDuration(300);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(0);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    @Override
    public void expandItem(int position) {
        int scrollDistancePx =  VoiceMailScrollHelp.getScrollY(layoutManager,mRecyclerView.getHeight(),this,position);
        if(scrollDistancePx != 0){
            mRecyclerView.scrollBy(mRecyclerView.getScrollX(), scrollDistancePx);
        }
        setBg(this.getResources().getColor(R.color.transparent,getTheme()),
                this.getResources().getColor(R.color.voice_mail_bg,getTheme()));
    }

    @Override
    public void closeItem() {
        setBg(this.getResources().getColor(R.color.voice_mail_bg,getTheme()),
                this.getResources().getColor(R.color.transparent,getTheme()));
    }
}
