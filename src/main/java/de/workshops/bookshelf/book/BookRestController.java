package de.workshops.bookshelf.book;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/book")
@Validated
public class BookRestController {
    private final ObjectMapper mapper;
    private final ResourceLoader resourceLoader;

    private List<Book> books;

    public BookRestController(ObjectMapper mapper, ResourceLoader resourceLoader) {
        this.mapper = mapper;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        final var resource = resourceLoader.getResource("classpath:books.json");
        this.books = mapper.readValue(resource.getInputStream(), new TypeReference<>() {
        });
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return books;
    }

    @GetMapping(path = "/{isbn}")
    public Book getBookByIsbn(@PathVariable(name = "isbn") String isbnValue) throws BookException {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbnValue))
                .findFirst().orElseThrow(() ->new BookException("Could not find book for ISBN"));
    }

    @GetMapping(params = "author")
    public ResponseEntity<Book> getBookByAuthor(@RequestParam @NotBlank @Size(min = 3) String author) {
        final var b = books.stream()
                .filter(book -> book.getAuthor().contains(author))
                .findFirst().orElse(null);

        if (b == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(b);
    }

    @PostMapping("/search")
    List<Book> searchBooks(@RequestBody @Valid BookSearchRequest bookSearchRequest) {
        return books.stream()
                .filter(book -> book.getAuthor().contains(bookSearchRequest.author())
                                || book.getTitle().startsWith(bookSearchRequest.title()))
                .toList();
    }

    @ExceptionHandler(BookException.class)
    ProblemDetail handleBookException(BookException e) {
        var problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.I_AM_A_TEAPOT, e.getMessage());
        problemDetail.setTitle("Book not found.");
        problemDetail.setType(URI.create("http://localhost:8080/book_exception.html"));
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
