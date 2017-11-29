package com.zxz.autopager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zxz.autopager.bean.BannerBean;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public AutoViewPager mPager;
    List<BannerBean> imgs = new ArrayList<>();
    String[] urls = {
            "https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=3903709785,1949644128&fm=85&s=6BAE3063131264645AF514DA0300A0B2",
            "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4270979887,889435537&fm=58",
            "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1861448221,245638653&fm=58"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createData();
        mPager = (AutoViewPager) findViewById(R.id.page);
        mPager.setImageUrls(imgs);
        mPager.setOnItemClickListener(new AutoViewPager.OnItemClickListener() {
            @Override
            public void onPagerItemClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "点击了" + position, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void createData() {
        imgs.add(new BannerBean(urls[0]));
        imgs.add(new BannerBean(urls[1]));
        imgs.add(new BannerBean(urls[2]));
    }
}
