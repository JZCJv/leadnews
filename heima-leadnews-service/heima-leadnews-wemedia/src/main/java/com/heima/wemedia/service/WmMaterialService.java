package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 自媒体上传素材
     * @param multipartFile 前端传过来的文件
     * @return
     */
    ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile);

    /**
     * 自媒体素材列表查询
     * @param dto
     * @return
     */
    PageResponseResult fileList(WmMaterialDto dto);

    /**
     * 收藏素材
     * @param id
     * @return
     */
    ResponseResult collect(Integer id);

    /**
     * 取消收藏素材
     * @param id
     */
    ResponseResult cancelCollect(Integer id);
}
