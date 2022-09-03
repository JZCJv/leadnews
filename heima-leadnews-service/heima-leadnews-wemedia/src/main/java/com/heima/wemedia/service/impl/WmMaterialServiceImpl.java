package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.minio.MinIOFileStorageService;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.ThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {


    @Autowired
    private MinIOFileStorageService minIOFileStorageService;

    /**
     * 自媒体上传素材
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult<WmMaterial> uploadPicture(MultipartFile multipartFile) {

        //参数校验
        if (multipartFile == null) {

            throw new LeadNewsException(AppHttpCodeEnum.DATA_NOT_EXIST);

        }


        //获取登录的用户
        WmUser wmUser = (WmUser) ThreadLocalUtils.get();
        System.out.println("wmUser========== = " + wmUser);
        System.out.println("userId=================="+wmUser.getId());
        if (wmUser == null) {
            throw new LeadNewsException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }


        //上传到minio
        try {

            //获取一个uuid
            String uuid = UUID.randomUUID().toString().replace("_", "");
            //获取文件后缀
            String originalFilename = multipartFile.getOriginalFilename();
            //截取后缀
            String extName = originalFilename.substring(originalFilename.lastIndexOf("."));

            String fileName = uuid + extName;
            System.out.println("fileName ============= " + fileName);
            String url = minIOFileStorageService.uploadImgFile(null, fileName, multipartFile.getInputStream());

            //存储到DB
            WmMaterial wmMaterial = new WmMaterial();

            wmMaterial.setUserId(wmUser.getId());
            wmMaterial.setUrl(url);
            wmMaterial.setCreatedTime(new Date());
            wmMaterial.setType((short) 0);
            wmMaterial.setIsCollection((short) 0);
            save(wmMaterial);//保存到数据库

            //返回素材信息
            return ResponseResult.okResult(wmMaterial);


        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }

    }

    /**
     * 自媒体素材列表查询
     *
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult fileList(WmMaterialDto dto) {

        //处理参数 为空给默认值
        dto.checkParam();

        //判断是否登录（获取登录用户信息）
        WmUser user = (WmUser) ThreadLocalUtils.get();
        if (user == null) {
            throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);
        }

        //设置查询参数
        IPage<WmMaterial> ipage = new Page<>(dto.getPage(), dto.getSize());

        QueryWrapper<WmMaterial> queryWrapper = new QueryWrapper<>();
        //判断是否为当前登录用户
        queryWrapper.eq("user_id", user.getId());

        //是否收藏
        if (dto.getIsCollection() != null && dto.getIsCollection() == 1) {
            queryWrapper.eq("is_collection", dto.getIsCollection());

        }
        //排序 降序
        queryWrapper.orderByDesc("created_time");

        //分页查询 iPage封装分页前后数据（page,size, 总页数，总记录数，List列表）
        ipage = page(ipage, queryWrapper);



        //封装分页数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) ipage.getTotal());
        pageResponseResult.setCode(200);
        pageResponseResult.setErrorMessage("查询成功");
        pageResponseResult.setData(ipage.getRecords());


        return pageResponseResult;
    }

    /**
     * 收藏素材
     * @param id
     * @return
     */
    @Override
    public ResponseResult collect(Integer id) {

        //设置条件
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setId(id);
        wmMaterial.setIsCollection((short) 1);
        //调用方法修改
        updateById(wmMaterial);

        return ResponseResult.okResult(200);

    }

    /**
     * 取消收藏素材
     * @param id
     */
    @Override
    public ResponseResult cancelCollect(Integer id) {
        //设置条件
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setId(id);
        wmMaterial.setIsCollection((short) 0);
        //调用方法修改
        updateById(wmMaterial);

        return ResponseResult.okResult(200);

    }
}