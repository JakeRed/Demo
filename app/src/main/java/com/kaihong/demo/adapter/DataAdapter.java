package com.kaihong.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaihong.demo.entity.AnimatorEntity;
import com.kaihong.demo.intface.ViewUpdateInterface;
import com.kaihong.demo.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaihong on 2017/4/4.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MainViewHolder> {

    private List<AnimatorEntity> mDatas = new ArrayList<>();

    private Context mContext = null;

    // 列表展开标识
    private int opened = -1;

    // 接口回调
    private ViewUpdateInterface updateInterface = null;

    public DataAdapter(Context mContext ,List<AnimatorEntity> mDatas , ViewUpdateInterface updateInterface) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.updateInterface = updateInterface;
    }

    /**
     * 绑定item布局
     * @param parent
     * @param pos
     * @return
     */
    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int pos) {
        return new MainViewHolder((ViewGroup) LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.rv_item, parent, false));
    }

    /**
     * 绑定数据到控件
     * @param holder
     * @param pos
     */
    @Override
    public void onBindViewHolder(MainViewHolder holder, int pos) {
        AnimatorEntity contact = mDatas.get(pos);
        holder.bind(pos, contact);
    }

    /**
     * 返回列表条数
     * @return
     */
    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    /**
     * 实例化控件等操作
     */
    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // 标题
        public TextView title;
        // 隐藏的内容
        public TextView context;

        public LinearLayout total;

        // 实例化
        public MainViewHolder(ViewGroup itemView) {
            super(itemView);
            total = (LinearLayout) itemView.findViewById(R.id.total_list);
            title = ((TextView) itemView.findViewById(R.id.title));
            context = ((TextView) itemView.findViewById(R.id.context));
            itemView.setOnClickListener(this);
        }

        // 此方法实现列表的展开和关闭
        public void bind(int pos, AnimatorEntity info) {
            title.setText(info.getTitle());
            // opened 纪录当前点开的item position ，未点开为-1.
            if (pos == opened){
                context.setText(info.getDesc());
                context.setVisibility(View.VISIBLE);
            } else{
                context.setVisibility(View.GONE);
            }
            // 判断是否更改背景，模仿出一个item展开后背景黑色的表象
            if(pos == opened){
                total.setBackgroundColor(mContext.getResources().getColor(R.color.white,mContext.getTheme()));
            }else{
                total.setBackgroundColor(mContext.getResources().getColor(R.color.transparent,mContext.getTheme()));
            }
        }

        /**
         * 为item添加点击效果
         * @param v
         */
        @Override
        public void onClick(View v) {
            if(opened != -1){
                int oldOpened = opened;
                opened = -1;
                notifyItemChanged(oldOpened);
                updateInterface.closeItem();
            }else{
                opened = getLayoutPosition();
                notifyItemChanged(opened);
                updateInterface.expandItem(opened);
            }
        }
    }
}