package com.jjayo802.comciplus.repository;

import com.jjayo802.comciplus.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query("SELECT t FROM TimeTable t WHERE t.ymd = :ymd")
    List<TimeTable> findTableWithYMD(@Param("ymd") String ymd);

}
