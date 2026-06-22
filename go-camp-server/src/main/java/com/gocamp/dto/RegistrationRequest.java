package com.gocamp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RegistrationRequest {

    private Long parentUserId;
    private List<StudentRegistration> students;
    private String paymentMethod;

    @Data
    public static class StudentRegistration {
        private String name;
        private Integer gender;
        private Integer age;
        private String rank;
        private String city;
        private String companion;
        private List<SessionSelection> sessions;
    }

    @Data
    public static class SessionSelection {
        private Long sessionId;
        private List<DailyScheduleItem> schedules;
    }

    @Data
    public static class DailyScheduleItem {
        private String date;
        private Boolean hasClass;
        private Boolean hasBreakfast;
        private Boolean hasLunch;
        private Boolean hasDinner;
        private String accommodationType;
        private String breakfastSource;
    }
}