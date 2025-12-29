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
 * 进度表
 * </p>
 *
 * @author 
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("progress")
public class Progress implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 进度ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 选题ID
     */
    private Long topicId;

    /**
     * 进度阶段（如：开题报告、中期检查、结题验收等）
     */
    private String progressStage;

    /**
     * 进度内容
     */
    private String progressContent;

    /**
     * 进度登记时间
     */
    private LocalDateTime progressDate;

    /**
     * 进度状态（pending：待审核、approved：已通过、rejected：已驳回）
     */
    private String status;

    /**
     * 教师评语
     */
    private String teacherComment;

}