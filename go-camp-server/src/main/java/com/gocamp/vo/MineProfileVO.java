package com.gocamp.vo;

import lombok.Data;
import java.util.List;

@Data
public class MineProfileVO {
    private ProfileInfo profile;
    private List<StudentInfo> students;
    private List<LeaveRecordInfo> leaveRecords;

    @Data
    public static class ProfileInfo {
        private String name;
        private String phone;
        private String city;
    }

    @Data
    public static class StudentInfo {
        private Long id;
        private String name;
        private String rank;
        private String statusText;
        private String statusClass;
        private String sessions;
        private String className;
        private String accommodation;
        private String roomInfo;
        private String breakfastSource;
    }

    @Data
    public static class LeaveRecordInfo {
        private Long id;
        private String studentName;
        private String date;
        private String typeText;
        private String statusText;
        private String statusClass;
    }
}