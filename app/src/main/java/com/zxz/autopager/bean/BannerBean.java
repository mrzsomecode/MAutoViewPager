package com.zxz.autopager.bean;

import com.zxz.autopager.interfaces.IBanner;

/**
 * ${DESC}
 *
 * @author zxz
 * @time 2017/11/21 15:08
 */

public class BannerBean implements IBanner {
    String url;

    public BannerBean(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
