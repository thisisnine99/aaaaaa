package com.korea.MOVIEBOOK.Movie.Weekly;

import com.korea.MOVIEBOOK.Movie.Movie.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MovieWeeklyService {
    private final MovieWeeklyRepository movieWeeklyRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final MovieAPI movieAPI;

    String dateString = "";
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public List<MovieDTO> movieWeeklyList(String weeks) throws ParseException {
        String week = weeklydate(weeks);
        String year = weeks.substring(0, 4);
        List<MovieWeekly> movieWeeklyList = this.movieWeeklyRepository.findByYearAndWeek(year, week);
        if (movieWeeklyList.isEmpty()) {
            //  WeeklyAPI실행함수
            List<Map> weeklyBoxOfficeList = movieAPI.getWeekly(weeks); //  주간 박스오피스 리스트를 받는 api를 실행하고 mapList로 받음
            movieService.movieAdd(weeklyBoxOfficeList); //  movie에 디테일을 저장함.
            add(weeklyBoxOfficeList);    //  moviedaily에도 저장함.
        }
        List<MovieDTO> movieWeeklyListDTO = getMovieWeeklyListDTO(movieWeeklyList);
        return movieWeeklyListDTO;
    }

    public List<MovieDTO> getMovieWeeklyListDTO(List<MovieWeekly> weeklyList) {
        List<MovieDTO> movieWeeklyList = new ArrayList<>();
        for (MovieWeekly movieWeekly : weeklyList) {
            MovieDTO movieDTO = MovieDTO.builder()
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
            movieWeeklyList.add(movieDTO);
        }
        return movieWeeklyList;
    }

    public void add(List<Map> boxOfficeList) throws ParseException{
        //  ==========  movieWeekly에 저장하는 함수 ==========
        String week = weeklydate(date);
        String year = date.substring(0,4);
        for (Map<String, Object> boxOffice : boxOfficeList) {
            MovieWeekly movieWeekly = new MovieWeekly();
            movieWeekly.setYear(year);
            movieWeekly.setWeek(week);
            movieWeekly.setRank(Long.parseLong((String) boxOffice.get("rank")));
            movieWeekly.setMovie(movieRepository.findBymovieCode((String) boxOffice.get("movieCd")));
            System.out.println("=========================================실행되는지 확인위클리");
            movieWeeklyRepository.save(movieWeekly);
        }
    }

//    public List<Map> saveWeeklyMovieDataByAPI(List<Map> movieList, String date) throws ParseException {
//        List<Map> finalFailedMovieList = new ArrayList<>();
//        Map rData = null;
//        for (Map movie : movieList) {
//            if(this.movieService.findMovieByCD((String) movie.get("movieCd")) == null) {
//                rData = movieAPI.movieDetail(movie);  // api2 호출
//                if (rData.get("failedMovieList") != null) {
//                    finalFailedMovieList.addAll((List<Map>) rData.get("failedMovieList"));
//                } else {
//                    movieAPI.kmdb((String) movie.get("movieNm"), (String)rData.get("releaseDateAndNationNm"));
//                    movieService.add((String) movie.get("movieNm"), Long.parseLong((String) movie.get("audiAcc")));
//                    MovieWeekly movieWeekly = add((String) movie.get("movieCd"), Long.parseLong((String) movie.get("rank")), date);
//                    movieService.setMovieWeeklyID(movieWeekly,(String) movie.get("movieCd"));
//                }
//            } else {
//                MovieWeekly movieWeekly = add((String) movie.get("movieCd"), Long.parseLong((String) movie.get("rank")), date);
//                this.movieService.setMovieWeeklyID(movieWeekly,(String) movie.get("movieCd"));
//            }
//        }
//        return finalFailedMovieList;
//    }

    public String weeklydate(String date) throws ParseException {
        //  ==========  몇번째 주 인지 변환해주는 함수  ==========
        dateString = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dateparse = sdf.parse(dateString);

        // Calendar 객체를 사용하여 주차 계산
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateparse);
        String weekNumber = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
        return weekNumber;
    }
}
