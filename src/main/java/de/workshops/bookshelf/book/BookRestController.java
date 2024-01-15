package de.workshops.bookshelf.book;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/book")
@Validated
public class BookRestController {
    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return service.getAllBooks();
    }

    @GetMapping(path = "/{isbn}")
    public Book getBookByIsbn(@PathVariable(name = "isbn") String isbnValue) throws BookException {
        return service.getBookByIsbn(isbnValue);
    }

    @GetMapping(params = "author")
    public ResponseEntity<Book> getBookByAuthor(@RequestParam(required = false) @Size(min = 3) String author) {
        final var b = service.getBookByAuthor(author);

        if (b == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(b);
    }

    @PostMapping("/search")
    List<Book> searchBooks(@RequestBody @Valid BookSearchRequest bookSearchRequest) {
        return service.searchBooks(bookSearchRequest);
    }

    @PostMapping
    Book createBook(@RequestBody @Valid Book newBook) {
        return service.createBook(newBook);
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
