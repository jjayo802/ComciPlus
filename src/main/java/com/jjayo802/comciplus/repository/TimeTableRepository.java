package com.jjayo802.comciplus.repository;

import com.jjayo802.comciplus.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query("SELECT t FROM TimeTable t WHERE t.ymd = :ymd AND t.schoolId = :schoolId AND t.grade = :grade AND t.className = :className")
    List<TimeTable> findTableWithYMD(
            @Param("schoolId") String schoolId,
            @Param("grade") int grade,
            @Param("className") String className,
            @Param("ymd") String ymd);

    @Query("SELECT t FROM TimeTable t WHERE t.ymd = :ymd AND t.period = :period AND t.schoolId = :schoolId AND t.grade = :grade AND t.className = :className")
    List<TimeTable> findTableWithYMDAndPeriod(
            @Param("schoolId") String schoolId,
            @Param("grade") int grade,
            @Param("className") String className,
            @Param("ymd") String ymd,
            @Param("period") int period);

}
