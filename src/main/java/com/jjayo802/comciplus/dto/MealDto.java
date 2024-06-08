package com.jjayo802.comciplus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MealDto {
    List<MealDto2> meal;
    String weekName;
}
