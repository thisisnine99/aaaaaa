package com.korea.MOVIEBOOK.Movie.Daily;

import com.korea.MOVIEBOOK.Movie.Movie.MovieService;
import com.korea.MOVIEBOOK.Movie.MovieAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MovieDailyAPI {

    private final MovieDailyService movieDailyService;
    private final MovieAPI movieAPI;
    private final MovieService movieService;
    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    Integer gubun = 0;  // MovieDaily, MovieWeekly 구분 변수

    // api n개 처리해주는 놈
    public List<Map> saveDailyMovieDataByAPI(List<Map> movieList) {
        List<Map> finalFailedMovieList = new ArrayList<>();
        Map rData = null;
        int i = 0;

        for (Map movie : movieList) {

            rData = this.movieAPI.movieDetail(movie);  // api2 호출
            if(rData.get("failedMovieList") != null) {
                finalFailedMovieList.addAll((List<Map>) rData.get("failedMovieList"));
            }
            else {
                this.movieAPI.kmdb((String) movie.get("movieNm"), (String)rData.get("releaseDateAndNationNm"));
                this.movieService.add((String) movie.get("movieNm"), Long.parseLong((String) movie.get("audiAcc")));
                this.movieDailyService.add((String) movie.get("movieNm"), Long.parseLong((String) movie.get("rank")), date);
            }
            i++;
            System.out.println("=======i의값====" + i);
            System.out.println("=======movie Code 의값 : " + movie.get("movieCd"));
            System.out.println("=======movie Name 의값 : " + movie.get("movieNm"));
            System.out.println("=======movie Rank 의값 : " + movie.get("rank"));
            System.out.println("=======movie Acc 의값 : " + movie.get("audiAcc"));
        }

        System.out.println("failedSize : " + finalFailedMovieList.size());
        return finalFailedMovieList;
    }


    public void movieDaily(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);

            String key = "f53a4247c0c7eda74780f0c0b855d761";
            URL uri = new URL(url + key + "&targetDt=" + "20231228");

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Map boxOfficeResult = (LinkedHashMap) resultMap.getBody().get("boxOfficeResult");
            if (boxOfficeResult == null) {

            }
            List<Map> dailyBoxOfficeList = (ArrayList<Map>) boxOfficeResult.get("dailyBoxOfficeList");
            for (Map<String, Object>dailyBoxOffice : dailyBoxOfficeList) {
                System.out.println("영화제목============>" + dailyBoxOffice.get("movieNm"));
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMovieDaily() {
        String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=";
        movieDaily(url);
    }
}
