package com.korea.MOVIEBOOK.Movie.Daily;

import com.korea.MOVIEBOOK.Movie.Movie.Movie;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
                MovieDaily movieDaily = this.movieDailyService.add((String) movie.get("movieCd"), Long.parseLong((String) movie.get("rank")), date);
                this.movieService.test(movieDaily,(String) movie.get("movieNm"));
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


    public List<Map> movieDaily(String date) {
        Map rData = null;
        HashMap<String, Object> result = new HashMap<String, Object>();
        String key = "f53a4247c0c7eda74780f0c0b855d761";
        List<Map> finalFailedMovieList = new ArrayList<>();
        try {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json";

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + "?" + "key=" + key + "&targetDt=" + date).build();

            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
            result.put("header", resultMap.getHeaders()); //헤더 정보 확인
            result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

            LinkedHashMap lm = (LinkedHashMap) resultMap.getBody().get("boxOfficeResult");
            ArrayList<Map> dboxoffList = (ArrayList<Map>) lm.get("dailyBoxOfficeList");

            if (dboxoffList.size() < 10) {
                movieDaily(date); // api1 호출
            }

            finalFailedMovieList = saveDailyMovieDataByAPI(dboxoffList);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body", "excpetion오류");
            System.out.println(e.toString());
        }
        return finalFailedMovieList;
    }

}
