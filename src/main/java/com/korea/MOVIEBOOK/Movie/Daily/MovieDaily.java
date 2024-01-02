package com.korea.MOVIEBOOK.Movie.Daily;

import com.korea.MOVIEBOOK.Movie.Movie.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class MovieDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;               // 조회 일자        - LocalDate

    private Long rank;                 // 순위             - 영화 진흥원 API

    @OneToOne(mappedBy = "moviedaily")
    private Movie movie;

}
