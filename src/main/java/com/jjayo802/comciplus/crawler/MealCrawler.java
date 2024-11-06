package com.jjayo802.comciplus.crawler;

import com.jjayo802.comciplus.entity.Meal;
import com.jjayo802.comciplus.repository.MealRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class MealCrawler {

    public static void saveMealsToDB(MealRepository repository, LocalDate startDate){
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();
        Element weekElement = getWeekTableElement(year,month,date);

        assert weekElement != null;

        Element lastWeekElement = null;
        Element nextWeekElement = null;

        String[][] meals = new String[5][];
        boolean isEmptyDateFirst = false;
        for (int i = 0; i < 5; i++) {
            Element dateElement = weekElement.child(i + 1);
            String dateElementDate = dateElement.ownText();

            if(dateElementDate.isBlank() && i == 0) isEmptyDateFirst = true;

            if(dateElementDate.isBlank() && isEmptyDateFirst){
                if(lastWeekElement == null){
                    int lastMonth = month - 1;
                    int lastYear = year;
                    if(lastMonth == 0){
                        lastMonth = 12;
                        lastYear -= 1;
                    }

                    LocalDate last = LocalDate.of(lastYear,lastMonth,1);
                    int dateCount = last.lengthOfMonth();
                    int lastDate = dateCount - 1;

                    lastWeekElement = getWeekTableElement(lastYear,lastMonth,lastDate);
                }
                dateElement = lastWeekElement.child(i+1);
            }
            if(dateElementDate.isBlank() && !isEmptyDateFirst){
                if(nextWeekElement == null){
                    int nextMonth = month + 1;
                    int nextYear = year;
                    if(nextMonth == 13){
                        nextMonth = 1;
                        nextYear += 1;
                    }

                    nextWeekElement = getWeekTableElement(nextYear,nextMonth,1);
                }
                dateElement = nextWeekElement.child(i+1);
            }

            String mealHtml = dateElement.child(0).child(0).html();
            String[] meal = mealHtml.split("<br>");

            for (String s : meal) {
                if(s.isBlank()) {
                    meal = Arrays.copyOfRange(meal,0,meal.length - 1);
                    break;
                }
            }

            LocalDate today = startDate.plusDays(i);
            String ymd = String.format("%d%02d%02d",today.getYear(),today.getMonthValue(),today.getDayOfMonth());

            //Meal mealEntity = new Meal(null,ymd,mealHtml);
            //repository.save(mealEntity);

            meals[i] = meal;
        }

        //return meals;
    }

    private static Element getWeekTableElement(int year, int month, int date){
        String dateString = String.valueOf(date);

        String url = String.format("http://inam.icehs.kr/foodlist.do?year=%d&month=%02d&m=020411&s=inam"
                ,year,month);

        Connection connection = Jsoup.connect(url);
        Document document;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements elements = document.getElementsByClass("tb_calendar");
        Elements tableValues = elements.get(0).getElementsByTag("tr");

        Element weekElement = null;
        for(Element element : tableValues){
            Elements values = element.getElementsByTag("td");
            for(Element value : values){
                String dateValue = value.ownText();
                if(dateValue.equals(dateString)) weekElement = element;

            }
        }

        return weekElement;
    }
}
