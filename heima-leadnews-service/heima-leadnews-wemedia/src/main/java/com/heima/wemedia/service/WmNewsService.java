package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询自媒体文章列表
     * @return
     */
    ResponseResult fileList(WmNewsPageReqDto wmNewsPageReqDto);

    /**
     * 发表自媒体文章
     * @param dto
     * @return
     */
    ResponseResult submit(WmNewsDto dto);
}
