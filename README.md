# Demo
    ios 9.2 之后推出了可视化VoiceMail，从开发的角度看问题，发现点击voiceMail 的某一条item的时候，item展开了，并且背景虚化变灰，那么心里面想，如果用android的RecyclerView来做这个界面，点击item之后展开倒不是一个问题，问题在点击之后怎么触发背景变灰而当前item颜色正常呢？这便是我这边博客要解决的问题，这是我第一次写博客，作为一名毕业不到一年的开发者，可能代码设计存在缺陷，如果你看了这篇博客，还劳烦你能批评指正，谢谢！
我需要解决以下问题：
    1，使用RecyclerView动画展开Item。
    2，展开Item之后item的高度改变了，要确保展开后点击的Item完全可见（展开的时候计算滑动距离）。
    3，展开item之后需要将背景变灰，这里并没有盖上一层蒙版也不是去通知每一个item。

首先看一下效果图（/designSketch）：

1，使用动画展开Item
      RecyclerView设置为支持动画，并且设置动画时间，来实现展开动画，展开新添View部分可以自己用addView来添加，也可以直接包含在Item的布局里，通过调用可见课隐藏来操作展开和合上，具体代码如下：
     初始化界面设置动画支持：
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

在绘制item里操作显示和隐藏
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

到这里第一步就实现了，下面我们开始来做第二步。

2，展开Item确保其完全可见
    展开Item的时候，Item的高度随着展开而改变，目前我写的demo是通过dp值来固定展开和未展开的item高度值，而非通过计算，这部分是可以通过计算的，具体计算步骤如下：
1，recyclerView.getLayoutManager().getChildAt(position).getHeight()来得到item展开后的值，在调用getScrollY的时候
2，然后判断position-1 或者 position + 1 来得到未展开的值进行计算
3，这里是用position-1 还是 position + 1 通过判断position + 1（或者position - 1）item是否可见来限定时+1 还是 -1，这样之后你可以完全使用像素值进行计算。
但是dp值理论上在手机上是不会改变的，所以我这里就不算了，那是通过打印得到这两个值，这个算法也很简单：
1，判断是否是第一个条目被点击，如果是，返回RecyclerView的滚动距离，这个距离就是当前item的不可见距离，展开之后RecyclerView向下滑动该距离。
2，如果不是第一个item被点击，那么判断RecyclerView滑动距离除以（position*未展开item的高度）为0，且这两者取余不等于0，那么点击的item为可见区域的第一条，且第一条未完全显示，应该向下滑动这个余数的距离。
3，如果不是第一条item被点击，那么判断下RecyclerView滑动距离+RecyclerView的高度 - （position*未展开item的高度）< （position*展开item的高度），那么点击的item展开之后一定为最后一条，且应该向上滑动，让其完全可见，滑动距离为“<”号左右两边的差值。
4，均不满足这上面条件则不需要滑动。

基于上面的流程，写了一个工具类来计算滑动距离，重点算法如下：
    private static int itemHeight = 42;  // 未展开item的高度
    private static int openItemHeight = 158;  // 展开的item的高度
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

3，更改背景色
    这里将item的背景设置为透明，那么item显示的颜色就是RecycerView的颜色，以至于点击之后更改一下RecyclerView的背景就能达到一个高亮显示当前展开item。这部分目前想到的方法最简单且只刷新点击Item的方法是这样，如果您有好的方法还请告诉我。
在OnBindViewHolder里设置当前点击item为高亮背景，然后在Activity里设置RecyclerView背景为灰色。
            // 判断是否更改背景，模仿出一个item展开后背景黑色的表象
            if(pos == opened){
                total.setBackgroundColor(mContext.getResources().getColor(R.color.white,mContext.getTheme()));
            }else{
                total.setBackgroundColor(mContext.getResources().getColor(R.color.transparent,mContext.getTheme()));
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

到现在就实现了这个效果图的样子，希望批评指正，小弟不胜感激。


