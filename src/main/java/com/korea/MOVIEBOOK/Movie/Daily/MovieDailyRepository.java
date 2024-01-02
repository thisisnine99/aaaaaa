package com.korea.MOVIEBOOK.Movie.Daily;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieDailyRepository extends JpaRepository<MovieDaily,Long> {
    List<MovieDaily> findBydate(String date);
    Optional<MovieDaily> findByDateAndRank(String date, Long rank);

}
