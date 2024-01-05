package com.korea.MOVIEBOOK.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;

    private void getAPI(String url, String command, Boolean isNew) {
        //  기본url과 명령어를 넣으면 api를 사용해서 이미 db에있는지 체크하고 없다면 db에 저장해주는 메서드.
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + command).build();

            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);

            ArrayList<Map<String, Object>> bookList = (ArrayList<Map<String, Object>>) resultMap.getBody().get("item");
            for (Map<String, Object> bookData : bookList) {
                if (checkDuplicate((String) bookData.get("isbn"))) {
                    saveBook(bookData, isNew);
                    System.out.println("============================= 책 추가됨 =============================");
                } else if (!checkDuplicate((String) bookData.get("isbn")) &&
                        !bookRepository.findByIsbn((String) bookData.get("isbn")).getAddDate().equals(LocalDate.now())) {
                    updateBook(bookData, isNew);
                    System.out.println("============================= 책 업데이트 됨 =============================");
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 예외 처리
            e.printStackTrace();
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    public void getBook(String ketWord) {
        String url = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx?ttbkey=ttbdlrjsrn81027001";
        String command = "&Query=" + ketWord + "&QueryType=Keyword&MaxResults=20&start=1&SearchTarget=Book&output=JS&Version=20131101";
    }
    public void getBestSeller() {
        String url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=ttbdlrjsrn81027001";
        String command = "&QueryType=Bestseller&MaxResults=25&start=1&Cover=Big&SearchTarget=Book&output=JS&Version=20131101";
        getAPI(url, command, false);
        System.out.println("======================== 베스트셀러도서추가 ========================");
    }
    public void getNewSpecialBook() {
        String url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=ttbdlrjsrn81027001";
        String command = "&QueryType=ItemNewSpecial&MaxResults=25&start=1&Cover=Big&SearchTarget=Book&output=JS&Version=20131101";
        getAPI(url, command, true);
        System.out.println("======================== 주목할만한신간도서추가 ========================");
    }

    public List<Book> getBestSellerList() {
        //  db에 있는 오늘 추가된 책들 중 bestSeller들만 추려서 List를 리턴하는 함수
        List<Book> bookList = bookRepository.findByAddDate(LocalDate.now());
        if (bookList.isEmpty()) {
            getBestSeller();
        }
        bookList = bookRepository.findByAddDate(LocalDate.now());
        List<Book> bestSellerList = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getBestRank() != null) {
                bestSellerList.add(book);
            }
        }
        return bestSellerList;
    }


//    여기서부터 수정해야함 여기 bookList를 만드는데 AddDate로해서 신간도서를 추가안하게됨.
    public List<Book> getNewSpecialBookList() {
        //  db에 있는 책들중 NewSpecialBook 들만 추려서 List를 리턴하는 함수
        List<Book> newSpecialBookList = bookRepository.findByIsNewBookAndAddDate(true, LocalDate.now());
        if (newSpecialBookList.isEmpty()) {
            getNewSpecialBook();
        }
        return newSpecialBookList;
    }


    private Boolean checkDuplicate(String isbn) {
        //  이미 DB에 저장되어있는 책인지 확인하는 함수
        List<Book> bookList = bookRepository.findAll();
        boolean answer = true;
        for (Book book : bookList) {
            if (book.getIsbn().equals(isbn)) {
                answer = false;
                break;
            }
        }
        return answer;
    }
    private void saveBook(Map<String, Object> bookData, Boolean isNew) {
        //  book정보를 db에 저장하는 함수
        Book book = new Book();
        book.setTitle((String) bookData.get("title"));
        book.setAuthor((String) bookData.get("author"));
        book.setDescription((String) bookData.get("description"));
        book.setIsbn((String) bookData.get("isbn"));
        book.setIsbn13((String) bookData.get("isbn13"));
        book.setCover((String) bookData.get("cover"));
        book.setPublisher((String) bookData.get("publisher"));
        book.setPricestandard((Integer) bookData.get("priceStandard"));
        book.setBestRank((Integer) bookData.get("bestRank"));
        book.setPubdate(getLocalDate(bookData.get("pubDate")));
        book.setAddDate(LocalDate.now());
        book.setIsNewBook(isNew);
        bookRepository.save(book);
    }

    private void updateBook(Map<String, Object> bookData, Boolean isNew) {
        //  book정보를 오늘자 정보로 db에 업데이트 하는 함수
        Book book = bookRepository.findByIsbn((String) bookData.get("isbn"));
        book.setTitle((String) bookData.get("title"));
        book.setAuthor((String) bookData.get("author"));
        book.setDescription((String) bookData.get("description"));
        book.setIsbn((String) bookData.get("isbn"));
        book.setIsbn13((String) bookData.get("isbn13"));
        book.setCover((String) bookData.get("cover"));
        book.setPublisher((String) bookData.get("publisher"));
        book.setPricestandard((Integer) bookData.get("priceStandard"));
        book.setBestRank((Integer) bookData.get("bestRank"));
        book.setPubdate(getLocalDate(bookData.get("pubDate")));
        book.setAddDate(LocalDate.now());
        book.setIsNewBook(isNew);
        bookRepository.save(book);
    }
    private BookDTO createBookDTO(Map<String, Object> bookData) {
        try {
            return BookDTO.builder()
                    .title((String) bookData.get("title"))
                    .author((String) bookData.get("author"))
                    .description((String) bookData.get("description"))
                    .isbn((String) bookData.get("isbn"))
                    .isbn13((String) bookData.get("isbn13"))
                    .cover((String) bookData.get("cover"))
                    .publisher((String) bookData.get("publisher"))
                    .priceStandard((Integer) bookData.get("priceStandard"))
                    .bestRank((Integer) bookData.get("bestRank"))
                    .pubDate(getLocalDate(bookData.get("pubDate")))
                    .build();
        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return null;
        }
    }

    private LocalDate getLocalDate(Object dateObj) {
        //  String으로 받은 날짜 데이터를 LocalDate타입으로 변경해주는 함수
        String dateString = (String) dateObj;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate pubDate = LocalDate.parse(dateString, formatter);
        return pubDate;
    }
    public Book findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }
}
