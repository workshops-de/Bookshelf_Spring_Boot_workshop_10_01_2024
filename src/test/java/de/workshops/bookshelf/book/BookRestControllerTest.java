package de.workshops.bookshelf.book;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookRestControllerTest {

    @Mock
    BookService service;

    @InjectMocks
    private BookRestController controllerUnderTest;

    @Test
    void shouldGetAllBooks() {
        when(service.getAllBooks()).thenReturn(List.of(new Book(), new Book(), new Book()));

        final var allBooks = controllerUnderTest.getAllBooks();

        assertThat(allBooks).hasSize(3);
    }

}