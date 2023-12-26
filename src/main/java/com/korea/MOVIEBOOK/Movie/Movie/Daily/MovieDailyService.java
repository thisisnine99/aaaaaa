package com.korea.MOVIEBOOK.Movie.Movie.Daily;

import com.korea.MOVIEBOOK.Movie.Date.MovieDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import java.util.*;

@RequiredArgsConstructor
@Service
public class MovieDailyService {
    private final MovieDailyRepository movieDailyRepository;
    private final MovieDateService movieDateService;

    LocalDateTime today = LocalDateTime.now().minusDays(7);;
    String today2 = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public void addKmdb(String plot, String company, String imageUrl, String date, String title) {
        MovieDaily movie = this.movieDailyRepository.findByDateAndTitle(date, title);
        String plotcontent = plot.replaceAll("!HS", "").replaceAll("!HE", "").replaceAll("\\s+", "");
        movie.setPlot(plotcontent);
        movie.setCompany(company);
        movie.setImageUrl(imageUrl);
        this.movieDailyRepository.save(movie);

    }

    public void addDeail(String movieNm, String actorText, String runtime, String genre, String releaseDate, String viewingRating, String director, String nations, String date) {
            MovieDaily movie = new MovieDaily();
            movie.setActor(actorText);
            movie.setRuntime(runtime);
            movie.setGenre(genre);
            movie.setReleaseDate(releaseDate);
            movie.setViewingRating(viewingRating);
            movie.setDirector(director);
            movie.setTitle(movieNm);
            movie.setNations(nations);
            movie.setDate(date);
            this.movieDailyRepository.save(movie);
    }

    public void add(Integer gubun, Long rank, String title, Long audiAcc, String date) throws ParseException {
        if(gubun == 0){
            MovieDaily movie = this.movieDailyRepository.findByDateAndTitle(date, title);
            movie.setRank(rank);
            movie.setAudiAcc(audiAcc);
            this.movieDailyRepository.save(movie);
        }
    }
    public void deleteDailyMovie(String date) {
        List<MovieDaily> movieDailyList = this.movieDailyRepository.findBydate(date);
        int i = 0;
        while (i < movieDailyList.size()) {
            this.movieDailyRepository.delete(movieDailyList.get(i));
            i++;
        }
    }
    public List<MovieDaily> findDailyMovie(String date){
        return this.movieDailyRepository.findBydate(date);
    }
}
