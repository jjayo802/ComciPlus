package com.jjayo802.comciplus.controller;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;

@Slf4j
@Controller
public class MainController {

    @GetMapping("/main")
    public String mainForm(Model model){
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int month = now.getMonthValue();
        int date = now.getDayOfMonth();
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

        Element todayElement = null;
        Element weekElement = null;
        for(Element element : tableValues){
            Elements values = element.getElementsByTag("td");
            for(Element value : values){
                String dateValue = value.ownText();
                System.out.println(dateValue);
                if(dateValue.equals(dateString)) {
                    todayElement = value.child(0).child(0);
                    weekElement = element;
                }
            }
        }

        String meal = "";
        if(todayElement != null) meal = todayElement.wholeText();

        System.out.println(meal);

        assert weekElement != null;
        for (int i = 0; i < 5; i++) {
            Element dateElement = weekElement.child(i + 1);
            String meal =
        }

        return "main";
    }

    @PostMapping("/memo")
    public String memoForm(){
        return "memo";
    }

    @PostMapping("/memo/update")
    public String memoUpdate(){
        return "redirect:/main";
    }
}
