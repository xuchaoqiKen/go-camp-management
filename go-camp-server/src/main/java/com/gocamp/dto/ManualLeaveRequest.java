package com.gocamp.dto;

import lombok.Data;

@Data
public class ManualLeaveRequest {
    private Long studentId;
    private String sessionId;
    private String leaveDate;
    private String type;
    private String reason;

    // 手动添加getter/setter解决编译问题
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getLeaveDate() { return leaveDate; }
    public void setLeaveDate(String leaveDate) { this.leaveDate = leaveDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
