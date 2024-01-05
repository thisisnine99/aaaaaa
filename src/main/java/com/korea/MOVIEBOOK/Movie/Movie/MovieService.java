package com.korea.MOVIEBOOK.Movie.Movie;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {

    final private MovieRepository movieRepository;
    final private MovieAPI movieAPI;

    public void movieAdd(List<Map> boxOfficeList) {
        for (Map<String, Object> boxoffice : boxOfficeList) {

            Map<String, Object> detail = movieAPI.getMovieDetail((String) boxoffice.get("movieCd"));

            ArrayList<Map> actorsList = (ArrayList<Map>) detail.get("actors");
            String actors = "";
            for (int i = 0; i < actorsList.size(); i++) {
                String actorText = (String) actorsList.get(i).get("peopleNm");
                String cast = (String) actorsList.get(i).get("cast");
                actors += actorText + "(" + cast + ")";

                if (i < actorsList.size() - 1) {
                    actors += ",";
                }
            }

            String movieCD = (String) detail.get("movieCd");

            String runtime = (String) detail.get("showTm");

            String movieNm = (String) detail.get("movieNm");

            ArrayList<Map> genres = (ArrayList<Map>) detail.get("genres");
            String genre = (String) genres.get(0).get("genreNm");

            String releaseDate = (String) detail.get("openDt");

            ArrayList<Map> audits = (ArrayList<Map>) detail.get("audits");
            String viewingRating = (String) audits.get(0).get("watchGradeNm");

            ArrayList<Map> directors = (ArrayList<Map>) detail.get("directors");
            String director = "";
            if (!directors.isEmpty()) {
                director = (String) directors.get(0).get("peopleNm");
            }

            ArrayList<Map> nations = (ArrayList<Map>) detail.get("nations");
            String nationNm = (String) nations.get(0).get("nationNm");

            findMovieList(movieCD, movieNm, actors, runtime, genre, releaseDate, viewingRating, director, nationNm);
        }
    }

    public void findMovieList(String movieCD, String movieNm, String actorText, String runtime, String genre, String releaseDate,
                              String viewingRating, String director, String nations) {
        Movie movie = this.movieRepository.findByTitleAndNationsAndReleaseDate(movieNm, nations, releaseDate);
        if (movie == null) {
            addDetail(movieCD, movieNm, actorText, runtime, genre, releaseDate, viewingRating, director, nations);
        }
    }

    public void addDetail(String movieCD, String movieNm, String actorText, String runtime, String genre,
                          String releaseDate, String viewingRating, String director, String nations) {
        Movie movie = new Movie();
        movie.setMovieCode(movieCD);
        movie.setTitle(movieNm);
        movie.setActor(actorText);
        movie.setRuntime(runtime);
        movie.setGenre(genre);
        movie.setReleaseDate(releaseDate);
        movie.setViewingRating(viewingRating);
        movie.setDirector(director);
        movie.setNations(nations);
        movieRepository.save(movie);
    }
}
