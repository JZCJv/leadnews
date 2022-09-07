package com.cjz.schedule;


import com.heima.schedule.ScheduleApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScheduleApplication.class)
public class RedisTest {


    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 添加元素
     */
    @Test
    public void testAdd() {

        Random random = new Random();
        for (int i = 0; i <= 20; i++) {

            redisTemplate.opsForZSet().add("hello","CJZ:"+i,random.nextInt(50));
        }


    }

    /**
     * 查询元素
     */
    @Test
    public void testRange() {

        //根据下标查询（包前包后）
      //  Set<String> result = redisTemplate.opsForZSet().range("hello", 4, 9); //4到9的索引是：第5到第10个数

        //根据元素score值范围（包前包后）
       // Set<String> result = redisTemplate.opsForZSet().rangeByScore("hello", 7, 12);

        //查询元素score小于等于21
        Set<String> result = redisTemplate.opsForZSet().rangeByScore("hello", 0, 1662520431809D);

        System.out.println("result = " + result);


    }

    /**
     * 删除内容
     */
    @Test
    public void testRemove() {
        //根据分数删除
       // Long result = redisTemplate.opsForZSet().removeRangeByScore("hello", 2, 9);


        //
        Long result = redisTemplate.opsForZSet().remove("hello", "CJZ:20");
        System.out.println("result = " + result);
    }
}
