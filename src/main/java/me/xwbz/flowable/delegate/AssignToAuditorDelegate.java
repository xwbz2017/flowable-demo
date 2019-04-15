package me.xwbz.flowable.delegate;

import me.xwbz.flowable.common.util.FlowUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


/**
 * delegate - 分配审批人
 * <br>
 * 整体流程：
 * 1.创建申请
 * 2.分配给审批人（需要审批人列表，当前审批人）
 * -> 有下一个审批人 -> 3
 * -> 无 -> 4
 * 3.审批人审批
 * -> 同意 -> 2
 * -> 拒绝 -> 5
 * 4.存储数据
 * 5.结束
 */
public class AssignToAuditorDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        FlowUtil.toNextCandidate(execution);
    }
}
