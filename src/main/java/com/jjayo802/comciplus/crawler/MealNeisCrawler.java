package com.jjayo802.comciplus.crawler;

import com.jjayo802.comciplus.entity.Meal;
import com.jjayo802.comciplus.entity.TimeTable;
import com.jjayo802.comciplus.repository.MealRepository;
import com.jjayo802.comciplus.repository.TimeTableRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class MealNeisCrawler {

    public static void saveMealsToDB(MealRepository repository, String cityId, String schoolId){
        LocalDate now = LocalDate.now();
        String url = getAPIUrl(now, cityId, schoolId);
        System.out.println(url);

        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        Document document;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(document == null) return;

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(document.wholeText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONArray array = (JSONArray) jsonObject.get("mealServiceDietInfo");
        JSONObject row = (JSONObject) array.get(1);
        JSONArray meals = (JSONArray) row.get("row");

        for (Object mealObject : meals) {
            JSONObject mealJson = (JSONObject) mealObject;

            String ymd = (String) mealJson.get("MLSV_YMD");

            int tableYear = Integer.parseInt(ymd.substring(0,4));
            int tableMonth = Integer.parseInt(ymd.substring(4,6));
            int tableDate = Integer.parseInt(ymd.substring(6,8));
            LocalDate localDate = LocalDate.of(tableYear,tableMonth,tableDate);
            int tableDayOfWeek = localDate.get(ChronoField.DAY_OF_WEEK);

            if(tableDayOfWeek == 6) continue;

            String mealString = (String) mealJson.get("DDISH_NM");
            System.out.println(mealString);
            mealString = mealString.replace("\n","<br>");

            String tableYmd = String.format("%d%02d%02d",tableYear,tableMonth,tableDate);

            //TimeTable timeTableEntity = new TimeTable(null,schoolId,grade,className,name,"",tableYmd,period);
            Meal meal = new Meal(null,schoolId,tableYmd,mealString);
            repository.save(meal);

            //result[period-1][tableDayOfWeek-1] = (String) timeTableJson.get("ITRT_CNTNT");
        }
    }

    private static String getAPIUrl(LocalDate now, String cityId, String schoolId) {
        int dayOfWeek = now.get(ChronoField.DAY_OF_WEEK);
        if(dayOfWeek == 7) dayOfWeek = 0;
        LocalDate start = now.minusDays(dayOfWeek);
        LocalDate end = start.plusDays(6);

        int startY = start.getYear();
        int startM = start.getMonthValue();
        int startD = start.getDayOfMonth();

        int endY = end.getYear();
        int endM = end.getMonthValue();
        int endD = end.getDayOfMonth();

        return "https://open.neis.go.kr/hub/mealServiceDietInfo?" +
                        "KEY=f2d3193e82dc40b5876523737ac34ae0&" +
                        "Type=json&" +
                        "ATPT_OFCDC_SC_CODE=" + cityId + "&" +
                        "SD_SCHUL_CODE=" + schoolId + "&" +
                        String.format("MLSV_FROM_YMD=%d%02d%02d&",startY,startM,startD) +
                        String.format("MLSV_TO_YMD=%d%02d%02d",endY,endM,endD);
    }
}
