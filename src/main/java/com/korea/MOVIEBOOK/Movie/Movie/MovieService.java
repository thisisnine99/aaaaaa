package com.korea.MOVIEBOOK.Movie.Movie;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDaily;
import com.korea.MOVIEBOOK.Movie.Daily.MovieDailyAPI;
import com.korea.MOVIEBOOK.Movie.Daily.MovieDailyRepository;
import com.korea.MOVIEBOOK.Movie.MovieDTO;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeekly;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeeklyRepository;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeeklyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    final private MovieRepository movieRepository;
    final private MovieDailyRepository movieDailyRepository;
    final private MovieWeeklyRepository movieWeeklyRepository;
    String dateString = "";
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public void findMovieList(String movieCD,String movieNm, String actorText, String runtime, String genre, String releaseDate, String viewingRating, String director, String nations){
        Movie movie = this.movieRepository.findByTitleAndNationsAndReleaseDate(movieNm, nations, releaseDate);
        if(movie == null){
            addDetail(movieCD, movieNm, actorText, runtime, genre, releaseDate, viewingRating, director, nations);
        }
    }
    public void addDetail(String movieCD, String movieNm, String actorText, String runtime, String genre, String releaseDate, String viewingRating, String director, String nations) {
        Movie movie = new Movie();
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
    public Movie findMovieByCD(String movieCode){
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

    public  List<MovieDTO> listOfMovieDailyDTO()
    {
        List<MovieDaily> movieDailyList = this.movieDailyRepository.findBydate(date);
        return setMovieDTO(movieDailyList);
    }
    public List<MovieDTO> setMovieDTO(List<MovieDaily> movieDaily){
        List<MovieDTO> movieDTOS = new ArrayList<>();
        for(MovieDaily movieDaily1 : movieDaily)
        {
            MovieDTO movieDTO= MovieDTO.builder()
                    .movieCode(movieDaily1.getMovie().getMovieCode())
                    .dailyRank(movieDaily1.getRank())
                    .date(movieDaily1.getDate())
                    .title(movieDaily1.getMovie().getTitle())
                    .director(movieDaily1.getMovie().getDirector())
                    .actor(movieDaily1.getMovie().getActor())
                    .runtime(movieDaily1.getMovie().getRuntime())
                    .plot(movieDaily1.getMovie().getPlot())
                    .genre(movieDaily1.getMovie().getGenre())
                    .releaseDate(movieDaily1.getMovie().getReleaseDate())
                    .company(movieDaily1.getMovie().getCompany())
                    .nations(movieDaily1.getMovie().getNations())
                    .audiAcc(movieDaily1.getMovie().getAudiAcc())
                    .viewingRating(movieDaily1.getMovie().getViewingRating())
                    .imageUrl(movieDaily1.getMovie().getImageUrl())
                    .build();
            movieDTOS.add(movieDTO);
        }
        return movieDTOS;
    }

    public  List<MovieDTO> listOfMovieWeeklyDTO(String weeks) throws ParseException {
        String week = weeklydate(weeks);
        String year = weeks.substring(0,4);
        List<MovieWeekly> movieWeeklyList = this.movieWeeklyRepository.findByYearAndWeek(year,week);
        return setMovieWeeklyDTO(movieWeeklyList);
    }
    public List<MovieDTO> setMovieWeeklyDTO(List<MovieWeekly> movieWeeklies){
        List<MovieDTO> movieDTOS2 = new ArrayList<>();
        for(MovieWeekly movieWeekly : movieWeeklies)
        {
            MovieDTO movieDTO= MovieDTO.builder()
                    .movieCode(movieWeekly.getMovie().getMovieCode())
                    .weeklyRank(movieWeekly.getRank())
                    .year(movieWeekly.getYear())
                    .week(movieWeekly.getWeek())
                    .title(movieWeekly.getMovie().getTitle())
                    .director(movieWeekly.getMovie().getDirector())
                    .actor(movieWeekly.getMovie().getActor())
                    .runtime(movieWeekly.getMovie().getRuntime())
                    .plot(movieWeekly.getMovie().getPlot())
                    .genre(movieWeekly.getMovie().getGenre())
                    .releaseDate(movieWeekly.getMovie().getReleaseDate())
                    .company(movieWeekly.getMovie().getCompany())
                    .nations(movieWeekly.getMovie().getNations())
                    .audiAcc(movieWeekly.getMovie().getAudiAcc())
                    .viewingRating(movieWeekly.getMovie().getViewingRating())
                    .imageUrl(movieWeekly.getMovie().getImageUrl())
                    .build();
            movieDTOS2.add(movieDTO);
        }
        return movieDTOS2;
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
