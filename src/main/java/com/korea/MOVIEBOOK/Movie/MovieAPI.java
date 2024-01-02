package com.korea.MOVIEBOOK.Movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.korea.MOVIEBOOK.Movie.Daily.MovieDailyService;
import com.korea.MOVIEBOOK.Movie.Movie.MovieService;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeeklyService;
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
public class MovieAPI {

    private final MovieDailyService movieDailyService;
    private final MovieWeeklyService movieWeeklyService;
    private final MovieService movieService;

    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDateTime today = LocalDateTime.now();
    String today2 = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public void kmdb(String movieNm, String releaseDts) {
        String releaseDt = releaseDts.substring(0, 8); // genre
        String nation = releaseDts.substring(8);
        if ("한국".equals(nation)) {
            nation = "대한민국";
        }

        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";
        String key = "390XVFE3E50ON75912O7";

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            header.add("Content-type", "application/json");
            header.add("Accept-Charset", "UTF-8"); //맞는 타입에 따라
            HttpEntity<?> entity = new HttpEntity<>(header);

            String url = "http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp?collection=kmdb_new2&ServiceKey=";

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + key + "&detail=Y&title=" + movieNm + "&releaseDts=" + releaseDt + "&nation=" + nation).build();

            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<String> resultString = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);

            String[] responseBits = resultString.toString().split("\r\n");
            String jsonStr = responseBits[1];

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resultMap = mapper.readValue(jsonStr, Map.class);

            ArrayList<Map> dataList = (ArrayList<Map>) resultMap.get("Data");

            Map<String, Object> resultMap2 = dataList.get(0);
            ArrayList<Map> ResultList = (ArrayList<Map>) resultMap2.get("Result");

            Map<String, Object> plotsList = (Map<String, Object>) ResultList.get(0).get("plots");
            ArrayList<Map> plotList = (ArrayList<Map>) plotsList.get("plot");
            String plotText = (String) plotList.get(0).get("plotText");

            String post = (String) ResultList.get(0).get("posters");
            String[] dataArray = post.split("\\|");
            String poster = dataArray[0].trim();

            String company = (String) ResultList.get(0).get("company");

            this.movieService.addKmdb(plotText, company, poster, movieNm);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body", "excpetion오류");
            System.out.println(e.toString());
        }
    }

    public Map<String, Object> movieDetail(Map movie) {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String key = "f53a4247c0c7eda74780f0c0b855d761";
        Map rData = new HashMap<>();

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            String url = "https://www.kobis.or.kr/kobisopenapi/webservice/rest/movie/searchMovieInfo.json";

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + "?" + "key=" + key + "&movieCd=" + movie.get("movieCd")).build();

            //이 한줄의 코드로 API를 호출해 MAP타입으로 전달 받는다.
            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            result.put("statusCode", resultMap.getStatusCodeValue()); //http status code를 확인
            result.put("header", resultMap.getHeaders()); //헤더 정보 확인
            result.put("body", resultMap.getBody()); //실제 데이터 정보 확인

            LinkedHashMap movieDetail = (LinkedHashMap) resultMap.getBody().get("movieInfoResult");

            List<Map> failedMovieList = new ArrayList<>();

            if ((LinkedHashMap) resultMap.getBody().get("movieInfoResult") == null || (Map<String, Object>) movieDetail.get("movieInfo") == null) {
                failedMovieList.add(movie);
                rData.put("failedMovieList", failedMovieList);
            }

            Map<String, Object> detailList = (Map<String, Object>) movieDetail.get("movieInfo");

            ArrayList<Map> actorsList = (ArrayList<Map>) detailList.get("actors");
            String actors = "";
            for (int i = 0; i < actorsList.size(); i++) {
                String actorText = (String) actorsList.get(i).get("peopleNm");
                String cast = (String) actorsList.get(i).get("cast");
                actors += actorText + "(" + cast + ")";

                if (i < actorsList.size() - 1) {
                    actors += ",";
                }
            }
            String movieCD = (String) movie.get("movieCd");

            String runtime = (String) detailList.get("showTm");

            String movieNm = (String) detailList.get("movieNm");

            ArrayList<Map> genres = (ArrayList<Map>) detailList.get("genres");
            String genre = (String) genres.get(0).get("genreNm");

            String releaseDate = (String) detailList.get("openDt");

            ArrayList<Map> audits = (ArrayList<Map>) detailList.get("audits");
            String viewingRating = (String) audits.get(0).get("watchGradeNm");

            ArrayList<Map> directors = (ArrayList<Map>) detailList.get("directors");
            String director = "";
            if (!directors.isEmpty()) {
                director = (String) directors.get(0).get("peopleNm");
            }

            ArrayList<Map> nations = (ArrayList<Map>) detailList.get("nations");
            String nationNm = (String) nations.get(0).get("nationNm");


            this.movieService.findMovieList(movieCD, movieNm, actors, runtime, genre, releaseDate, viewingRating, director, nationNm);
            rData.put("releaseDateAndNationNm", releaseDate + nationNm);



        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("statusCode", "999");
            result.put("body", "excpetion오류");
            System.out.println(e.toString());
        }
        return rData;
    }
}
