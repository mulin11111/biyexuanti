package com.graduation.topic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 选题表
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("topic")
public class Topic implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选题ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 课题名称
     */
    private String title;

    /**
     * 课题描述
     */
    private String description;

    /**
     * 指导教师
     */
    private String teacherName;

    /**
     * 最大可选学生数
     */
    private Integer maxStudents;

    /**
     * 当前已选学生数
     */
    private Integer currentStudents;

    /**
     * 状态：1（可选题）、0（不可选题）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}