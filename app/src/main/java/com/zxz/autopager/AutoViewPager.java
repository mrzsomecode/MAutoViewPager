package com.zxz.autopager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.zxz.autopager.interfaces.IBanner;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 无限循环轮播图
 * Created by Administrator on 2016/3/31.
 */
public class AutoViewPager<T extends IBanner> extends FrameLayout implements ViewPager.OnPageChangeListener {
    private List<T> imageUrls;
    /**
     * 切换延时（ms）
     */
    private int switchTime = 3000;
    //小圆点的方位
    private int dotMode;

    private boolean openSwitch = false;
    //轮播图片
    private ImageView[] imageViewsList;
    //圆点
    private ImageView[] dotViewsList;
    private ViewPager viewPager;
    /**
     * 当前显示
     */
    private int currentItem = 1;
    private Context context;
    private LinearLayout dotLayout;
    private MyPagerAdapter adapter;
    private Timer timer;
    private TimerTask timerTask;
    private OnClickListener dotClickListener;

    private OnItemClickListener listener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            nextPage();
        }
    };
    private int dotDrawable;

    public List<T> getImageUrls() {
        return imageUrls;
    }

    float upTouchX = 0;

    public void setImageUrls(List<T> imageUrls) {
        this.imageUrls = imageUrls;
        changePages();
    }

    public long getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(int switchTime) {
        this.switchTime = switchTime;
    }

    public void setOpenSwitch(boolean openSwitch) {
        this.openSwitch = openSwitch;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AutoViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public AutoViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoViewPager(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    /**
     * 关闭轮播
     */
    public synchronized void closeSwitch() {
        if (!openSwitch)
            return;
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 开启轮播
     */
    public synchronized void startSwitch() {
        if (!openSwitch)
            return;
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
        }
        timer.schedule(timerTask, switchTime, switchTime);
    }

    /**
     * 初始化
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoViewPager);
        switchTime = a.getInteger(R.styleable.AutoViewPager_switch_time, 3000);
        openSwitch = a.getBoolean(R.styleable.AutoViewPager_open_switch, true);
        dotMode = a.getInteger(R.styleable.AutoViewPager_dot_mode, 2);
        dotDrawable = a.getResourceId(R.styleable.AutoViewPager_dot_drawable, R.drawable.dot_res);
        a.recycle();
        initView();
        initListener();
        changePages();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float scorllX = ev.getX() - upTouchX;
        upTouchX = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //左右移动
                if (scorllX != 0) {
                    if (openSwitch)
                        closeSwitch();
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (openSwitch && timerTask == null)
                    startSwitch();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        requestFocus();
        startSwitch();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closeSwitch();
    }

    private void initView() {
        dotLayout = new LinearLayout(getContext());
        dotLayout.removeAllViews();
        switch (dotMode) {
            case 1:
                dotLayout.setGravity(Gravity.LEFT);
                break;
            case 2:
                dotLayout.setGravity(Gravity.CENTER);
                break;
            case 3:
                dotLayout.setGravity(Gravity.RIGHT);
                break;
        }
        viewPager = new ViewPager(getContext());
        viewPager.setOffscreenPageLimit(3);
        adapter = new MyPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        addView(viewPager, generateDefaultLayoutParams());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        dotLayout.setPadding(16, 16, 16, 16);
        addView(dotLayout, params);
    }

    //圆点点击切换pager
    private void initListener() {
        dotClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSwitch();
                viewPager.setCurrentItem((Integer) v.getTag());
                startSwitch();
            }
        };
    }

    /**
     * 下一页
     */
    private void nextPage() {
        //如果View onDetachedFromWindow 了
        //onPageScrollStateChanged(ViewPager.SCROLL_STATE_IDLE) 不会调用(切换滑动停止时的回调)
        //所以这里也要判断currentItem
        if (currentItem == viewPager.getAdapter().getCount() - 1) {
            currentItem = 1;
            viewPager.setCurrentItem(1, false);
            viewPager.setCurrentItem(2, true);
        } else if (currentItem == 0) {
            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 2, false);
            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1, true);
        } else {
            viewPager.setCurrentItem(currentItem + 1, true);
        }
    }

    /**
     * images and dotView
     */
    private void changePages() {
        if (imageUrls == null || imageUrls.size() == 0)
            return;
        int length = imageUrls.size();
        if (imageViewsList == null || imageViewsList.length != length + 2) {
            imageViewsList = new ImageView[length + 2];
            // 热点个数与图片特殊相等
            // 3 1 2 3 1 无限循环轮播
            ImageView first = createImageView();
            ImageView last = createImageView();
            imageViewsList[length + 1] = last;
            imageViewsList[0] = first;
        }
        if (dotViewsList == null || dotViewsList.length != length) {
            dotViewsList = new ImageView[length];
            dotLayout.removeAllViews();
        }
        for (int i = 0; i < length; i++) {
            ImageView view = imageViewsList[i + 1];
            if (view == null) {
                view = createImageView();
                imageViewsList[i + 1] = view;
            }
            ImageView dotView = dotViewsList[i];
            if (dotView == null) {
                dotView = new ImageView(context);
                dotView.setImageResource(dotDrawable);
                dotView.setPadding(8, 0, 8, 0);
                dotView.setTag(i + 1);
                dotView.setOnClickListener(dotClickListener);
                dotLayout.addView(dotView);
                dotViewsList[i] = dotView;
            }
            dotView.setSelected(currentItem - 1 == i);
        }
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(currentItem);
    }

    @NonNull
    private ImageView createImageView() {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (dotViewsList == null || imageViewsList == null)
            return;
        //3 1 2 3 1 ,position 最后一个 就是第一个
        //position 为 0 就是最后一个
        currentItem = position;
        for (int i = 0; i < dotViewsList.length; i++) {
            if ((position == imageViewsList.length - 1 && i == 0) //第一个
                    || (position == 0 && i == dotViewsList.length - 1) //最后一个
                    || i == position - 1) {
                dotViewsList[i].setSelected(true);
            } else {
                dotViewsList[i].setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (currentItem == viewPager.getAdapter().getCount() - 1) {
                viewPager.setCurrentItem(1, false);
            } else if (currentItem == 0) {
                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 2, false);
            }
        }
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewsList[position]);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = imageViewsList[position];
            final int truePosiiton;
            if (position == 0) {
                truePosiiton = imageUrls.size() - 1;
            } else if (position == imageViewsList.length - 1) {
                truePosiiton = 0;
            } else {
                truePosiiton = position - 1;
            }
            String url = imageUrls.get(truePosiiton).getUrl();
            Glide.with(context).load(url).into(imageView);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AutoViewPager.this.listener != null)
                        AutoViewPager.this.listener.onPagerItemClick(v, truePosiiton);
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return imageViewsList != null ? imageViewsList.length : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public interface OnItemClickListener {
        void onPagerItemClick(View view, int position);
    }

}
