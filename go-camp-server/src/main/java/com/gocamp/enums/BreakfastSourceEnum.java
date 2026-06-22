package com.gocamp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum BreakfastSourceEnum {
    BOARDING_INCLUDED("boarding_included", "托管包含"),
    ACCOMMODATION_INCLUDED("accommodation_included", "住宿包含"),
    DAY_PURCHASE("day_purchase", "走读加购");

    @EnumValue
    private final String code;
    private final String desc;

    BreakfastSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}