package com.jjayo802.comciplus.controller;

import com.jjayo802.comciplus.crawler.ComciCrawler;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Controller
public class MainController {

    @GetMapping("/main")
    public String mainForm(Model model){
        String[][] timeTables = ComciCrawler.getTimeTables();
        if(timeTables == null) return null;

        System.out.println(Arrays.deepToString(timeTables));

        for (int i = 0; i < 8; i++) model.addAttribute("period" + (i+1),timeTables[i]);

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
