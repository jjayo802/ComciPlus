package com.jjayo802.comciplus.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TimeTableDto {
    String idText;
    String name;
    String memo;
    String ymd;
    int period;
}
