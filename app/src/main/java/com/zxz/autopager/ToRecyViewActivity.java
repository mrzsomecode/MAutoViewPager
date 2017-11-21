package com.zxz.autopager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zxz.autopager.bean.BannerBean;

import java.util.ArrayList;
import java.util.List;

public class ToRecyViewActivity extends Activity {
    List<BannerBean> imgs = new ArrayList<>();
    String[] urls = {
            "https://ss0.baidu.com/73F1bjeh1BF3odCf/it/u=3903709785,1949644128&fm=85&s=6BAE3063131264645AF514DA0300A0B2",
            "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4270979887,889435537&fm=58",
            "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1861448221,245638653&fm=58"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_recy_view);
        createData();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MAdapter());
    }

    private void createData() {
        imgs.add(new BannerBean(urls[0]));
        imgs.add(new BannerBean(urls[1]));
        imgs.add(new BannerBean(urls[2]));
    }

    class MAdapter extends RecyclerView.Adapter<MAdapter.MHolder> {
        @Override
        public MHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
            return new MHolder(view);
        }

        @Override
        public void onBindViewHolder(MHolder holder, int position) {
            AutoViewPager mPager = (AutoViewPager) holder.itemView.findViewById(R.id.page);
            mPager.setImageUrls(imgs);
            mPager.setOnItemClickListener(new AutoViewPager.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(getApplicationContext(), "点击了" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        class MHolder extends RecyclerView.ViewHolder {

            public MHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
