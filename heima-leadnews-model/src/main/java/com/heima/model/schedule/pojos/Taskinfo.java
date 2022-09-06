package com.heima.model.schedule.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * taskinfo 任务实体
 *
 * @author CAIJIAZHEN
 * @date 2022/09/06
 */
@Data
@TableName("taskinfo")
public class Taskinfo{

    /**
     * 任务id
     */
    @TableId(type = IdType.ID_WORKER)
    private Long taskId;

    /**
     * 执行时间
     */
    @TableField("execute_time")
    private Date executeTime;

    /**
     * 参数
     */
    @TableField("parameters")
    private String parameters;

    /**
     * 任务主题
     */
    @TableField("task_topic")
    private Integer taskTopic;

}