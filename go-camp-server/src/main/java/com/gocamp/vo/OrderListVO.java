package com.gocamp.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {
    private Long id;
    private String orderNo;
    private Long studentId;
    private String studentName;
    private Long sessionId;
    private String sessionName;
    private String rankCategory;
    private String accommodationType;
    private String roomMatchStatus;
    private BigDecimal amount;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime createTime;
}