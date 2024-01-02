package com.korea.MOVIEBOOK.Movie.Weekly;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDaily;
import com.korea.MOVIEBOOK.Movie.Movie.Movie;
import com.korea.MOVIEBOOK.Movie.Movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MovieWeeklyService {
    private final MovieWeeklyRepository movieWeeklyRepository;
    private final MovieService movieService;

    String dateString = "";

    public String weeklydate(String date) throws ParseException {

        dateString = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateparse = sdf.parse(dateString);

        // Calendar 객체를 사용하여 주차 계산
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateparse);

        String weekNumber = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));

        return weekNumber;
    }

    public MovieWeekly add(String movieCD, Long rank, String date) throws ParseException{
        String week = weeklydate(date);
        String year = date.substring(0,4);
        Movie movie = this.movieService.findMovieByCD(movieCD);
        MovieWeekly movieWeekly = new MovieWeekly();
        movieWeekly.setMovie(movie);
        movieWeekly.setRank(rank);
        movieWeekly.setYear(year);
        movieWeekly.setWeek(week);
        return this.movieWeeklyRepository.save(movieWeekly);
    }
    public void deleteWeeklyMovie(String weeks) throws ParseException {
        String date = String.valueOf(Integer.parseInt(weeks) -7);
        String week = weeklydate(date);
        String year = date.substring(0,4);
        List<MovieWeekly> movieWeeklyList = this.movieWeeklyRepository.findByYearAndWeek(year,week);
        int i = 0;
        while (i < movieWeeklyList.size()) {
            this.movieWeeklyRepository.delete(movieWeeklyList.get(i));
            i++;
        }
    }

    public List<MovieWeekly> findWeeklyMovie(String weeks) throws ParseException {
        String week = weeklydate(weeks);
        String year = weeks.substring(0,4);
        return this.movieWeeklyRepository.findByYearAndWeek(year,week);
    }
}
