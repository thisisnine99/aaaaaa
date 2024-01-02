package com.korea.MOVIEBOOK.Movie.Weekly;

import com.korea.MOVIEBOOK.Movie.Movie.Movie;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MovieWeekly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String year;                // 조회 일자(년)     - LocalDate

    private String week;                // 조회 일자(주)     - LocalDate

    private Long rank;                  // 순위             - 영화 진흥원 API

    @OneToOne(mappedBy = "movieweekly")
    private Movie movie;
}
