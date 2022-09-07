package com.heima.schedule.service.impl;

import com.heima.common.constants.RedisConstants;
import com.heima.common.constants.ScheduleConstants;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskInfoLogsMapper;
import com.heima.schedule.mapper.TaskInfoMapper;
import com.heima.schedule.service.TaskService;
import com.heima.utils.common.BeanHelper;
import com.heima.utils.common.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 任务处理业务
 *
 * @author CAIJIAZHEN
 * @date 2022/09/06
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TaskInfoMapper taskInfoMapper;

    @Autowired
    private TaskInfoLogsMapper taskInfoLogsMapper;

    /**
     * 添加任务
     *主
     * @param task 任务
     * @return long
     */
    @Transactional
    @Override
    public long addTask(Task task) {

        //把任务添加到DB
        addTaskToDB(task);

        //把任务添加到redis
        addTaskToRedis(task);

        return task.getTaskId();
    }

    /**
     * 将任务添加到数据库
     *副1
     * @param task 任务
     */
    private void addTaskToDB(Task task) {

        try {
            //添加任务表
            Taskinfo taskInfo = BeanHelper.copyProperties(task, Taskinfo.class);
            if (taskInfo != null) {
                taskInfo.setExecuteTime(new Date(task.getExecuteTime()));
                taskInfoMapper.insert(taskInfo);
                //把新的任务id赋值给Task对象
                task.setTaskId(taskInfo.getTaskId());
            }

            //添加日志表
            TaskinfoLogs taskinfoLogs = BeanHelper.copyProperties(taskInfo, TaskinfoLogs.class);
            if (taskinfoLogs != null) {
                taskinfoLogs.setVersion(1);//给个默认值 ，后面根据mybatis-puls 的乐观锁拦截器实现更新
                taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
                taskInfoLogsMapper.insert(taskinfoLogs);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 将任务添加到Redis
     *副2
     * @param task 任务
     */
    private void addTaskToRedis(Task task) {

        //判断任务时间是否在未来5分钟之内
        //获取未来5分钟的时间
        long futureTime = DateTime.now().plusMinutes(5).getMillis();

        if (task.getExecuteTime() <= futureTime) {
            //设置key =常量加任务主题  不同主题不同的值
            String key = RedisConstants.TASK_TOPIC_PREFIX + task.getTaskTopic();
            redisTemplate.opsForZSet().add(key, JsonUtils.toString(task), task.getExecuteTime());

        }


    }


    /**
     * 从延迟队列消费任务
     * 重点：从延迟队列取出符合条件（根据score查询，score小于或等于当前时间毫秒值）
     */
    @Override
    public List<Task> pollTask(Integer taskTopic) {
        log.info("开始消费了");

        String key = RedisConstants.TASK_TOPIC_PREFIX + taskTopic;

        //查询redis中符合条件的任务
        System.out.println("现在时间"+System.currentTimeMillis());//测试
        Set<String> taskSet = redisTemplate.opsForZSet().rangeByScore(key, 0, System.currentTimeMillis());
        List<Task> taskList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(taskSet)) {

            for (String taskJson : taskSet) {
                Task task = JsonUtils.toBean(taskJson, Task.class);

                //更新DB数据
                updateTaskToDB(task);

                //删除redis
                redisTemplate.opsForZSet().remove(key, taskJson);

                taskList.add(task);

            }
        }

        log.info("消费结束");

        return taskList;
    }

    /**
     * 更新DB数据
     */
    private void updateTaskToDB(Task task) {

        log.info("更新DB");


        try {
            //删除任务表记录
            taskInfoMapper.deleteById(task.getTaskId());

            //删除任务日志
            TaskinfoLogs taskinfoLogs = taskInfoLogsMapper.selectById(task.getTaskId());
            if (taskinfoLogs != null) {
                taskinfoLogs.setStatus(ScheduleConstants.EXECUTED);//已执行
                taskInfoLogsMapper.updateById(taskinfoLogs);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        log.info("结束更新DB");

    }
}
