package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsAutoScanService {


    /**
     * 自动扫描wm新闻
     *
     * @param wmNews wm新闻
     */
    public void autoScanWmNews(WmNews wmNews);
}
