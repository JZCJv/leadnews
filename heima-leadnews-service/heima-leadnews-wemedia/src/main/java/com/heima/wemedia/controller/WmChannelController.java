package com.heima.wemedia.controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询所有频道
 */
@RestController
@RequestMapping("/api/v1/channel")
@Api(value = "查询所有频道",tags = "channels")
public class WmChannelController {


    @Autowired
    private WmChannelService wmChannelService;

    /**
     * 查询所有频道
     *
     * @return
     */
    @GetMapping("/channels")
    @ApiOperation("查询所有频道的接口")
    public ResponseResult channels() {

        return ResponseResult.okResult( wmChannelService.list());

    }
}
