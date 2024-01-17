package de.workshops.bookshelf.book;

import de.workshops.bookshelf.SpringSecurityTestConfiguration;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SpringSecurityTestConfiguration.class)
@ActiveProfiles("test")
class BookRestControllerRestClientTest {

    @LocalServerPort
    private int port;

    @MockBean
    private BookService bookService;

    private RestClient restClient;

    @PostConstruct
    void initRestClient() {
        final var encodeToString = Base64.getEncoder().encodeToString("user:password".getBytes(StandardCharsets.UTF_8));
        restClient = RestClient.builder()
                .baseUrl("http://localhost:%d/book".formatted(port))
                .defaultHeader("Authorization", "Basic " + encodeToString)
                .build();
    }

    @Test
    void shouldGetAllBooks() {
        when(bookService.getAllBooks()).thenReturn(List.of(new Book(), new Book(), new Book()));

        final var responseEntity = restClient
                .get()
                .retrieve()
                .toEntity(List.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).hasSize(3);
    }

    @Test
    void shouldGetBookByIsbn_OK() throws BookException {
        final var isbn = "123456789";

        when(bookService.getBookByIsbn(isbn)).thenAnswer(invocationOnMock -> {
            var book = new Book();
            book.setIsbn(isbn);
            return book;
        });

        final var responseEntity = restClient
                .get()
                .uri("/%s".formatted(isbn))
                .retrieve()
                .toEntity(Book.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isNotNull()
                .hasFieldOrPropertyWithValue("isbn", isbn);
    }

    @Test
    void shouldGetBookByIsbn_I_AM_A_TEAPOT() throws BookException {
        doThrow(BookException.class).when(bookService).getBookByIsbn(anyString());

        final var exception = catchThrowableOfType(() -> restClient
                .get()
                .uri("/123456789")
                .retrieve()
                .toEntity(Book.class), HttpClientErrorException.class);

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.I_AM_A_TEAPOT);

        final var problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
        assertThat(problemDetail).isNotNull()
                .hasFieldOrPropertyWithValue("title", "Book not found.");
    }

    @Test
    void shouldGetBookByAuthor_BAD_REQUEST() {
        final var exception = catchThrowableOfType(() -> restClient
                .get()
                .uri("?author=Ro")
                .retrieve()
                .toEntity(Book.class), HttpClientErrorException.class);

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldGetBookByAuthor_BAD_REQUEST_differentWayOfAssertJAssertion() {
        assertThatExceptionOfType(HttpClientErrorException.class)
                .isThrownBy(() -> restClient
                        .get()
                        .uri("?author=Ro")
                        .retrieve()
                        .toEntity(Book.class))
                .satisfies(e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
}