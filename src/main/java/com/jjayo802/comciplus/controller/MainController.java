package com.jjayo802.comciplus.controller;

import com.jjayo802.comciplus.DateUtils;
import com.jjayo802.comciplus.crawler.ComciCrawler;
import com.jjayo802.comciplus.crawler.MealCrawler;
import com.jjayo802.comciplus.dto.MealDto;
import com.jjayo802.comciplus.dto.MealDto2;
import com.jjayo802.comciplus.dto.TimeTableDto;
import com.jjayo802.comciplus.entity.TimeTable;
import com.jjayo802.comciplus.repository.TimeTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

@Slf4j
@Controller
public class MainController {

    @Autowired
    TimeTableRepository timeTableRepository;

    @GetMapping("/main")
    public String mainForm(Model model){
        LocalDate now = LocalDate.now();
        int dayOfWeek = now.get(ChronoField.DAY_OF_WEEK);
        if(dayOfWeek == 7) dayOfWeek = 0;
        LocalDate start = now.minusDays(dayOfWeek);

        for (int i = 0; i < 5; i++) {
            LocalDate today = start.plusDays(i);
            String ymd = String.format("%d%02d%02d",today.getYear(),today.getMonthValue(),today.getDayOfMonth());
            List<TimeTable> todayTable = timeTableRepository.findTableWithYMD(ymd);
            if(todayTable.isEmpty()) {

            }
        }

        String[][] timeTables = ComciCrawler.getTimeTables();
        String[][] meals = MealCrawler.getMeals();
        if(timeTables == null) return null;

        System.out.println(Arrays.deepToString(timeTables));

        //LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();
        String ymd = String.format("%d%02d%02d",year,month,date);

        for (int i = 0; i < 8; i++) {
            List<TimeTableDto> dtoList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                dtoList.add(new TimeTableDto(i+","+j, timeTables[i][j],"",ymd,i+1));
            }

            model.addAttribute("period" + (i+1),dtoList);
        }

        List<MealDto> mealDtoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MealDto2> mealDto2List = new ArrayList<>();
            for (String s : meals[i]) mealDto2List.add(new MealDto2(s));
            mealDtoList.add(new MealDto(mealDto2List, DateUtils.getWeekDayString(i+1)));
        }
        model.addAttribute("meals",mealDtoList);


        return "main";
    }

    private void saveWeekDataToDB(){
        String[][] timeTables = ComciCrawler.getTimeTables();
        String[][] meals = MealCrawler.getMeals();


    }

    @PostMapping(value = "/memo")
    public String memoForm(@RequestBody String memo) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(memo);
        String idText = (String) object.get("idText");

        int weekDay = Integer.parseInt(idText.split(",")[0]);
        int period = Integer.parseInt(idText.split(",")[1]);



        return "main";
    }
}
