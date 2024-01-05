package com.korea.MOVIEBOOK.Movie.Movie;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDaily;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MovieService111 {

    final private MovieRepository movieRepository;
    String dateString = "";
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public void findMovieList(String movieCD, String movieNm, String actorText, String runtime, String genre, String releaseDate,
                              String viewingRating, String director, String nations) {
        Movie movie = this.movieRepository.findByTitleAndNationsAndReleaseDate(movieNm, nations, releaseDate);
        if(movie == null){
            addDetail(movieCD, movieNm, actorText, runtime, genre, releaseDate, viewingRating, director, nations);
        }
    }
    public void addDetail(String movieCD, String movieNm, String actorText, String runtime, String genre,
                          String releaseDate, String viewingRating, String director, String nations) {
        Movie movie = new Movie();
        movie.setMovieCode(movieCD);
        movie.setActor(actorText);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setReleaseDate(releaseDate);
        movie.setViewingRating(viewingRating);
        movie.setDirector(director);
        movie.setTitle(movieNm);
        movie.setNations(nations);
        movie.setMovieCode(movieCD);
        this.movieRepository.save(movie);
    }

    public void addKmdb(String plot, String company, String imageUrl, String title) {
        String plotcontent = plot.replaceAll("!HS", "").replaceAll("!HE", "").replaceAll("\\s+", "");

        Movie movie = this.movieRepository.findByTitle(title);
        movie.setPlot(plotcontent);
        movie.setCompany(company);
        movie.setImageUrl(imageUrl);
        this.movieRepository.save(movie);
    }
    public void add(String title, Long audiAcc) {
        Movie movie = findMovie(title);
        movie.setAudiAcc(audiAcc);
        this.movieRepository.save(movie);
    }

    public Movie findMovie(String title){
        return this.movieRepository.findByTitle(title);
    }
    public Movie findMovieByMovieCode(String movieCode){
        return this.movieRepository.findBymovieCode(movieCode);
    }

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


    public void test(MovieDaily movieDaily, String title){
        Movie movie = this.movieRepository.findByTitle(title);
        movie.setMoviedaily(movieDaily);
        this.movieRepository.save(movie);
    }

    public void setMovieWeeklyID(MovieWeekly movieWeekly, String movieCd){
        Movie movie = this.movieRepository.findBymovieCode(movieCd);
        movie.setMovieweekly(movieWeekly);
        this.movieRepository.save(movie);
    }
}
