package me.xwbz.flowable.bean;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.xwbz.flowable.bean.enums.AuditStatus;
import me.xwbz.flowable.common.util.FlowUtil;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.task.api.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class Process implements Serializable {
    private static final long serialVersionUID = 8416239698142485806L;

    private String processInstanceId;

    private AuditStatus auditStatus;

    private Date startTime;

    private Date endTime;

    private String no;

    private String type;

    private String fromUserName;

    private String toUserName;

    private String taskId;

    private String taskName;

    private String deleteReason;

    private Map<String, Object> params;

    private List<BaseBean> files;

    public Process(HistoricProcessInstance process){
        this.no = process.getBusinessKey();
        this.startTime = process.getStartTime();
        this.endTime = process.getEndTime();
        this.deleteReason = process.getDeleteReason();
        this.processInstanceId = process.getId();
    }

    public Process withTask(Task task){
        this.taskId = task.getId();
        this.taskName = task.getName();
        return this;
    }

    public Process withVariables(Map<String, Object> variables){
        this.params = JSON.parseObject((String) variables.get(FlowUtil.AUDIT_PARAMS_KEY));
        this.fromUserName = FlowUtil.getFromUserName(variables);
        Optional<CandidateParam> candidate = FlowUtil.getCandidate(variables);
        if(candidate.isPresent()){
            this.toUserName = candidate.get().getName();
        }
        this.type = (String) variables.get(FlowUtil.AUDIT_TYPE_KEY);
        this.auditStatus = AuditStatus.valueOf((String) variables.get(FlowUtil.AUDIT_STATUS_KEY));
        return this;
    }

    public Process withFiles(List<Attachment> attachments){
        if(attachments  != null && !attachments.isEmpty()){
            this.files = attachments.stream().map(a -> new BaseBean(a.getUrl(), a.getName())).collect(Collectors.toList());
        }
        return this;
    }

    public Process withActivity(HistoricActivityInstance activity){
        this.taskId = activity.getTaskId();
        this.taskName = activity.getActivityName();
        return this;
    }
}
