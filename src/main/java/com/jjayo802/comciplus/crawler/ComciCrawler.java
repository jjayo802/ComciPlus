package com.jjayo802.comciplus.crawler;

import com.jjayo802.comciplus.dto.IdNameDto;
import com.jjayo802.comciplus.entity.TimeTable;
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
import java.util.ArrayList;
import java.util.List;

public class ComciCrawler {

    static String key = "f2d3193e82dc40b5876523737ac34ae0";

    public static void saveTimeTablesToDB(TimeTableRepository repository, String cityId, String schoolId, int grade, String className){
        LocalDate now = LocalDate.now();
        String url = getAPIUrl(now, cityId, schoolId, grade, className);
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

        JSONArray array = (JSONArray) jsonObject.get("hisTimetable");
        JSONObject row = (JSONObject) array.get(1);
        JSONArray timeTables = (JSONArray) row.get("row");

        //String[][] result = new String[8][5];

        for (Object timeTable : timeTables) {
            JSONObject timeTableJson = (JSONObject) timeTable;

            int period = Integer.parseInt((String)timeTableJson.get("PERIO"));
            String ymd = (String) timeTableJson.get("ALL_TI_YMD");

            int tableYear = Integer.parseInt(ymd.substring(0,4));
            int tableMonth = Integer.parseInt(ymd.substring(4,6));
            int tableDate = Integer.parseInt(ymd.substring(6,8));
            LocalDate localDate = LocalDate.of(tableYear,tableMonth,tableDate);
            int tableDayOfWeek = localDate.get(ChronoField.DAY_OF_WEEK);

            if(tableDayOfWeek == 6) continue;

            String name = (String) timeTableJson.get("ITRT_CNTNT");
            String tableYmd = String.format("%d%02d%02d",tableYear,tableMonth,tableDate);

            TimeTable timeTableEntity = new TimeTable(null,schoolId,grade,className,name,"",tableYmd,period);
            repository.save(timeTableEntity);

            //result[period-1][tableDayOfWeek-1] = (String) timeTableJson.get("ITRT_CNTNT");
        }
    }

    private static String getAPIUrl(LocalDate now, String cityId, String schoolId, int grade, String classNum) {
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

        String url =
                "https://open.neis.go.kr/hub/hisTimetable?" +
                        "KEY=f2d3193e82dc40b5876523737ac34ae0&" +
                        "Type=json&" +
                        //"ATPT_OFCDC_SC_CODE=E10&" +
                        //"SD_SCHUL_CODE=7310059&" +
                        //"GRADE=2&" +
                        //"CLRM_NM=2&" +
                        "ATPT_OFCDC_SC_CODE=" + cityId + "&" +
                        "SD_SCHUL_CODE=" + schoolId + "&" +
                        "GRADE=" + grade + "&" +
                        "CLRM_NM=" + classNum + "&" +
                        String.format("TI_FROM_YMD=%d%02d%02d&",startY,startM,startD) +
                        String.format("TI_TO_YMD=%d%02d%02d",endY,endM,endD);
        return url;
    }

    public static List<IdNameDto> getSchoolsOfCity(String cityId, String name){
        String url =
                "https://open.neis.go.kr/hub/schoolInfo?" +
                        "KEY=" + key + "&" +
                        "Type=json&" +
                        "SCHUL_KND_SC_NM=고등학교&" +
                        "ATPT_OFCDC_SC_CODE=" + cityId + "&" +
                        "SCHUL_NM=" + name;

        JSONObject object = getJsonObjectFromURL(url);
        if(object == null) return new ArrayList<>();

        JSONArray schoolInfo = (JSONArray) object.get("schoolInfo");
        JSONObject row = (JSONObject) schoolInfo.get(1);
        JSONArray schools = (JSONArray) row.get("row");

        List<IdNameDto> result = new ArrayList<>();

        for (Object school : schools) {
            JSONObject schoolObject = (JSONObject) school;
            String schoolId = (String) schoolObject.get("SD_SCHUL_CODE");
            String schoolName = (String) schoolObject.get("SCHUL_NM");
            result.add(new IdNameDto(schoolId, schoolName));
        }

        return result;
    }

    public static List<IdNameDto> getClassesOfGrade(String cityId, String schoolId, int grade){
        LocalDate date = LocalDate.now();
        int year = date.getYear();

        String url =
                "https://open.neis.go.kr/hub/classInfo?" +
                        "KEY=" + key + "&" +
                        "Type=json&" +
                        "ATPT_OFCDC_SC_CODE=" + cityId + "&" +
                        "SD_SCHUL_CODE=" + schoolId + "&" +
                        "GRADE=" + grade + "&" +
                        "AY=" + year;

        JSONObject object = getJsonObjectFromURL(url);
        if(object == null) return new ArrayList<>();

        JSONArray schoolInfo = (JSONArray) object.get("classInfo");
        JSONObject row = (JSONObject) schoolInfo.get(1);
        JSONArray schools = (JSONArray) row.get("row");

        List<IdNameDto> result = new ArrayList<>();

        for (Object school : schools) {
            JSONObject schoolObject = (JSONObject) school;
            String className = (String) schoolObject.get("CLASS_NM");
            result.add(new IdNameDto(className, className));
        }

        return result;
    }

    static JSONObject getJsonObjectFromURL(String url){
        Connection connection = Jsoup.connect(url).ignoreContentType(true);
        Document document;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(document == null) return null;

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(document.wholeText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }
}
