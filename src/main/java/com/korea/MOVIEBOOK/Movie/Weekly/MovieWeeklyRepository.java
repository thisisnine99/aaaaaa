package com.korea.MOVIEBOOK.Movie.Weekly;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieWeeklyRepository extends JpaRepository<MovieWeekly,Long> {

    List<MovieWeekly> findByYearAndWeek(String year, String week);
}
