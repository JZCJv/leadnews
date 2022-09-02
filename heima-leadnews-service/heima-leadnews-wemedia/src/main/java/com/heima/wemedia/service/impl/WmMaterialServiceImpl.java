package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.minio.MinIOFileStorageService;
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
    public ResponseResult uploadPicture(MultipartFile multipartFile) {

        //参数校验
        if (multipartFile == null) {

            throw new LeadNewsException(AppHttpCodeEnum.DATA_NOT_EXIST);

        }


        //获取登录的用户
        WmUser wmUser = (WmUser) ThreadLocalUtils.get();
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
            String url = minIOFileStorageService.uploadHtmlFile(null, fileName, multipartFile.getInputStream());

            //存储到DB
            WmMaterial wmMaterial = new WmMaterial();
            wmMaterial.setId(wmUser.getId());
            wmMaterial.setUrl(url);
            wmMaterial.setCreatedTime(new Date());
            wmMaterial.setType((short) 0);
            wmMaterial.setIsCollection((short) 0);
            save(wmMaterial);//保存到数据库

            //返回素材信息
            ResponseResult.okResult(wmMaterial);


        } catch (IOException e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }


        return null;
    }
}
