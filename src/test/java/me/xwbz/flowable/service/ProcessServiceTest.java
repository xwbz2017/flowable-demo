package me.xwbz.flowable.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.xwbz.flowable.bean.*;
import me.xwbz.flowable.bean.Process;
import me.xwbz.flowable.bean.enums.AuditStatus;
import me.xwbz.flowable.bean.enums.CandidateType;
import me.xwbz.flowable.bean.enums.OperateType;
import me.xwbz.flowable.util.FlowUtil;
import org.flowable.engine.TaskService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class ProcessServiceTest extends BaseTest {

    @Autowired
    private ProcessService processService;
    @Autowired
    private TaskService taskService;

    private User user = new User("2", "李四", "user");

    @Test
    public void start() {
        ProcessParam param = new ProcessParam();
        param.setType("加班");
        param.setAuditors(Arrays.asList(
                new CandidateParam("1", "张三", CandidateType.USER),
                new CandidateParam("admin", "管理员", CandidateType.GROUP)));
        param.setFiles(Collections.singletonList(new BaseBean("123", "123.png")));
        param.setParams(Collections.singletonMap("时长", 2));
        param.setProcessId("standardRequest");

        processService.start(user, param);
        myAuditList();
    }

    @Test
    public void agree(){
        User auditor = new User("1", "张三", "admin");

        ProcessParam param = new ProcessParam();
        param.setType("加班");
        param.setAuditors(Arrays.asList(
                new CandidateParam(auditor.getId(), auditor.getName(), CandidateType.USER),
                new CandidateParam("admin", "管理员", CandidateType.GROUP)));
        param.setFiles(Collections.singletonList(new BaseBean("123", "123.png")));
        param.setParams(Collections.singletonMap("时长", 2));
        param.setProcessId("standardRequest");

        processService.start(user, param);

        Process process = processService.waitAuditList(auditor, null).get(0);
        log.info("待审批：{}", JSON.toJSONString(process));

//        if(processService.isAssigneeOrCandidate(auditor, process.getTaskId())){
//        }

        processService.beforeAgreeOrReject(process.getTaskId(), auditor, OperateType.AGREE,
                new ReasonParam("准了", Arrays.asList(new BaseBean("234", "234.txt"))));
        taskService.complete(process.getTaskId(), Collections.singletonMap(FlowUtil.APPROVED_KEY, true));

        List<Process> list = processService.waitAuditList(auditor, null);
        log.info("待审批列表：{}", JSON.toJSONString(list));
        Assert.assertTrue(list.stream().noneMatch(p -> p.getTaskId() != null && p.getTaskId().equals(process.getTaskId())));

        list = processService.auditList(user, null, null);
        log.info("已审批列表：{}", JSON.toJSONString(list));
    }

    @Test
    public void reject(){
        User auditor = new User("1", "张三", "admin");

        ProcessParam param = new ProcessParam();
        param.setType("加班");
        param.setAuditors(Arrays.asList(
                new CandidateParam(auditor.getId(), auditor.getName(), CandidateType.USER)));
        param.setFiles(Collections.singletonList(new BaseBean("123", "123.png")));
        param.setParams(Collections.singletonMap("时长", 2));
        param.setProcessId("standardRequest");

        processService.start(user, param);

        Process process = processService.waitAuditList(auditor, null).get(0);
        log.info("待审批：{}", JSON.toJSONString(process));

//        if(processService.isAssigneeOrCandidate(auditor, process.getTaskId())){
//        }

        processService.beforeAgreeOrReject(process.getTaskId(), auditor, OperateType.REJECT,
                new ReasonParam("我反对这门亲事",null));
        taskService.complete(process.getTaskId(), Collections.singletonMap(FlowUtil.APPROVED_KEY, false));

        List<Process> list = processService.waitAuditList(auditor, null);
        log.info("待审批列表：{}", JSON.toJSONString(list));
        Assert.assertTrue(list.stream().noneMatch(p -> p.getNo().equals(process.getNo())));

        list = processService.auditList(user, null, null);
        log.info("已审批列表：{}", JSON.toJSONString(list));
    }

    @Test
    public void waitAuditList() {
        List<Process> list = processService.waitAuditList(user, null);
        log.info("待审批列表：{}", JSON.toJSONString(list));
    }

    @Test
    public void auditList() {
        List<Process> list = processService.auditList(user, null, null);
        log.info("已审批列表：{}", JSON.toJSONString(list));
    }

    @Test
    public void myAuditList() {
        List<Process> list = processService.myAuditList(user, null, AuditStatus.WAIT_AUDIT);
        log.info("我申请的未审批列表：{}", JSON.toJSONString(list));
    }

    @Test
    public void cancel() {
        User auditor = new User("1", "张三", "admin");

        ProcessParam param = new ProcessParam();
        param.setType("加班");
        param.setAuditors(Arrays.asList(
                new CandidateParam(auditor.getId(), auditor.getName(), CandidateType.USER)));
        param.setFiles(Collections.singletonList(new BaseBean("123", "123.png")));
        param.setParams(Collections.singletonMap("时长", 2));
        param.setProcessId("standardRequest");

        processService.start(user, param);

        List<Process> list = processService.myAuditList(user, null, AuditStatus.WAIT_AUDIT);
        log.info("我申请的未审批列表：{}", JSON.toJSONString(list));

        processService.cancel(user, list.get(0).getProcessInstanceId());

        list = processService.myAuditList(user, null, null);
        log.info("我申请的列表：{}", JSON.toJSONString(list));
    }
}