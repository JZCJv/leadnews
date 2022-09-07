package com.cjz.schedule;

import com.heima.common.constants.RedisConstants;
import com.heima.common.constants.ScheduleConstants;
import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import com.heima.utils.common.JsonUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 测试延迟任务的方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScheduleApplication.class)
public class TaskTest {

    @Autowired
    private TaskService taskService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    //发布文章
    @Test
    public void testTask() {
        //模拟文章发布
        for (int i = 0; i < 10; i++) {
            Task task = new Task();
            task.setTaskTopic(ScheduleConstants.TASK_TOPIC_NEWS_PUBLISH);
            Map<String, Object> map = new HashMap<>();
            map.put("id", 6294);
            task.setParameters(JsonUtils.toString(map));
            task.setExecuteTime(DateTime.now().plusMinutes(i).getMillis());
            taskService.addTask(task);
        }
    }


    /**
     * 消费延迟任务
     */
    //TODO  消费
    @Test
    public void testPollTask() {
        System.out.println("测试");
        List<Task> taskList = taskService.pollTask(1);
        System.out.println("taskList = " + taskList);

    }

    //测试查询redis
    @Test
    public void testSelectRedis() {

        String key = RedisConstants.TASK_TOPIC_PREFIX + 1;
        System.out.println("key = " + key);

        //查询redis中符合条件的任务
        System.out.println("现在时间" + System.currentTimeMillis());//测试
        long timeNow = System.currentTimeMillis();
        System.out.println("timeNow = " + timeNow);

        Set<String> taskSet = redisTemplate.opsForZSet().rangeByScore(key, 0, System.currentTimeMillis());
        System.out.println("taskSet = " + taskSet);
    }
}
