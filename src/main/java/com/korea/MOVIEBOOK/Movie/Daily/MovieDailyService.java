package com.korea.MOVIEBOOK.Movie.Daily;

import com.korea.MOVIEBOOK.Movie.Movie.*;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeekly;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MovieDailyService {
    private final MovieDailyRepository movieDailyRepository;
    private final MovieRepository movieRepository;
    private final MovieAPI movieAPI;
    private final MovieService movieService;

    String dateString = "";
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public List<MovieDTO> movieDailyList(String date) {
        List<MovieDaily> movieDailyList = this.movieDailyRepository.findByDate(date);
        if (movieDailyList.isEmpty()) {
            List<Map> dailyBoxOfficeList = movieAPI.getDaily(date);   //  일간 박스오피스 리스트를 받는 api를 실행하고 mapList로 받음
            movieService.movieAdd(dailyBoxOfficeList); //  movie에 디테일을 저장함.
            add(dailyBoxOfficeList);    //  moviedaily에도 저장함.
        }
        List<MovieDTO> movieDailyListDTO = getMovieDailyListDTO(movieDailyList);
        return movieDailyListDTO;
    }

    public List<MovieDTO> getMovieDailyListDTO(List<MovieDaily> dailyList) {
        List<MovieDTO> movieDailyListDTO = new ArrayList<>();
        for (MovieDaily movieDaily : dailyList) {
            MovieDTO movieDTO = MovieDTO.builder()
                    .dailyRank(movieDaily.getRank())
                    .date(movieDaily.getDate())
                    .movieCode(movieDaily.getMovie().getMovieCode())
                    .title(movieDaily.getMovie().getTitle())
                    .director(movieDaily.getMovie().getDirector())
                    .actor(movieDaily.getMovie().getActor())
                    .runtime(movieDaily.getMovie().getRuntime())
                    .plot(movieDaily.getMovie().getPlot())
                    .genre(movieDaily.getMovie().getGenre())
                    .releaseDate(movieDaily.getMovie().getReleaseDate())
                    .company(movieDaily.getMovie().getCompany())
                    .nations(movieDaily.getMovie().getNations())
                    .audiAcc(movieDaily.getMovie().getAudiAcc())
                    .viewingRating(movieDaily.getMovie().getViewingRating())
                    .imageUrl(movieDaily.getMovie().getImageUrl())
                    .build();
            movieDailyListDTO.add(movieDTO);
        }
        return movieDailyListDTO;
    }

    public void add(List<Map> dailyBoxOfficeList) {
        for (Map<String, Object> boxOffice : dailyBoxOfficeList) {
            MovieDaily movieDaily = new MovieDaily();
            Movie movie = movieRepository.findBymovieCode((String) boxOffice.get("movieCd"));
            movieDaily.setDate(date);
            movieDaily.setRank(Long.parseLong((String) boxOffice.get("rank")));
            movieDaily.setMovie(movie);
            movieDailyRepository.save(movieDaily);
            System.out.println("=========================================실행되는지 확인데일리");
            System.out.println(movie.getTitle());
        }
    }

    public void test() {
        System.out.println(movieDailyRepository.findAll().get(0).getMovie().getId());
        System.out.println(movieDailyRepository.findAll().get(0).getMovie().getTitle());
    }

//    public List<Map> saveDailyMovieDataByAPI(List<Map> movieList, String date) {
//        List<Map> finalFailedMovieList = new ArrayList<>();
//        Map rData = null;
//
//        for (Map movie : movieList) {
//
//            rData = movieAPI.movieDetail(movie);  // api2 호출
//            if(rData.get("failedMovieList") != null) {
//                finalFailedMovieList.addAll((List<Map>) rData.get("failedMovieList"));
//            }
//            else {
//                movieAPI.kmdb((String) movie.get("movieNm"), (String)rData.get("releaseDateAndNationNm"));
//                this.movieService.add((String) movie.get("movieNm"), Long.parseLong((String) movie.get("audiAcc")));
//                MovieDaily movieDaily = add((String) movie.get("movieCd"), Long.parseLong((String) movie.get("rank")), date);
//                this.movieService.test(movieDaily,(String) movie.get("movieNm"));
//            }
//        }
//        return finalFailedMovieList;
//    }
}
