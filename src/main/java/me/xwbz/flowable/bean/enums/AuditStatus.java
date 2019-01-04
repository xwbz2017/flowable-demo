package me.xwbz.flowable.bean.enums;

public enum AuditStatus {
    /**
     * 待审批
     */
    WAIT_AUDIT,
    /**
     * 已同意申请
     */
    AGREE_AUDIT,
    /**
     * 已拒绝申请
     */
    REJECT_AUDIT,
    /**
     * 已取消
     */
    CANCEL
}