package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@Api(tags = "店铺操作")
@RequestMapping("/admin/shop")

public class ShopController {

    public static final String SHOP_STATUS = "SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 修改店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("修改店铺状态")
    public Result updateShopStatus(@PathVariable Integer status){
        log.info("修改店铺状态为：{}", status);
        redisTemplate.opsForValue().set(SHOP_STATUS, status);
        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getShopStatus(){
        Integer shopStatus = (Integer) redisTemplate.opsForValue().get(SHOP_STATUS);
        log.info("获取店铺状态：{}", shopStatus);
        return Result.success(shopStatus);
    }
}
