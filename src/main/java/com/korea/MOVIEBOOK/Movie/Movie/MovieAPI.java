package com.korea.MOVIEBOOK.Movie.Movie;

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

import java.util.*;

@Component
@RequiredArgsConstructor
public class MovieAPI {
    public List<Map> getListApi(UriComponents uri) {
        List<Map> boxOfficeList = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Map boxOfficeResult = (LinkedHashMap) resultMap.getBody().get("boxOfficeResult");
            String boxofficeType = (String) boxOfficeResult.get("boxofficeType");

            System.out.println(boxofficeType);

            if (boxofficeType.startsWith("주간")) {
                boxOfficeList = (ArrayList<Map>) boxOfficeResult.get("weeklyBoxOfficeList");
            } else if (boxofficeType.startsWith("일별")) {
                boxOfficeList = (ArrayList<Map>) boxOfficeResult.get("dailyBoxOfficeList");
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boxOfficeList;
    }

    public Map<String, Object> getMapApi(UriComponents uri) {
        Map<String, Object> movieInfo = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            Map movieInfoResult = (LinkedHashMap) resultMap.getBody().get("movieInfoResult");
            movieInfo = (Map<String, Object>) movieInfoResult.get("movieInfo");

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieInfo;
    }

    public List<Map> getDaily(String date) {
        String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?key=";
        String key = "f53a4247c0c7eda74780f0c0b855d761&targetDt=";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + key + date + "&weekGb=0").build();
        return getListApi(uri);
    }
    //  일간박스오피스

    public List<Map> getWeekly(String date) {
        String url = "https://kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchWeeklyBoxOfficeList.json?key=";
        String key = "f53a4247c0c7eda74780f0c0b855d761&targetDt=";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + key + date + "&weekGb=0").build();
        return getListApi(uri);
    }
    //  주간박스오피스

    public Map<String, Object> getMovieDetail(String movieCD) {
        String url = "https://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json?key=";
        String key = "f53a4247c0c7eda74780f0c0b855d761&movieCd=";
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + key + movieCD).build();
        return getMapApi(uri);
    }
}
