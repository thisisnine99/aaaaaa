package com.korea.MOVIEBOOK.book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByIsbn13(String isbn13);
    Book findByIsbn(String isbn);
    Book findByTitle(String title);
    List<Book> findByAddDate(LocalDate addDate);
    List<Book> findByBestRankAndAddDate(Integer bestRank,LocalDate addDate);
    List<Book> findByIsNewBookAndAddDate(Boolean isNew, LocalDate addDate);
}
