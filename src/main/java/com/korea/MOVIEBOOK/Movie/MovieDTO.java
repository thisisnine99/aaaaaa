package com.korea.MOVIEBOOK.Movie;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MovieDTO {
    private String year;

    private String week;

    private String date;

    private Long dailyRank;

    private Long weeklyRank;

    private String movieCode;           // 영화 번호        - 영화 진흥원 API

    private String title;               // 영화 제목        - 영화 진흥원 API

    private String director;            // 영화 감독        - 영화 진흥원 API

    @Column(columnDefinition = "text")
    private String actor;               // 배우명 (역할)    - 영화 진흥원 API

    private String runtime;             // 상영 시간        - 영화 진흥원 API

    @Column(columnDefinition = "text")
    private String plot;                // 줄거리           - KMDb API

    private String genre;               // 장르             - 영화 진흥원 API

    private String releaseDate;         // 개봉 일자        - 영화 진흥원 API

    private String company;             // 제작사           - KMDb API

    private String nations;             // 제작 국가        - 영화 진흥원 API

    private Long audiAcc;               // 누적 관객수      - 영화 진흥원 API

    private String viewingRating;       // 관람 등급        - 영화 진흥원 API

    private String imageUrl;            // 포스터 URL       - KMDb API

    public boolean getYear(String year) {
        if(this.year == null){
            return false;
        }
        return this.year.equals(year);
    }

    public boolean getWeek(String week) {
        if(this.week == null){
            return false;
        }
        return this.week.equals(week);
    }

}
