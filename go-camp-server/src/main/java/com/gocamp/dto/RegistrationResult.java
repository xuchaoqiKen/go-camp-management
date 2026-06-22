package com.gocamp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RegistrationResult {

    private String paymentNo;
    private BigDecimal totalAmount;
    private List<OrderSummary> orders;

    @Data
    public static class OrderSummary {
        private String orderNo;
        private Long studentId;
        private String studentName;
        private Long sessionId;
        private String sessionName;
        private BigDecimal orderAmount;
        private String accommodationType;
    }
}