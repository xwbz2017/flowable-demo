package me.xwbz.flowable.method;

import me.xwbz.flowable.bean.CandidateParam;
import me.xwbz.flowable.bean.enums.CandidateType;
import me.xwbz.flowable.common.util.FlowUtil;
import org.flowable.engine.delegate.DelegateExecution;

/**
 * 审批相关的方法
 *
 * 用于flowable流程使用
 */
public class AuditMethod {

    /**
     * 是否存在审批者
     * <sequenceFlow sourceRef="decision" targetRef="assignToAuditor">
     *        <conditionExpression xsi:type="tFormalExpression">
     *                 <![CDATA[
     *                     ${auditMethod.existAuditor(execution)}
     *                 ]]>
     *    </conditionExpression>
     *  </sequenceFlow>
     */
    public boolean existAuditor(DelegateExecution execution){
        return FlowUtil.existAuditor(execution);
    }

    /**
     * 获取当前审批者
     */
    public CandidateParam getCurrentAuditor(DelegateExecution execution){
        return FlowUtil.getCandidateFromExecution(execution).orElse(null);
    }

    /**
     * 获取当前审批者id
     * <userTask id="approveTask" name="等待审批" flowable:assignee="${auditMethod.getCurrentAuditorId(execution)}" />
     */
    public String getCurrentAuditorId(DelegateExecution execution){
        CandidateParam candidate = getCurrentAuditor(execution);
        if(candidate.getType() == null || candidate.getType() == CandidateType.USER) {
            return candidate.getId();
        }
        return null;
    }

    /**
     * 获取当前候选组
     */
    public String getCandidateGroups(DelegateExecution execution){
        CandidateParam candidate = getCurrentAuditor(execution);
        if(candidate.getType() == CandidateType.GROUP) {
            return candidate.getId();
        }
        return null;
    }

    public String getCandidateUsers(DelegateExecution execution){
        CandidateParam candidate = getCurrentAuditor(execution);
        if(candidate.getType() == null || candidate.getType() == CandidateType.USER) {
            return candidate.getId();
        }
        return null;
    }

    /**
     * 是否同意申请
     */
    public boolean isApproved(DelegateExecution execution){
        return FlowUtil.isApproved(execution);
    }
}
