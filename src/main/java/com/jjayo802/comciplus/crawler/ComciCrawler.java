package com.jjayo802.comciplus.crawler;

import com.fasterxml.jackson.core.JsonParser;
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

public class ComciCrawler {
    public static String[][] getTimeTables(){
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();

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
                        "ATPT_OFCDC_SC_CODE=E10&" +
                        "SD_SCHUL_CODE=7310059&" +
                        "GRADE=2&" +
                        "CLRM_NM=2&" +
                        String.format("TI_FROM_YMD=%d%02d%02d&",startY,startM,startD) +
                        String.format("TI_TO_YMD=%d%02d%02d",endY,endM,endD);

        System.out.println(url);

        Connection connection = Jsoup.connect(url);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(document == null) return null;

        System.out.println(document.wholeText());

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

        String[][] result = new String[7][5];

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

            result[period-1][tableDayOfWeek-1] = (String) timeTableJson.get("ITRT_CNTNT");
        }

        return result;
    }


}
