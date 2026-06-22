package com.gocamp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum AccommodationTypeEnum {
    NO_STAY("no_stay", "不住宿"),
    FULL_BOARD("full_board", "全托管"),
    STANDARD_ROOM("standard_room", "陪住标间"),
    KING_ROOM("king_room", "陪住大床");

    @EnumValue
    private final String code;
    private final String desc;

    AccommodationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}