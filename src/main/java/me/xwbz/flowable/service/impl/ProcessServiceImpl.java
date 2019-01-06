package me.xwbz.flowable.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.xwbz.flowable.bean.*;
import me.xwbz.flowable.bean.Process;
import me.xwbz.flowable.bean.enums.AuditStatus;
import me.xwbz.flowable.bean.enums.OperateType;
import me.xwbz.flowable.service.ProcessService;
import me.xwbz.flowable.util.FlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.history.NativeHistoricActivityInstanceQuery;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ManagementService managementService;

    @Override
    @Transactional
    public void start(User user, ProcessParam param) {
        Map<String, Object> vars = FlowUtil.initStartMap(param);

        log.debug("当前用户id: {} ", Authentication.getAuthenticatedUserId());
        // 设置发起人
        identityService.setAuthenticatedUserId(user.getId());
        // 另外生成编号，这里就使用uuid了
        String no = UUID.randomUUID().toString().replace("-", "");
        ProcessInstance pro = runtimeService.startProcessInstanceByKey(param.getProcessId(), no, vars);
        // 文件材料
        this.createAttachment(null, pro.getId(), param.getFiles());
    }

    private void createAttachment(String taskId, String processInstanceId, List<BaseBean> files) {
        if (files != null && !files.isEmpty()) {
            // 上传附件，可以直接上传字节流（不建议）
            files.forEach(file ->
                    taskService.createAttachment("application/octet-stream", taskId,
                            processInstanceId, file.getName(), null, file.getId()));
        }
    }

    @Override
    public boolean isAssigneeOrCandidate(User user, String taskId) {
        long count = taskService.createTaskQuery()
                .taskId(taskId)
                .or()
                .taskAssignee(user.getId())
                .taskCandidateUser(user.getId())
                .taskCandidateGroup(user.getGroup())
                .endOr().count();
        return count > 0;
    }

    @Override
    @Transactional
    public void beforeAgreeOrReject(String taskId, User user, OperateType operateType, ReasonParam reason) {
        // 组成员操作后方便查询
        taskService.setAssignee(taskId, user.getId());
        if (StringUtils.isNotEmpty(reason.getText())) {
            // 审批意见
            taskService.addComment(taskId, null, null, reason.getText());
        }
        this.createAttachment(taskId, null, reason.getFiles());
    }

    private List<Process> convertProcessList(List<HistoricActivityInstance> activities) {
        return activities.stream().map(a -> {
            // 同上面的拿到这个任务的流程实例
            HistoricProcessInstance p = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(a.getProcessInstanceId())
                    .singleResult();
            // 因为任务已结束（我看到有提到删除任务TaskHelper#completeTask），所以只能从历史里获取
            Map<String, Object> params = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(a.getProcessInstanceId()).list()
                    // 拿到的是HistoricVariableInstance对象，需要转成原来存储的方式
                    .stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            return new Process(p).withActivity(a).withVariables(params);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Process> auditList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
        // 如果不需要筛选自定义参数
        if (auditStatus == null && StringUtils.isEmpty(auditType)) {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    // 我审批的
                    .taskAssignee(user.getId())
                    // 按照开始时间倒序
                    .orderByHistoricActivityInstanceStartTime().desc()
                    // 已结束的（其实就是判断有没有结束时间）
                    .finished()
                    // 分页
                    .listPage((pageNum - 1) * pageSize, pageSize);
            return convertProcessList(activities);
        }
        // 否则需要自定义sql
        // managementService.getTableName是用来获取表名的（加上上一篇提到的liquibase，估计flowable作者对数据表命名很纠结）
        // 这里从HistoricVariableInstance对应的表里找到自定义参数
        // 筛选对象类型不支持二进制，存储的时候尽量使用字符串、数字、布尔值、时间，用来比较的值也有很多限制，例如null不能用like比较。
        String sql = "SELECT DISTINCT RES.* " +
                "FROM " + managementService.getTableName(HistoricActivityInstance.class) + " RES " +
                "INNER JOIN " + managementService.getTableName(HistoricVariableInstance.class) + " var " +
                "ON var.PROC_INST_ID_ = res.PROC_INST_ID_  " +
                "WHERE RES.ASSIGNEE_ = #{assignee} " +
                "AND RES.TENANT_ID_ = #{tenantId} " +
                "AND RES.END_TIME_ IS NOT NULL ";

        List<VariableParam> keys = new ArrayList<>();
        if (auditStatus != null) {
            keys.add(new VariableParam("statusKey", FlowUtil.AUDIT_STATUS_KEY, "statusValue", auditStatus.toString()));
        }
        if (StringUtils.isNotEmpty(auditType)) {
            keys.add(new VariableParam("typeKey", FlowUtil.AUDIT_TYPE_KEY, "typeValue", auditType));
        }
        sql += VariableParam.concatVariableSql(" AND ", keys, " ORDER BY RES.START_TIME_ DESC");
        NativeHistoricActivityInstanceQuery query = historyService.createNativeHistoricActivityInstanceQuery().sql(sql)
                .parameter(HistoryJsonConstants.ASSIGNEE, user.getId());
        // 加入独特配方
        keys.forEach(v -> {
            query.parameter(v.getNameKey(), v.getName());
            query.parameter(v.getValueKey(), v.getValue());
        });
        List<HistoricActivityInstance> activities = query.listPage((pageNum - 1) * pageSize, pageSize);
        return convertProcessList(activities);
    }

    @Override
    public List<Process> waitAuditList(User user, String auditType, int pageNum, int pageSize) {
        TaskQuery query = taskService.createTaskQuery()
                // or() 和 endOr()就像是左括号和右括号，中间用or连接条件
                // 指定是我审批的任务或者所在的组别审批的任务
                // 实在太复杂的情况，建议不使用flowable的查询
                .or()
                .taskAssignee(user.getId())
                .taskCandidateUser(user.getId())
                .taskCandidateGroup(user.getGroup())
                .endOr();
        // 查询自定义字段
        if (StringUtils.isNotEmpty(auditType)) {
            query.processVariableValueEquals(FlowUtil.AUDIT_TYPE_KEY, auditType);
        }
        // 根据创建时间倒序
        return query.orderByTaskCreateTime().desc()
                // 分页
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream().map(t -> {
                    // 拿到这个任务的流程实例，用于显示流程开始时间、结束时间、业务编号
                    HistoricProcessInstance p = historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(t.getProcessInstanceId())
                            .singleResult();
                    return new Process(p).withTask(t) // 拿到任务编号和任务名称
                            // 拿到创建时和中途加入的自定义参数
                            .withVariables(taskService.getVariables(t.getId()))
                            .withFiles(taskService.getProcessInstanceAttachments(p.getId()));
                }).collect(Collectors.toList());
    }

    private Process convertHostoryProcess(HistoricProcessInstance p) {
        // 不管流程是否结束，到历史里查，最方便
        Map<String, Object> params = historyService.createHistoricVariableInstanceQuery().processInstanceId(p.getId()).list()
                .stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
        // 获取最新的一个userTask，也就是任务活动纪录
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(p.getId())
                .orderByHistoricActivityInstanceStartTime().desc()
                .orderByHistoricActivityInstanceEndTime().asc().
                        listPage(0, 1);
        Process data = new Process(p);
        if (!activities.isEmpty()) {
            data.withActivity(activities.get(0));
        }
        return data.withVariables(params);
    }

    @Override
    public List<Process> mineList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
        // startedBy：创建任务时设置的发起人
        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(user.getId());
        // 自定义参数筛选
        if (StringUtils.isNotEmpty(auditType)) {
            instanceQuery.variableValueEquals(FlowUtil.AUDIT_TYPE_KEY, auditType);
        }
        if (auditStatus != null) {
            instanceQuery.variableValueEquals(FlowUtil.AUDIT_STATUS_KEY, auditStatus.toString());
        }

        return instanceQuery
                .orderByProcessInstanceStartTime().desc()
                .listPage((pageNum - 1) * pageSize, pageSize).stream()
                //  获取其中的详细和自定义参数
                .map(this::convertHostoryProcess)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancel(User user, String processInstanceId) {
        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (process == null) {
            throw new RuntimeException("该流程不在运行状态");
        }
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        runtimeService.setVariable(task.getExecutionId(), FlowUtil.AUDIT_STATUS_KEY, AuditStatus.CANCEL.toString());
        runtimeService.deleteProcessInstance(processInstanceId, "用户撤销");
    }
}
