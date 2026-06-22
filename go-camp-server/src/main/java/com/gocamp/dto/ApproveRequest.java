package com.gocamp.dto;

import lombok.Data;

@Data
public class ApproveRequest {
    private Long id;
    private Boolean approved;
    private String reason;

    // 手动添加getter/setter解决编译问题
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean approved) { this.approved = approved; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
