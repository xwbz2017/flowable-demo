package me.xwbz.flowable.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 开始流程的参数
 */
@Getter
@Setter
public class ProcessParam implements Serializable {
    private static final long serialVersionUID = -987462345684429640L;

    /**
     * 自定义类型
     */
    private String type;

    /**
     * 流程id，是xml文件里的那个processId
     */
    private String processId = "standardRequest";

    /**
     * 审批人
     */
    private List<CandidateParam> auditors;

    /**
     * 其他参数
     */
    private Map<String, Object> params;

    /**
     * 文件材料
     */
    private List<BaseBean> files;
}