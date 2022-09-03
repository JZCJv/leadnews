package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    /**
     * 绑定文章和内容素材、封面素材的关系
     *
     * @param materialIds
     * @param id
     * @param type
     */
    void saveNewMaterialIds(@Param("materialIds") List<Integer> materialIds, @Param("newsId") Integer newsId, @Param("type") int type);
}
