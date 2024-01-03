package com.korea.MOVIEBOOK.Movie.Weekly;

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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MovieWeeklyAPI {
    private final MovieAPI movieAPI;
    private final MovieWeeklyService movieWeeklyService;

    public List<Map> saveWeeklyMovieDataByAPI(List<Map> movieList, String date) throws ParseException {
        List<Map> finalFailedMovieList = new ArrayList<>();
        Map rData = null;
        int j = 0;
        for (Map movie : movieList) {
            rData = this.movieAPI.movieDetail(movie);  // api2 호출
            if(rData.get("failedMovieList") != null) {
                finalFailedMovieList.addAll((List<Map>) rData.get("failedMovieList"));
            } else {
                this.movieAPI.kmdb((String) movie.get("movieNm"), (String)rData.get("releaseDateAndNationNm"));
                this.movieWeeklyService.add(date, Long.parseLong((String) movie.get("rank")), (String) movie.get("movieNm"), Long.parseLong((String) movie.get("audiAcc")));
            }
            j++;
            System.out.println("=======j의값====" + j);
            System.out.println("=======movie Code 의값 : " + movie.get("movieCd"));
            System.out.println("=======movie Name 의값 : " + movie.get("movieNm"));
            System.out.println("=======movie Rank 의값 : " + movie.get("rank"));
            System.out.println("=======movie Acc 의값 : " + movie.get("audiAcc"));
        }
        System.out.println("failedSize : " + finalFailedMovieList.size());
        return finalFailedMovieList;
    }

    public void movieWeekly(URL uri, String date) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Map boxOfficeResult = (LinkedHashMap) resultMap.getBody().get("boxOfficeResult");
            if (boxOfficeResult == null) {

            }
            List<Map> weeklyBoxOfficeList = (ArrayList<Map>) boxOfficeResult.get("weeklyBoxOfficeList");
            for (Map<String, Object>weeklyBoxOffice : weeklyBoxOfficeList) {
                System.out.println("영화제목============>" + weeklyBoxOffice.get("movieNm"));
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMovieWeekly(String date) {
        String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=";
        String key = "f53a4247c0c7eda74780f0c0b855d761";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + key + "&targetDt=" + date + "&weekGb=0").build();
    }

    public void getMovieDaily() {
        String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=";
//        movieDaily(url);
    }
}
