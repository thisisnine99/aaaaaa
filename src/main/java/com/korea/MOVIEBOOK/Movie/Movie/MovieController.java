package com.korea.MOVIEBOOK.Movie.Movie;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDailyService;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeeklyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Controller
public class MovieController {
    private final MovieDailyService movieDailyService;
    private final MovieWeeklyService movieWeeklyService;

    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    String date = yesterday.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    LocalDateTime weeksago = LocalDateTime.now().minusDays(7);
    String weeks = weeksago.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);

    // LocalDate를 Date로 변환
    Date oneWeekAgoDate = Date.from(oneWeekAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
    String weekInfo = getCurrentWeekOfMonth(oneWeekAgoDate);


    @GetMapping("movie")
    public String movie(Model model) throws ParseException {
        List<MovieDTO> movieDailyDTO = movieDailyService.movieDailyList(date);
        List<List<MovieDTO>> movieListList = new ArrayList<>();

        Integer startIndex = 0;
        Integer endIndex = 5;
        for (int i = 0; i < movieDailyDTO.size() / 5; i++) {
            movieListList.add(movieDailyDTO.subList(startIndex, Math.min(endIndex, movieDailyDTO.size())));
            startIndex += 5;
            endIndex += 5;
        }
        for (List<MovieDTO> movieList : movieListList) {
            movieList.sort(new MovieDTOComparator());
        }
        //  데일리리스트

        List<MovieDTO> movieWeeklyDTO = this.movieWeeklyService.movieWeeklyList(weeks);
        List<List<MovieDTO>> movieWeeklyListList = new ArrayList<>();

        startIndex = 0;
        endIndex = 5;
        for (int i = 0; i < movieWeeklyDTO.size() / 5; i++) {
            movieWeeklyListList.add(movieWeeklyDTO.subList(startIndex, Math.min(endIndex, movieWeeklyDTO.size())));
            startIndex += 5;
            endIndex += 5;
        }
        //  위클리리스트

        System.out.println("=============== 확인용");
        movieDailyService.test();
        System.out.println("=============== 확인용");

        model.addAttribute("movieDailyDate", date);
        model.addAttribute("movieListList", movieListList);
        model.addAttribute("movieWeeklyDate", weekInfo);
        model.addAttribute("movieWeeklyListList", movieWeeklyListList);

        return "Movie/movie";
    }

    @PostMapping("movie/detail")
    public String movieDetail(Model model, String movieCD) {
//        Movie movie = movieService1.findMovieByCD(movieCD);
//
//        Integer runtime = Integer.valueOf(movie.getRuntime());
//        Integer hour = (int) Math.floor((double) runtime / 60);
//        Integer minutes = runtime % 60;
//        String movieruntime = String.valueOf(hour) + "시간" + String.valueOf(minutes) + "분";
//
//        model.addAttribute("movieDailyDetail", movie);
//        model.addAttribute("movieruntime", movieruntime);

        return "Movie/movie_detail";
    }

    //
//    public void movieDailySize(List<Map> failedMovieList) {
//        if (failedMovieList != null && !failedMovieList.isEmpty()) {
//            List<Map> failedMoiveList = movieDailyService.saveDailyMovieDataByAPI(failedMovieList, date);
//            movieDailySize(failedMoiveList);
//        }
//    }
//
//    public void movieWeeklySize(List<Map> failedMovieList) throws ParseException {
//        if (failedMovieList != null && !failedMovieList.isEmpty()) {
//            List<Map> failedMoiveList = movieWeeklyService.saveWeeklyMovieDataByAPI(failedMovieList, weeks);
//            movieDailySize(failedMoiveList);
//        }
//    }

    public static String getCurrentWeekOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1; // calendar에서의 월은 0부터 시작
        int day = calendar.get(Calendar.DATE);

        // 한 주의 시작은 월요일이고, 첫 주에 4일이 포함되어있어야 첫 주 취급 (목/금/토/일)
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);

        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        // 첫 주에 해당하지 않는 주의 경우 전 달 마지막 주차로 계산
        if (weekOfMonth == 0) {
            calendar.add(Calendar.DATE, -day); // 전 달의 마지막 날 기준
            return getCurrentWeekOfMonth(calendar.getTime());
        }

        // 마지막 주차의 경우
        if (weekOfMonth == calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)) {
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE)); // 이번 달의 마지막 날
            int lastDaysDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 이번 달 마지막 날의 요일

            // 마지막 날이 월~수 사이이면 다음달 1주차로 계산
            if (lastDaysDayOfWeek >= Calendar.MONDAY && lastDaysDayOfWeek <= Calendar.WEDNESDAY) {
                calendar.add(Calendar.DATE, 1); // 마지막 날 + 1일 => 다음달 1일
                return getCurrentWeekOfMonth(calendar.getTime());
            }
        }

        return month + "월 " + weekOfMonth + "주차";
    }

    class MovieDTOComparator implements Comparator<MovieDTO> {
        @Override
        public int compare(MovieDTO m1, MovieDTO m2) {
            // movieDailyRank 기준으로 정렬
            return m1.getDailyRank().compareTo(m2.getDailyRank());
        }
    }

}

