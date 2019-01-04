package me.xwbz.flowable.service;

import me.xwbz.flowable.bean.Process;
import me.xwbz.flowable.bean.ProcessParam;
import me.xwbz.flowable.bean.ReasonParam;
import me.xwbz.flowable.bean.User;
import me.xwbz.flowable.bean.enums.AuditStatus;
import me.xwbz.flowable.bean.enums.OperateType;

import java.util.List;

public interface ProcessService {

    void start(User user, ProcessParam param);

    /**
     * @param user 当前用户
     * @param taskId 用户任务id
     * @return 当前用户是否是这个申请的用户任务的审批人、候选人或者候选组
     */
    boolean isAssigneeOrCandidate(User user, String taskId);

    /**
     * 审批前操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     * @param taskId 用户任务id
     * @param user 当前用户
     * @param operateType 审批类型：同意，拒绝
     * @param reason 审批材料
     */
    void beforeAgreeOrReject(String taskId, User user, OperateType operateType, ReasonParam reason);

    /**
     * @param user 当前登陆用户
     * @param auditType 类型
     * @return 待我审批的流程列表
     */
    List<Process> waitAuditList(User user, String auditType);

    /**
     * @param user 当前登陆用户
     * @param auditType 类型
     * @param auditStatus 流程状态
     * @return 我已审批的流程列表
     */
    List<Process> auditList(User user, String auditType, AuditStatus auditStatus);

    /**
     * @param user 当前登陆用户
     * @param auditType 类型
     * @param auditStatus 流程状态
     * @return 查看我创建的流程列表
     */
    List<Process> myAuditList(User user, String auditType, AuditStatus auditStatus);

    void cancel(User user, String processInstanceId);
}
