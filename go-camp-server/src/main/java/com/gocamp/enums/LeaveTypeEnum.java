package com.gocamp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum LeaveTypeEnum {
    NO_DEDUCTION("no_deduction", "请假不扣课"),
    MEAL_REFUND_ONLY("meal_refund_only", "仅退餐饮");

    @EnumValue
    private final String code;
    private final String desc;

    LeaveTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}