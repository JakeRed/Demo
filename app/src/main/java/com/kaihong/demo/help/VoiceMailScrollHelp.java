package com.kaihong.demo.help;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

/**
 * Created by italkbb on 2017/4/5.
 */

public class VoiceMailScrollHelp {
    /**
     * 这里通过跑程序得到未展开item的dp值，和展开的dp值，这里使用dp值不会出现问题，当然还有一种方法来获取这两个值
     * recyclerView.getLayoutManager().getChildAt(position).getHeight()来得到item展开后的值，在调用getScrollY的时候
     * 然后判断position-1 或者 position + 1 来得到未展开的值进行计算
     * 这里是用position-1 还是 position + 1 通过判断position + 1（或者position - 1）item是否可见
     * 来限定时+1 还是 -1，这样之后你可以完全使用像素值进行计算。
     *
     */
    private static int itemHeight = 42;  // 未展开item的高度
    private static int openItemHeight = 158;  // 展开的item的高度
    /**
     * 获取滑动距离
     * @param customLinearLayoutManager
     * @return
     */
    public static int getScollYDistance(LinearLayoutManager customLinearLayoutManager) {
        int position = customLinearLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = customLinearLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 计算滑动的距离
     * @param mLayoutManager  RecyclerView 的管理类
     * @param height    RecyclerView高度
     * @param mContext  上下文
     * @param position  点击的position
     * @return
     */
    public static int getScrollY(LinearLayoutManager mLayoutManager, int height, Context mContext, int position) {
        int scrollDistancePx = getScollYDistance(mLayoutManager);
        int scrollDistance = px2dip(mContext,scrollDistancePx);
        height = px2dip(mContext,height);
        // 确定是否为第一条
        if(position == 0){
            return 0 - scrollDistancePx;
        }
        // 判断是否为第一条未显示完全
        if(scrollDistance/(position*itemHeight) == 1 &&
                scrollDistance%(position*itemHeight) != 0){
            // 向下滑动 暂时定义为正数
            return 0 - dip2px(mContext,scrollDistance%(position*itemHeight));
        }
        // 判断是否为最后一条未显示完全
        if((scrollDistance+height) - position*itemHeight < openItemHeight){
            return dip2px(mContext,openItemHeight - (scrollDistance+height) + position*itemHeight);
        }

        return 0;
    }
}
