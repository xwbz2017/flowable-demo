package me.xwbz.flowable.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import me.xwbz.flowable.bean.ProcessParam;
import me.xwbz.flowable.bean.User;
import me.xwbz.flowable.bean.CandidateParam;
import me.xwbz.flowable.bean.enums.AuditStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.history.HistoricActivityInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FlowUtil {

    /**
     * 审批人列表
     */
    private static final String AUDITOR_LIST_KEY = "AUDITOR_LIST";

    /**
     * 当前审批人
     */
    private static final String AUDITOR_KEY = "AUDITOR";

    /**
     * 当前审批人下标
     */
    private static final String AUDITOR_IDX_KEY = "AUDITOR_IDX";

    /**
     * 是否已审批
     **/
    public static final String APPROVED_KEY = "AUDIT_APPROVED";

    /**
     * 申请类型
     */
    public static final String AUDIT_TYPE_KEY = "AUDIT_TYPE";

    /**
     * 申请状态
     */
    public static final String AUDIT_STATUS_KEY = "AUDIT_STATUS";

    /**
     * 其他参数
     */
    public static final String AUDIT_PARAMS_KEY = "AUDIT_PARAMS";

    private static final String[] INCLUDE_ACTIVITY_TYPE_ARRAY = {"userTask", "startEvent"};

    private static final String ID_KEY = "USERID";

    private static final String NAME_KEY = "USERNAME";

    private FlowUtil() {
    }

    public static boolean includeActivityType(HistoricActivityInstance activity) {
        return ArrayUtils.contains(INCLUDE_ACTIVITY_TYPE_ARRAY, activity.getActivityType());
    }

    public static boolean existAuditor(DelegateExecution execution) {
        return execution.hasVariable(AUDITOR_KEY);
    }

    public static void setCandidate(DelegateExecution execution, CandidateParam candidate) {
        execution.setVariable(AUDITOR_KEY, JSONObject.toJSONString(candidate));
    }

    public static Optional<CandidateParam> getCandidateFromExecution(DelegateExecution execution) {
        return Optional.ofNullable(JSONObject.toJavaObject(JSONObject.parseObject((String) execution.getVariable(AUDITOR_KEY)), CandidateParam.class));
    }

    public static List<CandidateParam> getCandidateList(DelegateExecution execution){
        return JSON.parseArray((String) execution.getVariable(AUDITOR_LIST_KEY), CandidateParam.class);
    }

    /**
     * 切换到下一个审批人
     */
    public static void toNextCandidate(DelegateExecution execution) {
        // 去掉“同意”标识
        execution.removeVariable(APPROVED_KEY);
        execution.removeVariable(AUDITOR_KEY);
        execution.setVariable(AUDIT_STATUS_KEY, AuditStatus.WAIT_AUDIT.toString());

        // 拿到审批人列表
        List<CandidateParam> auditorList = FlowUtil.getCandidateList(execution);
        // 当前审批人在审批人列表的下标
        Integer auditorIdx = execution.getVariable(AUDITOR_IDX_KEY, Integer.class);
        if (auditorIdx == null) {
            // 第一次分配，初始化为第一个
            auditorIdx = 0;
        } else if (auditorIdx + 1 >= auditorList.size()) {
            //  所有审批人审批完成，结束分配
            return;
        } else {
            // 下一个
            auditorIdx++;
        }
        CandidateParam auditor = auditorList.get(auditorIdx);
        FlowUtil.setCandidate(execution, auditor);
        execution.setVariable(AUDITOR_IDX_KEY, auditorIdx);
    }

    public static void end(DelegateExecution execution, AuditStatus auditStatus){
        execution.setVariable(AUDIT_STATUS_KEY, auditStatus.toString());
    }


    public static Optional<CandidateParam> getCandidate(Map<String, Object> params) {
        return Optional.ofNullable(JSONObject.toJavaObject(JSONObject.parseObject((String) params.get(AUDITOR_KEY)), CandidateParam.class));
    }

    public static String getFromUserName(Map<String, Object> params) {
        return (String) params.get(NAME_KEY);
    }

    public static String getFromUserId(Map<String, Object> params) {
        return (String) params.get(ID_KEY);
    }

    public static void removeCandidate(DelegateExecution execution) {
        execution.removeVariable(AUDITOR_KEY);
    }

    public static boolean isApproved(DelegateExecution execution){
        Boolean approved = execution.getVariable(APPROVED_KEY, Boolean.class);
        return approved != null && approved;
    }

    public static Map<String, Object> userToMap(User user) {
        Map<String, Object> res = new HashMap<>(2);
        res.put(ID_KEY, user.getId());
        res.put(NAME_KEY, user.getName());
        return res;
    }


    public static Map<String, Object> initStartMap(ProcessParam param){
        Map<String, Object> vars = new HashMap<>();
        // 放入申请类型
        vars.put(AUDIT_TYPE_KEY, param.getType());
        // 放入审批人人列表
        vars.put(AUDITOR_LIST_KEY, JSONObject.toJSONString(param.getAuditors()));
        // 放入其他参数
        vars.put(AUDIT_PARAMS_KEY, JSONObject.toJSONString(param.getParams()));
        // 放入审批状态
        vars.put(AUDIT_STATUS_KEY, AuditStatus.WAIT_AUDIT.toString());
        return vars;
    }
}
