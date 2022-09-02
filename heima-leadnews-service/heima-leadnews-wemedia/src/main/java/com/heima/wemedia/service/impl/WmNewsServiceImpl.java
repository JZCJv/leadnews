package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.LeadNewsException;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.ThreadLocalUtils;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.stereotype.Service;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    /**
     * 查询自媒体文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult fileList(WmNewsPageReqDto dto) {

        //判断参数是否为空 设置默认值
        dto.checkParam();

        WmUser user = (WmUser) ThreadLocalUtils.get();
        if (user == null) {
            throw new LeadNewsException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);

        }
        //设置分页查询条件
        IPage<WmNews> ipage = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<WmNews> queryWrapper = new QueryWrapper<>();

        //判断登录的用户
        queryWrapper.eq("user_id", user.getId());

        //所属频道id
        if (dto.getChannelId() != null) {
            queryWrapper.eq("channel_id", dto.getChannelId());
        }

        if (dto.getStatus() != null) {
        //状态
            queryWrapper.eq("status", dto.getStatus());

        }

        //关键字
        if (dto.getKeyword() != null) {
            queryWrapper.like("title", dto.getKeyword());
        }

        if (dto.getBeginPubDate() != null && dto.getBeginPubDate() != null) {
        //时间
            queryWrapper.between("created_time", dto.getBeginPubDate(), dto.getEndPubDate());

        }

        //按照创建时间倒序
        queryWrapper.orderByDesc("created_time");

        ipage= page(ipage,queryWrapper);

        //封装分页数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) ipage.getTotal());
        pageResponseResult.setCode(200);
        pageResponseResult.setErrorMessage("查询成功");
        pageResponseResult.setData(ipage.getRecords());




        return pageResponseResult;
    }
}
