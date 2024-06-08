package com.jjayo802.comciplus.repository;

import com.jjayo802.comciplus.entity.Meal;
import com.jjayo802.comciplus.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal,Long> {

    @Query("SELECT t FROM Meal t WHERE t.ymd = :ymd")
    List<Meal> findMealWithYMD(@Param("ymd") String ymd);
}
