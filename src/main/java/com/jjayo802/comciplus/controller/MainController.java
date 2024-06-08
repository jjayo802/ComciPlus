package com.jjayo802.comciplus.controller;

import com.jjayo802.comciplus.DateUtils;
import com.jjayo802.comciplus.crawler.ComciCrawler;
import com.jjayo802.comciplus.crawler.MealCrawler;
import com.jjayo802.comciplus.dto.MealDto;
import com.jjayo802.comciplus.dto.MealDto2;
import com.jjayo802.comciplus.dto.TimeTableDto;
import com.jjayo802.comciplus.entity.Meal;
import com.jjayo802.comciplus.entity.TimeTable;
import com.jjayo802.comciplus.repository.MealRepository;
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
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

@Slf4j
@Controller
public class MainController {

    @Autowired
    TimeTableRepository timeTableRepository;

    @Autowired
    MealRepository mealRepository;

    @GetMapping("/main")
    public String mainForm(Model model){
        LocalDate now = LocalDate.now();
        int dayOfWeek = now.get(ChronoField.DAY_OF_WEEK);
        if(dayOfWeek == 7) dayOfWeek = 0;
        LocalDate start = now.minusDays(dayOfWeek).plusDays(1);

        TimeTable[][] timeTables = new TimeTable[8][5];
        Meal[] meals = new Meal[5];

        for (int i = 0; i < 5; i++) {
            LocalDate today = start.plusDays(i);
            String ymd = String.format("%d%02d%02d",today.getYear(),today.getMonthValue(),today.getDayOfMonth());
            List<TimeTable> todayTable = timeTableRepository.findTableWithYMD(ymd);
            List<Meal> todayMeal = mealRepository.findMealWithYMD(ymd);

            if(todayTable.isEmpty() || todayMeal.isEmpty()) {
                ComciCrawler.saveTimeTablesToDB(timeTableRepository);
                MealCrawler.saveMealsToDB(mealRepository,start);
                i--;
                continue;
            }

            int weekDay = today.getDayOfWeek().getValue();

            for (TimeTable timeTable : todayTable) {
                int period = timeTable.getPeriod();
                timeTables[period - 1][weekDay - 1] = timeTable;
            }

            Meal meal = todayMeal.get(0);
            meals[weekDay-1] = meal;
        }

        for (int i = 0; i < 8; i++) {
            List<TimeTableDto> dtoList = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                String content = "";
                String memo = "";
                if(timeTables[i][j] != null) {
                    content = timeTables[i][j].getName();
                    memo = timeTables[i][j].getMemo();
                }
                LocalDate today = start.plusDays(j);
                String ymd = String.format("%d%02d%02d",today.getYear(),today.getMonthValue(),today.getDayOfMonth());
                dtoList.add(new TimeTableDto(ymd+","+(i+1), content,memo,ymd,i+1));
            }

            model.addAttribute("period" + (i+1),dtoList);
        }

        List<MealDto> mealDtoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<MealDto2> mealDto2List = new ArrayList<>();
            String mealHtml = meals[i].getMeal();
            String[] meal = mealHtml.split("<br>");

            for (String s : meal) {
                if(s.isBlank()) {
                    meal = Arrays.copyOfRange(meal,0,meal.length - 1);
                    break;
                }
            }
            for (String s : meal) mealDto2List.add(new MealDto2(s));
            mealDtoList.add(new MealDto(mealDto2List, DateUtils.getWeekDayString(i+1)));
        }
        model.addAttribute("meals",mealDtoList);


        return "main";
    }

    private void saveWeekDataToDB(){
        //String[][] timeTables = ComciCrawler.getTimeTables();
        //String[][] meals = MealCrawler.getMeals();


    }

    @PostMapping(value = "/memo")
    public RedirectView memoForm(@RequestBody String data) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(data);
        String idText = (String) object.get("idText");
        String memo = (String) object.get("memoText");

        String ymd = idText.split(",")[0];
        int period = Integer.parseInt(idText.split(",")[1]);

        System.out.println(idText);

        List<TimeTable> tables = timeTableRepository.findTableWithYMDAndPeriod(ymd,period);
        TimeTable table = tables.get(0);
        table.setMemo(memo);
        timeTableRepository.save(table);

        return new RedirectView("/main");
    }
}
