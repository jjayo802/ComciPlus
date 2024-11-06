package com.jjayo802.comciplus.controller;

import com.jjayo802.comciplus.crawler.ComciCrawler;
import com.jjayo802.comciplus.dto.IdNameDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
public class SelectController {

    @GetMapping("/")
    public String select(Model model){


        return "select";
    }

    @GetMapping("/api/schools")
    @ResponseBody
    public List<IdNameDto> returnSchools(@RequestParam Optional<String> districtId, @RequestParam Optional<String> schoolName){
        return ComciCrawler.getSchoolsOfCity(districtId.orElse(""), schoolName.orElse(""));
    }

    @GetMapping("/api/classes")
    @ResponseBody
    public List<IdNameDto> returnClasses(@RequestParam String districtId, @RequestParam String schoolId, @RequestParam int grade){
        return ComciCrawler.getClassesOfGrade(districtId, schoolId, grade);
    }
}
