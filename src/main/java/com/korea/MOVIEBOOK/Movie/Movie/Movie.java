package com.korea.MOVIEBOOK.Movie.Movie;

import com.korea.MOVIEBOOK.Movie.Daily.MovieDaily;
import com.korea.MOVIEBOOK.Movie.Weekly.MovieWeekly;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToOne
    private MovieDaily moviedaily;

    @OneToOne
    private MovieWeekly movieweekly;

}